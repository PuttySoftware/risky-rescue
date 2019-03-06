/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.game;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.riskyrescue.Application;
import com.puttysoftware.riskyrescue.RiskyRescue;
import com.puttysoftware.riskyrescue.Support;
import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.assets.MusicConstants;
import com.puttysoftware.riskyrescue.assets.MusicManager;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.creatures.party.Party;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.game.scripts.InternalScriptRunner;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.riskyrescue.map.objects.Empty;
import com.puttysoftware.riskyrescue.map.objects.InfiniteRecursionException;
import com.puttysoftware.riskyrescue.map.objects.MapObject;
import com.puttysoftware.riskyrescue.map.objects.Player;
import com.puttysoftware.riskyrescue.map.objects.Tile;
import com.puttysoftware.riskyrescue.map.objects.Wall;
import com.puttysoftware.riskyrescue.prefs.PreferencesManager;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptArea;

public class GameLogic {
    // Fields
    private MapObject savedMapObject;
    private boolean savedGameFlag;
    private final ScoreTracker st;
    private boolean stateChanged;
    private GameGUI gameGUI;

    // Constructors
    public GameLogic() {
        this.gameGUI = new GameGUI();
        this.st = new ScoreTracker();
        this.savedMapObject = new Empty();
        this.savedGameFlag = false;
        this.stateChanged = true;
    }

    // Methods
    public boolean newGame() {
        JFrame owner = RiskyRescue.getApplication().getOutputFrame();
        if (this.savedGameFlag) {
            if (PartyManager.getParty() != null) {
                return true;
            } else {
                return PartyManager.createParty(owner);
            }
        } else {
            return PartyManager.createParty(owner);
        }
    }

    private GameViewingWindowManager getViewManager() {
        return this.gameGUI.getViewManager();
    }

    public void addToScore(long points) {
        this.st.addToScore(points);
    }

    public void showCurrentScore() {
        this.st.showCurrentScore();
    }

    private static void fireStepActions(int x, int y, int z) {
        RiskyRescue.getApplication().getScenarioManager().getMap()
                .updateVisibleSquares(x, y, z);
    }

    public void updateStats() {
        this.gameGUI.updateStats();
    }

    public void stateChanged() {
        this.stateChanged = true;
    }

    public void setSavedGameFlag(boolean value) {
        this.savedGameFlag = value;
    }

    public void setStatusMessage(final String msg) {
        this.gameGUI.setStatusMessage(msg);
    }

    public void updatePositionRelative(int x, int y, int z) {
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        boolean redrawsSuspended = false;
        int px = m.getPlayerLocationX();
        int py = m.getPlayerLocationY();
        int pz = m.getPlayerLocationZ();
        Application app = RiskyRescue.getApplication();
        boolean proceed = false;
        MapObject o = null;
        MapObject groundInto = new Empty();
        MapObject below = null;
        MapObject nextBelow = null;
        MapObject nextAbove = null;
        try {
            try {
                o = m.getCell(px + x, py + y, pz + z,
                        MapConstants.LAYER_OBJECT);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                o = new Empty();
            }
            try {
                below = m.getCell(px, py, pz, MapConstants.LAYER_GROUND);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                below = new Empty();
            }
            try {
                nextBelow = m.getCell(px + x, py + y, pz + z,
                        MapConstants.LAYER_GROUND);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                nextBelow = new Empty();
            }
            try {
                nextAbove = m.getCell(px + x, py + y, pz + z,
                        MapConstants.LAYER_OBJECT);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                nextAbove = new Wall();
            }
            try {
                proceed = o.preMoveCheck(true, px + x, py + y, pz + z, m);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                proceed = true;
            } catch (final InfiniteRecursionException ir) {
                proceed = false;
            }
        } catch (NullPointerException np) {
            proceed = false;
            o = new Empty();
        }
        if (proceed) {
            m.savePlayerLocation();
            this.getViewManager().saveViewingWindow();
            try {
                if (GameLogic.checkSolid(pz + z, this.savedMapObject, below,
                        nextBelow, nextAbove)) {
                    m.setCell(this.savedMapObject, px, py, pz,
                            MapConstants.LAYER_OBJECT);
                    m.offsetPlayerLocationX(x);
                    m.offsetPlayerLocationY(y);
                    m.offsetPlayerLocationZ(z);
                    px += x;
                    py += y;
                    pz += z;
                    this.getViewManager().offsetViewingWindowLocationX(y);
                    this.getViewManager().offsetViewingWindowLocationY(x);
                    this.savedMapObject = m.getCell(px, py, pz,
                            MapConstants.LAYER_OBJECT);
                    m.setCell(new Player(), px, py, pz,
                            MapConstants.LAYER_OBJECT);
                    app.getScenarioManager().setDirty(true);
                    this.redrawMap();
                    groundInto = m.getCell(px, py, pz,
                            MapConstants.LAYER_GROUND);
                    if (groundInto.overridesDefaultPostMove()) {
                        InternalScriptRunner.runScript(groundInto
                                .getPostMoveScript(false, px, py, pz));
                        if (!(this.savedMapObject instanceof Empty)) {
                            InternalScriptRunner.runScript(this.savedMapObject
                                    .getPostMoveScript(false, px, py, pz));
                        }
                    } else {
                        InternalScriptRunner.runScript(this.savedMapObject
                                .getPostMoveScript(false, px, py, pz));
                    }
                } else {
                    // Move failed - object is solid in that direction
                    GameLogic.fireMoveFailedActions(px + x, py + y, pz + z,
                            this.savedMapObject, below, nextBelow, nextAbove);
                    proceed = false;
                }
            } catch (final ArrayIndexOutOfBoundsException ae) {
                this.getViewManager().restoreViewingWindow();
                m.restorePlayerLocation();
                m.setCell(new Player(), m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                // Move failed - attempted to go outside the map
                RiskyRescue.getApplication().showMessage("Can't go that way");
                o = new Empty();
                proceed = false;
            }
        } else {
            // Move failed - pre-move check failed
            InternalScriptRunner.runScript(MapObject.getMoveFailedScript(false,
                    px + x, py + y, pz + z));
            proceed = false;
        }
        if (redrawsSuspended) {
            // Redraw post-suspend
            this.redrawMap();
            redrawsSuspended = false;
        }
        // Post-move actions
        ArrayList<InternalScriptArea> areaScripts = app.getScenarioManager()
                .getMap().getScriptAreasAtPoint(new Point(px, py), pz);
        for (InternalScriptArea isa : areaScripts) {
            InternalScriptRunner.runScript(isa);
        }
        GameLogic.fireStepActions(px, py, pz);
        this.updateStats();
        this.checkGameOver();
    }

    public void updatePositionRelativeNoEvents(int x, int y, int z) {
        boolean redrawsSuspended = false;
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        int px = m.getPlayerLocationX();
        int py = m.getPlayerLocationY();
        int pz = m.getPlayerLocationZ();
        Application app = RiskyRescue.getApplication();
        boolean proceed = false;
        MapObject o = null;
        MapObject below = null;
        MapObject nextBelow = null;
        MapObject nextAbove = null;
        try {
            try {
                o = m.getCell(px + x, py + y, pz + z,
                        MapConstants.LAYER_OBJECT);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                o = new Empty();
            }
            try {
                below = m.getCell(px, py, pz, MapConstants.LAYER_GROUND);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                below = new Empty();
            }
            try {
                nextBelow = m.getCell(px + x, py + y, pz + z,
                        MapConstants.LAYER_GROUND);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                nextBelow = new Empty();
            }
            try {
                nextAbove = m.getCell(px + x, py + y, pz + z,
                        MapConstants.LAYER_OBJECT);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                nextAbove = new Wall();
            }
            try {
                proceed = o.preMoveCheck(true, px + x, py + y, pz + z, m);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                proceed = true;
            } catch (final InfiniteRecursionException ir) {
                proceed = false;
            }
        } catch (NullPointerException np) {
            proceed = false;
            o = new Empty();
        }
        if (proceed) {
            m.savePlayerLocation();
            this.getViewManager().saveViewingWindow();
            try {
                if (GameLogic.checkSolid(z + pz, this.savedMapObject, below,
                        nextBelow, nextAbove)) {
                    m.setCell(this.savedMapObject, px, py, pz,
                            MapConstants.LAYER_OBJECT);
                    m.offsetPlayerLocationX(x);
                    m.offsetPlayerLocationY(y);
                    m.offsetPlayerLocationZ(z);
                    px += x;
                    py += y;
                    pz += z;
                    this.getViewManager().offsetViewingWindowLocationX(y);
                    this.getViewManager().offsetViewingWindowLocationY(x);
                    this.savedMapObject = m.getCell(px, py, pz,
                            MapConstants.LAYER_OBJECT);
                    m.setCell(new Player(), px, py, pz,
                            MapConstants.LAYER_OBJECT);
                    app.getScenarioManager().setDirty(true);
                    this.redrawMap();
                } else {
                    // Move failed - object is solid in that direction
                    GameLogic.fireMoveFailedActions(px + x, py + y, pz + z,
                            this.savedMapObject, below, nextBelow, nextAbove);
                    proceed = false;
                }
            } catch (final ArrayIndexOutOfBoundsException ae) {
                this.getViewManager().restoreViewingWindow();
                m.restorePlayerLocation();
                m.setCell(new Player(), m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                // Move failed - attempted to go outside the map
                RiskyRescue.getApplication().showMessage("Can't go that way");
                o = new Empty();
                proceed = false;
            }
        } else {
            // Move failed - pre-move check failed
            InternalScriptRunner.runScript(MapObject.getMoveFailedScript(false,
                    px + x, py + y, pz + z));
            proceed = false;
        }
        if (redrawsSuspended) {
            // Redraw post-suspend
            this.redrawMap();
            redrawsSuspended = false;
        }
        this.updateStats();
        this.checkGameOver();
    }

    private void saveSavedMapObject() {
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        int px = m.getPlayerLocationX();
        int py = m.getPlayerLocationY();
        int pz = m.getPlayerLocationZ();
        Player player = (Player) m.getCell(px, py, pz,
                MapConstants.LAYER_OBJECT);
        player.setSavedObject(this.savedMapObject);
    }

    private void restoreSavedMapObject() {
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        int px = m.getPlayerLocationX();
        int py = m.getPlayerLocationY();
        int pz = m.getPlayerLocationZ();
        Player player = (Player) m.getCell(px, py, pz,
                MapConstants.LAYER_OBJECT);
        this.savedMapObject = player.getSavedObject();
    }

    private static boolean checkSolid(final int z, final MapObject inside,
            final MapObject below, final MapObject nextBelow,
            final MapObject nextAbove) {
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        boolean insideSolid = inside.isConditionallySolid(m, z);
        boolean belowSolid = below.isConditionallySolid(m, z);
        boolean nextBelowSolid = nextBelow.isConditionallySolid(m, z);
        boolean nextAboveSolid = nextAbove.isConditionallySolid(m, z);
        return !(insideSolid || belowSolid || nextBelowSolid || nextAboveSolid);
    }

    private static void fireMoveFailedActions(final int x, final int y,
            final int z, final MapObject inside, final MapObject below,
            final MapObject nextBelow, final MapObject nextAbove) {
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        boolean insideSolid = inside.isConditionallySolid(m, z);
        boolean belowSolid = below.isConditionallySolid(m, z);
        boolean nextBelowSolid = nextBelow.isConditionallySolid(m, z);
        boolean nextAboveSolid = nextAbove.isConditionallySolid(m, z);
        if (insideSolid) {
            InternalScriptRunner
                    .runScript(MapObject.getMoveFailedScript(false, x, y, z));
        }
        if (belowSolid) {
            InternalScriptRunner
                    .runScript(MapObject.getMoveFailedScript(false, x, y, z));
        }
        if (nextBelowSolid) {
            InternalScriptRunner
                    .runScript(MapObject.getMoveFailedScript(false, x, y, z));
        }
        if (nextAboveSolid) {
            InternalScriptRunner
                    .runScript(MapObject.getMoveFailedScript(false, x, y, z));
        }
    }

    public void updatePositionAbsolute(final int x, final int y, final int z) {
        Application app = RiskyRescue.getApplication();
        Map m = app.getScenarioManager().getMap();
        try {
            m.getCell(x, y, z, MapConstants.LAYER_OBJECT).preMoveCheck(true, x,
                    y, z, m);
        } catch (final ArrayIndexOutOfBoundsException ae) {
            // Do nothing
        } catch (final NullPointerException np) {
            // Do nothing
        }
        m.savePlayerLocation();
        this.getViewManager().saveViewingWindow();
        try {
            if (!(m.getCell(x, y, z, MapConstants.LAYER_OBJECT)
                    .isConditionallySolid(m, z))) {
                m.setCell(this.savedMapObject, m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                m.setPlayerLocation(x, y, z, 0);
                this.getViewManager()
                        .setViewingWindowLocationX(m.getPlayerLocationY()
                                - GameViewingWindowManager.getOffsetFactor());
                this.getViewManager()
                        .setViewingWindowLocationY(m.getPlayerLocationX()
                                - GameViewingWindowManager.getOffsetFactor());
                this.savedMapObject = m.getCell(m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                m.setCell(new Player(), m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                app.getScenarioManager().setDirty(true);
                InternalScriptRunner.runScript(
                        this.savedMapObject.getPostMoveScript(false, x, y, z));
            }
        } catch (final ArrayIndexOutOfBoundsException ae) {
            m.restorePlayerLocation();
            this.getViewManager().restoreViewingWindow();
            m.setCell(new Player(), m.getPlayerLocationX(),
                    m.getPlayerLocationY(), m.getPlayerLocationZ(),
                    MapConstants.LAYER_OBJECT);
            RiskyRescue.getApplication()
                    .showMessage("Can't go outside the map");
        } catch (final NullPointerException np) {
            m.restorePlayerLocation();
            this.getViewManager().restoreViewingWindow();
            m.setCell(new Player(), m.getPlayerLocationX(),
                    m.getPlayerLocationY(), m.getPlayerLocationZ(),
                    MapConstants.LAYER_OBJECT);
            RiskyRescue.getApplication()
                    .showMessage("Can't go outside the map");
        }
        GameLogic.fireStepActions(m.getPlayerLocationX(),
                m.getPlayerLocationY(), m.getPlayerLocationZ());
        this.redrawMap();
    }

    public void updatePositionAbsoluteNoEvents(final int x, final int y,
            final int z) {
        Application app = RiskyRescue.getApplication();
        Map m = app.getScenarioManager().getMap();
        m.savePlayerLocation();
        this.getViewManager().saveViewingWindow();
        try {
            if (!(m.getCell(x, y, z, MapConstants.LAYER_OBJECT)
                    .isConditionallySolid(m, z))) {
                m.setCell(this.savedMapObject, m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                m.setPlayerLocation(x, y, z, m.getPlayerLocationW());
                this.getViewManager()
                        .setViewingWindowLocationX(m.getPlayerLocationY()
                                - GameViewingWindowManager.getOffsetFactor());
                this.getViewManager()
                        .setViewingWindowLocationY(m.getPlayerLocationX()
                                - GameViewingWindowManager.getOffsetFactor());
                this.savedMapObject = m.getCell(m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                m.setCell(new Player(), m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT);
                app.getScenarioManager().setDirty(true);
            }
        } catch (final ArrayIndexOutOfBoundsException ae) {
            m.restorePlayerLocation();
            this.getViewManager().restoreViewingWindow();
            m.setCell(new Player(), m.getPlayerLocationX(),
                    m.getPlayerLocationY(), m.getPlayerLocationZ(),
                    MapConstants.LAYER_OBJECT);
            RiskyRescue.getApplication()
                    .showMessage("Can't go outside the map");
        } catch (final NullPointerException np) {
            m.restorePlayerLocation();
            this.getViewManager().restoreViewingWindow();
            m.setCell(new Player(), m.getPlayerLocationX(),
                    m.getPlayerLocationY(), m.getPlayerLocationZ(),
                    MapConstants.LAYER_OBJECT);
            RiskyRescue.getApplication()
                    .showMessage("Can't go outside the map");
        }
        RiskyRescue.getApplication().getScenarioManager().getMap()
                .updateVisibleSquares(x, y, z);
        this.redrawMap();
    }

    public void goToLevelRelative(final int level) {
        Application app = RiskyRescue.getApplication();
        Map m = app.getScenarioManager().getMap();
        final boolean levelExists = m.doesLevelExistOffset(level);
        if (!levelExists && m.isLevelOffsetValid(level)) {
            // Create the level
            this.saveSavedMapObject();
            m.addLevel(Support.getGameMapSize(), Support.getGameMapSize(),
                    Support.getGameMapFloorSize());
            m.fillLevelRandomly(new Tile(), new Empty());
            m.save();
            this.restoreSavedMapObject();
        } else if (levelExists && m.isLevelOffsetValid(level)) {
            this.saveSavedMapObject();
            m.switchLevelOffset(level);
            this.restoreSavedMapObject();
        } else {
            // Attempted to leave the dungeon...
            Party party = PartyManager.getParty();
            if (party.getActivePCCount() == 2 && party.isAlive()) {
                // If our buddy is with us and everyone is alive, we win!
                this.victory();
            } else {
                // Our buddy is not with us, or someone in our party is dead
            }
        }
        GameLogic.fireStepActions(m.getPlayerLocationX(),
                m.getPlayerLocationY(), m.getPlayerLocationZ());
        this.redrawMap();
        if (PreferencesManager
                .getMusicEnabled(PreferencesManager.MUSIC_DUNGEON)) {
            MusicManager.stopMusic();
            MusicManager.playMusic(MusicConstants.DUNGEON,
                    m.getPlayerLocationZ());
        }
    }

    public void redrawMap() {
        this.gameGUI.redrawMap();
    }

    public void resetViewingWindow() {
        this.gameGUI.resetViewingWindow();
    }

    public void victory() {
        // Play victory sound
        SoundManager.playSound(SoundConstants.WIN_GAME);
        // Display YOU WIN! message
        CommonDialogs.showDialog("YOU WIN! The game has ended.");
        this.st.commitScore();
        this.exitGame();
    }

    public void exitGame() {
        this.stateChanged = true;
        Application app = RiskyRescue.getApplication();
        // Reset saved game flag
        this.savedGameFlag = false;
        app.getScenarioManager().setDirty(false);
        // Exit game
        this.hideOutput();
        app.getGUIManager().showGUI();
    }

    public void checkGameOver() {
        if (!PartyManager.getParty().isAlive()) {
            this.gameOver();
        }
    }

    private void gameOver() {
        SoundManager.playSound(SoundConstants.DEFEAT);
        CommonDialogs.showDialog("You have died - Game Over!");
        this.st.commitScore();
        this.exitGame();
    }

    public JFrame getOutputFrame() {
        return this.gameGUI.getOutputFrame();
    }

    public void decay() {
        this.savedMapObject = new Empty();
    }

    public void playMap() {
        Map m;
        Application app = RiskyRescue.getApplication();
        app.getGUIManager().hideGUI();
        app.setInGame();
        if (app.getScenarioManager().getLoaded()) {
            this.stateChanged = false;
        }
        if (this.stateChanged) {
            // Initialize only if the map state has changed
            boolean didMapExist = true;
            int currRandom = PreferencesManager.getGeneratorRandomness();
            if (app.getScenarioManager().getMap() == null) {
                didMapExist = false;
            }
            RiskyRescue.newScenario();
            m = new Map();
            app.getScenarioManager().setMap(m);
            m.createMaps();
            m.addLevel(Support.getGameMapSize(), Support.getGameMapSize(),
                    Support.getGameMapFloorSize());
            m.fillLevelRandomly(new Tile(), new Empty());
            m.setPlayerLocationW(0);
            m.save();
            if (didMapExist) {
                m.setGeneratorRandomness(currRandom,
                        RiskyRescue.GENERATOR_RANDOMNESS_MAX);
            }
            this.resetViewingWindow();
            int px = m.getPlayerLocationX();
            int py = m.getPlayerLocationY();
            int pz = m.getPlayerLocationZ();
            m.updateVisibleSquares(px, py, pz);
            this.savedMapObject = new Empty();
            this.stateChanged = false;
        }
        // Make sure message area is attached to the border pane
        this.gameGUI.updateGameGUI();
        this.showOutput();
        this.checkGameOver();
        this.redrawMap();
    }

    public void showOutput() {
        this.gameGUI.showOutput(RiskyRescue.getApplication()
                .getScenarioManager().getMap().getPlayerLocationZ());
    }

    public void hideOutput() {
        this.gameGUI.hideOutput();
    }
}
