/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.battle;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.riskyrescue.Application;
import com.puttysoftware.riskyrescue.RiskyRescue;
import com.puttysoftware.riskyrescue.Support;
import com.puttysoftware.riskyrescue.assets.ImageManager;
import com.puttysoftware.riskyrescue.assets.MusicConstants;
import com.puttysoftware.riskyrescue.assets.MusicManager;
import com.puttysoftware.riskyrescue.assets.ObjectImage;
import com.puttysoftware.riskyrescue.assets.modifiers.ImageComposer;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.objects.MapObject;
import com.puttysoftware.riskyrescue.prefs.PreferencesManager;
import com.puttysoftware.riskyrescue.utilities.DrawGrid;

class BattleGUI {
    // Fields
    private JFrame battleFrame;
    private BattleDraw battlePane;
    private JLabel messageLabel;
    private final BattleViewingWindowManager vwMgr;
    private final BattleStats bs;
    private final BattleEffects be;
    private DrawGrid drawGrid;
    boolean eventHandlersOn;

    // Constructors
    BattleGUI() {
        this.vwMgr = new BattleViewingWindowManager();
        this.bs = new BattleStats();
        this.be = new BattleEffects();
        this.setUpGUI();
        this.eventHandlersOn = true;
    }

    // Methods
    JFrame getOutputFrame() {
        return this.battleFrame;
    }

    BattleViewingWindowManager getViewManager() {
        return this.vwMgr;
    }

    void clearStatusMessage() {
        this.messageLabel.setText(" ");
    }

    void setStatusMessage(final String msg) {
        if (!msg.isEmpty() && !msg.matches("\\s+")) {
            this.messageLabel.setText(msg);
        }
    }

    void showBattle() {
        Application app = RiskyRescue.getApplication();
        Map m = app.getScenarioManager().getMap();
        app.getMenuManager().setBattleMenus();
        if (PreferencesManager
                .getMusicEnabled(PreferencesManager.MUSIC_BATTLE)) {
            MusicManager.stopMusic();
            MusicManager.playMusic(MusicConstants.BATTLE,
                    m.getPlayerLocationZ());
        }
        this.battleFrame.setVisible(true);
        app.attachMenus(this.battleFrame);
    }

    void hideBattle() {
        if (MusicManager.isMusicPlaying()) {
            MusicManager.stopMusic();
        }
        if (this.battleFrame != null) {
            this.battleFrame.setVisible(false);
        }
    }

    void redrawBattle(BattleDefinitions bd) {
        // Draw the battle, if it is visible
        if (this.battleFrame.isVisible()) {
            int x, y;
            int xFix, yFix;
            int xView = this.vwMgr.getViewingWindowLocationX();
            int yView = this.vwMgr.getViewingWindowLocationY();
            int xlView = this.vwMgr.getLowerRightViewingWindowLocationX();
            int ylView = this.vwMgr.getLowerRightViewingWindowLocationY();
            for (x = xView; x <= xlView; x++) {
                for (y = yView; y <= ylView; y++) {
                    xFix = x - xView;
                    yFix = y - yView;
                    try {
                        BufferedImageIcon icon1 = bd.getBattleMap()
                                .getBattleGround(y, x).getImage();
                        BufferedImageIcon icon2 = bd.getBattleMap()
                                .getBattleCell(y, x).getImage();
                        this.drawGrid.setImageCell(
                                ImageComposer.getCompositeImage(icon1, icon2),
                                xFix, yFix);
                    } catch (final ArrayIndexOutOfBoundsException ae) {
                        this.drawGrid.setImageCell(ImageManager.getObjectImage(
                                0, ObjectImage.SEALING_WALL), xFix, yFix);
                    }
                }
            }
            this.battlePane.updateGrid(this.drawGrid);
            this.battlePane.repaint();
            this.battleFrame.pack();
        }
    }

    void redrawOneBattleSquare(BattleDefinitions bd, int x, int y,
            MapObject obj3) {
        // Draw the battle, if it is visible
        if (this.battleFrame.isVisible()) {
            try {
                int xFix, yFix;
                int xView = this.vwMgr.getViewingWindowLocationX();
                int yView = this.vwMgr.getViewingWindowLocationY();
                xFix = y - xView;
                yFix = x - yView;
                BufferedImageIcon icon1 = bd.getBattleMap()
                        .getBattleGround(x, y).getImage();
                BufferedImageIcon icon2 = bd.getBattleMap().getBattleCell(x, y)
                        .getImage();
                BufferedImageIcon icon3 = obj3.getImage();
                this.drawGrid.setImageCell(
                        ImageComposer.getCompositeImage(icon1, icon2, icon3),
                        xFix, yFix);
                this.battlePane.repaint();
            } catch (final ArrayIndexOutOfBoundsException ae) {
                // Do nothing
            }
            this.battleFrame.pack();
        }
    }

    void updateStatsAndEffects(BattleDefinitions bd) {
        this.bs.updateStats(bd.getActiveCharacter());
        this.be.updateEffects(bd.getActiveCharacter());
    }

    private void setUpGUI() {
        EventHandler handler = new EventHandler();
        Container borderPane = new Container();
        borderPane.setLayout(new BorderLayout());
        this.messageLabel = new JLabel(" ");
        this.messageLabel.setOpaque(true);
        if (Support.inDebugMode()) {
            this.battleFrame = new JFrame("Battle (DEBUG)");
        } else {
            this.battleFrame = new JFrame("Battle");
        }
        this.battlePane = new BattleDraw();
        this.battleFrame.setContentPane(borderPane);
        this.battleFrame
                .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.battlePane.setLayout(new GridLayout(
                BattleViewingWindowManager.getViewingWindowSize(),
                BattleViewingWindowManager.getViewingWindowSize()));
        this.battleFrame.setResizable(false);
        this.drawGrid = new DrawGrid(
                BattleViewingWindowManager.getViewingWindowSize());
        for (int x = 0; x < BattleViewingWindowManager
                .getViewingWindowSize(); x++) {
            for (int y = 0; y < BattleViewingWindowManager
                    .getViewingWindowSize(); y++) {
                this.drawGrid.setImageCell(
                        ImageManager.getObjectImage(0, ObjectImage.DARKNESS), x,
                        y);
            }
        }
        this.battlePane.updateGrid(this.drawGrid);
        borderPane.add(this.battlePane, BorderLayout.CENTER);
        borderPane.add(this.messageLabel, BorderLayout.NORTH);
        borderPane.add(this.bs.getStatsPane(), BorderLayout.EAST);
        borderPane.add(this.be.getEffectsPane(), BorderLayout.SOUTH);
        this.battleFrame.addKeyListener(handler);
    }

    void turnEventHandlersOff() {
        this.eventHandlersOn = false;
    }

    void turnEventHandlersOn() {
        this.eventHandlersOn = true;
    }

    boolean areEventHandlersOn() {
        return this.eventHandlersOn;
    }

    private class EventHandler implements KeyListener {
        public EventHandler() {
            // Do nothing
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            BattleGUI bg = BattleGUI.this;
            if (bg.eventHandlersOn) {
                if (!PreferencesManager.oneMove()) {
                    if (e.isShiftDown()) {
                        this.handleArrows(e);
                    } else {
                        this.handleMovement(e);
                    }
                }
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
            BattleGUI bg = BattleGUI.this;
            if (bg.eventHandlersOn) {
                if (PreferencesManager.oneMove()) {
                    if (e.isShiftDown()) {
                        this.handleArrows(e);
                    } else {
                        this.handleMovement(e);
                    }
                }
            }
        }

        @Override
        public void keyTyped(final KeyEvent e) {
            // Do nothing
        }

        private void handleMovement(final KeyEvent e) {
            try {
                if (System.getProperty("os.name")
                        .equalsIgnoreCase("Mac OS X")) {
                    if (e.isMetaDown()) {
                        return;
                    }
                } else {
                    if (e.isControlDown()) {
                        return;
                    }
                }
                BattleLogic bl = RiskyRescue.getApplication().getBattle();
                BattleGUI bg = BattleGUI.this;
                if (bg.eventHandlersOn) {
                    final int keyCode = e.getKeyCode();
                    switch (keyCode) {
                    case KeyEvent.VK_NUMPAD4:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        bl.updatePosition(-1, 0);
                        break;
                    case KeyEvent.VK_NUMPAD2:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_X:
                        bl.updatePosition(0, 1);
                        break;
                    case KeyEvent.VK_NUMPAD6:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        bl.updatePosition(1, 0);
                        break;
                    case KeyEvent.VK_NUMPAD8:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        bl.updatePosition(0, -1);
                        break;
                    case KeyEvent.VK_NUMPAD7:
                    case KeyEvent.VK_Q:
                        bl.updatePosition(-1, -1);
                        break;
                    case KeyEvent.VK_NUMPAD9:
                    case KeyEvent.VK_E:
                        bl.updatePosition(1, -1);
                        break;
                    case KeyEvent.VK_NUMPAD3:
                    case KeyEvent.VK_C:
                        bl.updatePosition(1, 1);
                        break;
                    case KeyEvent.VK_NUMPAD1:
                    case KeyEvent.VK_Z:
                        bl.updatePosition(-1, 1);
                        break;
                    case KeyEvent.VK_NUMPAD5:
                    case KeyEvent.VK_S:
                        // Confirm before attacking self
                        int res = CommonDialogs.showConfirmDialog(
                                "Are you sure you want to attack yourself?",
                                "Battle");
                        if (res == JOptionPane.YES_OPTION) {
                            bl.updatePosition(0, 0);
                        }
                        break;
                    default:
                        break;
                    }
                }
            } catch (Exception ex) {
                RiskyRescue.logError(ex);
            }
        }

        private void handleArrows(final KeyEvent e) {
            try {
                if (System.getProperty("os.name")
                        .equalsIgnoreCase("Mac OS X")) {
                    if (e.isMetaDown()) {
                        return;
                    }
                } else {
                    if (e.isControlDown()) {
                        return;
                    }
                }
                BattleLogic bl = RiskyRescue.getApplication().getBattle();
                BattleGUI bg = BattleGUI.this;
                if (bg.eventHandlersOn) {
                    final int keyCode = e.getKeyCode();
                    switch (keyCode) {
                    case KeyEvent.VK_NUMPAD4:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        bl.fireArrow(-1, 0);
                        break;
                    case KeyEvent.VK_NUMPAD2:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_X:
                        bl.fireArrow(0, 1);
                        break;
                    case KeyEvent.VK_NUMPAD6:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        bl.fireArrow(1, 0);
                        break;
                    case KeyEvent.VK_NUMPAD8:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        bl.fireArrow(0, -1);
                        break;
                    case KeyEvent.VK_NUMPAD7:
                    case KeyEvent.VK_Q:
                        bl.fireArrow(-1, -1);
                        break;
                    case KeyEvent.VK_NUMPAD9:
                    case KeyEvent.VK_E:
                        bl.fireArrow(1, -1);
                        break;
                    case KeyEvent.VK_NUMPAD3:
                    case KeyEvent.VK_C:
                        bl.fireArrow(1, 1);
                        break;
                    case KeyEvent.VK_NUMPAD1:
                    case KeyEvent.VK_Z:
                        bl.fireArrow(-1, 1);
                        break;
                    case KeyEvent.VK_NUMPAD5:
                    case KeyEvent.VK_S:
                        // Confirm before attacking self
                        int res = CommonDialogs.showConfirmDialog(
                                "Are you sure you want to attack yourself?",
                                "Battle");
                        if (res == JOptionPane.YES_OPTION) {
                            bl.fireArrow(0, 0);
                        }
                        break;
                    default:
                        break;
                    }
                }
            } catch (Exception ex) {
                RiskyRescue.logError(ex);
            }
        }
    }
}
