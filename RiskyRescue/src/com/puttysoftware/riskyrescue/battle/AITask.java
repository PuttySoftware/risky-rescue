/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.battle;

import com.puttysoftware.riskyrescue.RiskyRescue;
import com.puttysoftware.riskyrescue.prefs.PreferencesManager;

class AITask extends Thread {
    // Fields
    private final BattleLogic b;
    private boolean done;

    // Constructors
    AITask(BattleLogic battle) {
        this.setName("AI Runner");
        this.b = battle;
        this.done = false;
    }

    @Override
    public void run() {
        try {
            while (!this.done && this.b.isWaitingForAI()) {
                this.b.executeNextAIAction();
                if (this.b.getLastAIActionResult()) {
                    // Delay, for animation purposes
                    try {
                        int battleSpeed = PreferencesManager.getBattleSpeed();
                        Thread.sleep(battleSpeed);
                    } catch (InterruptedException i) {
                        // Ignore
                    }
                    if (this.b.getTerminatedEarly()) {
                        // Bail out of here
                        return;
                    }
                }
            }
        } catch (Throwable t) {
            RiskyRescue.getErrorLogger().logError(t);
        }
    }

    void turnOver() {
        this.done = true;
    }
}
