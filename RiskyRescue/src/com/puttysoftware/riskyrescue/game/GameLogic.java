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
import com.puttysoftware.riskyrescue.assets.MusicConstants;
import com.puttysoftware.riskyrescue.assets.MusicManager;
import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.creatures.party.Party;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.game.scripts.InternalScriptRunner;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.riskyrescue.map.objects.Empty;
import com.puttysoftware.riskyrescue.map.objects.InfiniteRecursionException;
import com.puttysoftware.riskyrescue.map.objects.MapObject;
import com.puttysoftware.riskyrescue.map.objects.StairsDown;
import com.puttysoftware.riskyrescue.map.objects.StairsUp;
import com.puttysoftware.riskyrescue.map.objects.Tile;
import com.puttysoftware.riskyrescue.map.objects.Wall;
import com.puttysoftware.riskyrescue.prefs.PreferencesManager;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptArea;

public class GameLogic {
    // Fields
    private boolean savedGameFlag;
    private final ScoreTracker st;
    private boolean stateChanged;
    private GameGUI gameGUI;
    private boolean runBattles;

    // Constructors
    public GameLogic() {
        this.gameGUI = new GameGUI();
        this.st = new ScoreTracker();
        this.savedGameFlag = false;
        this.stateChanged = true;
        this.runBattles = true;
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

    public void skipBattlesOnce() {
        this.runBattles = false;
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
        MapObject o = new Empty();
        MapObject groundInto = new Empty();
        MapObject below = null;
        MapObject nextBelow = null;
        MapObject nextAbove = null;
        try {
            o = m.getCell(px + x, py + y, pz + z, MapConstants.LAYER_OBJECT);
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
        if (proceed) {
            m.savePlayerLocation();
            this.getViewManager().saveViewingWindow();
            try {
                if (GameLogic.checkSolid(pz + z, GameLogic.getSavedMapObject(),
                        below, nextBelow, nextAbove)) {
                    m.offsetPlayerLocationX(x);
                    m.offsetPlayerLocationY(y);
                    m.offsetPlayerLocationZ(z);
                    px += x;
                    py += y;
                    pz += z;
                    this.getViewManager().offsetViewingWindowLocationX(y);
                    this.getViewManager().offsetViewingWindowLocationY(x);
                    GameLogic.setSavedMapObject(
                            m.getCell(px, py, pz, MapConstants.LAYER_OBJECT));
                    app.getScenarioManager().setDirty(true);
                    this.redrawMap();
                    groundInto = m.getCell(px, py, pz,
                            MapConstants.LAYER_GROUND);
                    if (groundInto.overridesDefaultPostMove()) {
                        InternalScriptRunner.runScript(groundInto
                                .getPostMoveScript(false, px, py, pz));
                        if (!(GameLogic.getSavedMapObject() instanceof Empty)) {
                            InternalScriptRunner.runScript(GameLogic
                                    .getSavedMapObject()
                                    .getPostMoveScript(false, px, py, pz));
                        }
                    } else {
                        InternalScriptRunner
                                .runScript(GameLogic.getSavedMapObject()
                                        .getPostMoveScript(false, px, py, pz));
                    }
                } else {
                    // Move failed - object is solid in that direction
                    GameLogic.fireMoveFailedActions(px + x, py + y, pz + z,
                            GameLogic.getSavedMapObject(), below, nextBelow,
                            nextAbove);
                    proceed = false;
                }
            } catch (final ArrayIndexOutOfBoundsException ae) {
                this.getViewManager().restoreViewingWindow();
                m.restorePlayerLocation();
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
        if (this.runBattles) {
            if (!Support.inDebugMode()) {
                // Process random battles
                ArrayList<InternalScriptArea> areaScripts = app
                        .getScenarioManager().getMap()
                        .getScriptAreasAtPoint(new Point(px, py), pz);
                for (InternalScriptArea isa : areaScripts) {
                    InternalScriptRunner.runScript(isa);
                }
                // Process step actions
                GameLogic.fireStepActions(px, py, pz);
            }
        }
        if (Support.inDebugMode() && this.runBattles) {
            // Process step actions otherwise skipped
            GameLogic.fireStepActions(px, py, pz);
        }
        // Process general events
        this.updateStats();
        this.checkGameOver();
        if (!this.runBattles) {
            // Run battles previously skipped
            this.runBattles = true;
        }
    }

    public void updatePositionRelativeNoEvents(int x, int y, int z) {
        boolean redrawsSuspended = false;
        Map m = RiskyRescue.getApplication().getScenarioManager().getMap();
        int px = m.getPlayerLocationX();
        int py = m.getPlayerLocationY();
        int pz = m.getPlayerLocationZ();
        Application app = RiskyRescue.getApplication();
        boolean proceed = false;
        MapObject o = new Empty();
        MapObject below = null;
        MapObject nextBelow = null;
        MapObject nextAbove = null;
        try {
            o = m.getCell(px + x, py + y, pz + z, MapConstants.LAYER_OBJECT);
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
        if (proceed) {
            m.savePlayerLocation();
            this.getViewManager().saveViewingWindow();
            try {
                if (GameLogic.checkSolid(z + pz, GameLogic.getSavedMapObject(),
                        below, nextBelow, nextAbove)) {
                    m.offsetPlayerLocationX(x);
                    m.offsetPlayerLocationY(y);
                    m.offsetPlayerLocationZ(z);
                    px += x;
                    py += y;
                    pz += z;
                    this.getViewManager().offsetViewingWindowLocationX(y);
                    this.getViewManager().offsetViewingWindowLocationY(x);
                    GameLogic.setSavedMapObject(
                            m.getCell(px, py, pz, MapConstants.LAYER_OBJECT));
                    app.getScenarioManager().setDirty(true);
                    this.redrawMap();
                } else {
                    // Move failed - object is solid in that direction
                    GameLogic.fireMoveFailedActions(px + x, py + y, pz + z,
                            GameLogic.getSavedMapObject(), below, nextBelow,
                            nextAbove);
                    proceed = false;
                }
            } catch (final ArrayIndexOutOfBoundsException ae) {
                this.getViewManager().restoreViewingWindow();
                m.restorePlayerLocation();
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

    private static MapObject getSavedMapObject() {
        return PartyManager.getParty().getPlayer().getSavedObject();
    }

    private static void setSavedMapObject(final MapObject newSaved) {
        PartyManager.getParty().getPlayer().setSavedObject(newSaved);
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
        }
        m.savePlayerLocation();
        this.getViewManager().saveViewingWindow();
        try {
            if (!(m.getCell(x, y, z, MapConstants.LAYER_OBJECT)
                    .isConditionallySolid(m, z))) {
                m.setPlayerLocation(x, y, z);
                this.getViewManager()
                        .setViewingWindowLocationX(m.getPlayerLocationY()
                                - GameViewingWindowManager.getOffsetFactor());
                this.getViewManager()
                        .setViewingWindowLocationY(m.getPlayerLocationX()
                                - GameViewingWindowManager.getOffsetFactor());
                GameLogic.setSavedMapObject(m.getCell(m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT));
                app.getScenarioManager().setDirty(true);
                InternalScriptRunner.runScript(GameLogic.getSavedMapObject()
                        .getPostMoveScript(false, x, y, z));
            }
        } catch (final ArrayIndexOutOfBoundsException ae) {
            m.restorePlayerLocation();
            this.getViewManager().restoreViewingWindow();
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
                m.setPlayerLocation(x, y, z);
                this.getViewManager()
                        .setViewingWindowLocationX(m.getPlayerLocationY()
                                - GameViewingWindowManager.getOffsetFactor());
                this.getViewManager()
                        .setViewingWindowLocationY(m.getPlayerLocationX()
                                - GameViewingWindowManager.getOffsetFactor());
                GameLogic.setSavedMapObject(m.getCell(m.getPlayerLocationX(),
                        m.getPlayerLocationY(), m.getPlayerLocationZ(),
                        MapConstants.LAYER_OBJECT));
                app.getScenarioManager().setDirty(true);
            }
        } catch (final ArrayIndexOutOfBoundsException ae) {
            m.restorePlayerLocation();
            this.getViewManager().restoreViewingWindow();
            RiskyRescue.getApplication()
                    .showMessage("Can't go outside the map");
        }
        RiskyRescue.getApplication().getScenarioManager().getMap()
                .updateVisibleSquares(x, y, z);
        this.redrawMap();
    }

    public void goToLevelRelative(final int level) {
        // Level change
        Application app = RiskyRescue.getApplication();
        Map m = app.getScenarioManager().getMap();
        final boolean levelExists = m.doesLevelExistOffset(level);
        if (!levelExists && m.isLevelOffsetValid(level)) {
            // The player will spawn atop stairs that need saving
            if (level < 0) {
                // Going up
                GameLogic.setSavedMapObject(new StairsDown());
            } else {
                // Going down
                GameLogic.setSavedMapObject(new StairsUp());
            }
            // Create the level
            m.addLevel(Support.getGameMapSize(), Support.getGameMapSize(),
                    Support.getGameMapFloorSize());
            m.fillLevelRandomly(new Tile(), new Empty());
            m.save();
            this.resetViewingWindow();
            m.resetVisibleSquares();
            int px = m.getPlayerLocationX();
            int py = m.getPlayerLocationY();
            int pz = m.getPlayerLocationZ();
            m.updateVisibleSquares(px, py, pz);
        } else if (levelExists && m.isLevelOffsetValid(level)) {
            // The player will spawn atop stairs that need saving
            if (level < 0) {
                // Going up
                GameLogic.setSavedMapObject(new StairsDown());
            } else {
                // Going down
                GameLogic.setSavedMapObject(new StairsUp());
            }
            m.switchLevelOffset(level);
        } else {
            // Attempted to leave the dungeon...
            Party party = PartyManager.getParty();
            if (party.getActivePCCount() == 2 && party.isAlive()) {
                // If our buddy is with us and everyone is alive, we win!
                this.victory();
            } else if (party.getActivePCCount() != 2 && party.isAlive()) {
                // Our buddy is not with us...
                this.gameOverMessage(
                        "Leaving the dungeon without your buddy, are you? GAME OVER!");
            } else if (party.getActivePCCount() == 2 && !party.isAlive()) {
                // Our buddy is with us, but one of us is dead :(
                this.gameOverMessage(
                        "Both of you must be alive to achieve victory... GAME OVER!");
            } else {
                // Something else?
                this.gameOverMessage(
                        "I have no clue how you got here, so GAME OVER!");
            }
            // End of the line...
            return;
        }
        this.resetViewingWindow();
        GameLogic.fireStepActions(m.getPlayerLocationX(),
                m.getPlayerLocationY(), m.getPlayerLocationZ());
        this.redrawMap();
        if (PreferencesManager
                .getMusicEnabled(PreferencesManager.MUSIC_DUNGEON)) {
            MusicManager.stopMusic();
            MusicManager.playMusic(MusicConstants.DUNGEON,
                    PartyManager.getMapLevel());
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
        CommonDialogs.showTitledDialog(
                "You have successfully escaped with your buddy! YOU WIN!",
                "MISSION COMPLETE");
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

    private void gameOverMessage(final String message) {
        SoundManager.playSound(SoundConstants.DEFEAT);
        CommonDialogs.showErrorDialog(message, "MISSION FAILED");
        MusicManager.stopMusic();
        this.st.commitScore();
        this.exitGame();
    }

    private void gameOver() {
        SoundManager.playSound(SoundConstants.DEFEAT);
        CommonDialogs.showDialog("You have died - Game Over!");
        MusicManager.stopMusic();
        this.st.commitScore();
        this.exitGame();
    }

    public JFrame getOutputFrame() {
        return this.gameGUI.getOutputFrame();
    }

    public void decay() {
        GameLogic.setSavedMapObject(new Empty());
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
            this.stateChanged = false;
        }
        // Make sure message area is attached to the border pane
        this.gameGUI.updateGameGUI();
        this.showOutput();
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
