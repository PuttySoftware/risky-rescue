/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: products@putttysoftware.com
 */
package com.puttysoftware.riskyrescue.battle;

import com.puttysoftware.riskyrescue.Application;
import com.puttysoftware.riskyrescue.RiskyRescue;
import com.puttysoftware.riskyrescue.assets.GameSoundConstants;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.objects.Arrow;
import com.puttysoftware.riskyrescue.map.objects.BattleCharacter;
import com.puttysoftware.riskyrescue.map.objects.Empty;
import com.puttysoftware.riskyrescue.map.objects.MapObject;
import com.puttysoftware.riskyrescue.map.objects.Wall;

class ArrowTask extends Thread {
    // Fields
    private int x, y;
    private BattleDefinitions bd;

    // Constructors
    ArrowTask(int newX, int newY, BattleDefinitions defs) {
        this.x = newX;
        this.y = newY;
        this.bd = defs;
        this.setName("Arrow Handler");
    }

    @Override
    public void run() {
        try {
            boolean res = true;
            Application app = RiskyRescue.getApplication();
            Map m = this.bd.getBattleMap();
            int px = this.bd.getActiveCharacter().getX();
            int py = this.bd.getActiveCharacter().getY();
            int cumX = this.x;
            int cumY = this.y;
            int incX = this.x;
            int incY = this.y;
            MapObject o = null;
            try {
                o = m.getBattleCell(px + cumX, py + cumY);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                o = new Wall();
            }
            Arrow a = Arrow.createArrow(incX, incY);
            SoundManager.playSound(GameSoundConstants.ARROW_SHOOT);
            while (res) {
                res = o.arrowHitCheck();
                if (!res) {
                    break;
                }
                // Draw arrow
                app.getBattle().redrawOneBattleSquare(px + cumX, py + cumY, a);
                // Delay, for animation purposes
                Thread.sleep(ArrowSpeedConstants.getArrowSpeed());
                // Clear arrow
                app.getBattle().redrawOneBattleSquare(px + cumX, py + cumY,
                        new Empty());
                cumX += incX;
                cumY += incY;
                try {
                    o = m.getBattleCell(px + cumX, py + cumY);
                } catch (final ArrayIndexOutOfBoundsException ae) {
                    res = false;
                }
            }
            // Check to see if the arrow hit a creature
            BattleCharacter hit = null;
            if (o instanceof BattleCharacter) {
                // Arrow hit a creature, hurt it
                SoundManager.playSound(GameSoundConstants.ARROW_HIT);
                hit = (BattleCharacter) o;
                BattleLogic bl = app.getBattle();
                hit.getTemplate().doDamagePercentage(1);
                bl.setStatusMessage("Ow, you got shot!");
            } else {
                // Arrow has died
                SoundManager.playSound(GameSoundConstants.ARROW_DIE);
            }
            app.getBattle().arrowDone(hit);
        } catch (Throwable t) {
            RiskyRescue.getErrorLogger().logError(t);
        }
    }
}
