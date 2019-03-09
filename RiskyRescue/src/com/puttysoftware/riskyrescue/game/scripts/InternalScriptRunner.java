/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.game.scripts;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.riskyrescue.RiskyRescue;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.battle.Battle;
import com.puttysoftware.riskyrescue.items.Shop;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.objects.MapObject;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScript;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptActionCode;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptConstants;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntry;

public final class InternalScriptRunner {
    private InternalScriptRunner() {
        // Do nothing
    }

    public static void runScript(InternalScript s) {
        int actionCounter = 0;
        try {
            if (s != null) {
                int totAct = s.getActionCount();
                for (int x = 0; x < totAct; x++) {
                    actionCounter = x + 1;
                    InternalScriptEntry se = s.getAction(x);
                    InternalScriptRunner.validateScriptEntry(se);
                    InternalScriptActionCode code = se.getActionCode();
                    switch (code) {
                    case MESSAGE:
                        // Show the message
                        String msg = se.getFirstActionArg().getString();
                        RiskyRescue.getApplication().showMessage(msg);
                        break;
                    case SOUND:
                        // Play the sound
                        int snd = se.getFirstActionArg().getInteger();
                        SoundManager.playSound(snd);
                        break;
                    case SHOP:
                        // Show the shop
                        int shopType = se.getFirstActionArg().getInteger();
                        Shop shop = RiskyRescue.getApplication()
                                .getGenericShop(shopType);
                        if (shop != null) {
                            shop.showShop();
                        } else {
                            throw new IllegalArgumentException(
                                    "Illegal Shop Type: " + shopType);
                        }
                        break;
                    case DECAY:
                        RiskyRescue.getApplication().getGameManager().decay();
                        break;
                    case SWAP_PAIRS:
                        String swap1 = se.getActionArg(0).getString();
                        String swap2 = se.getActionArg(1).getString();
                        MapObject swapObj1 = RiskyRescue.getApplication()
                                .getObjects().getInstanceByName(swap1);
                        MapObject swapObj2 = RiskyRescue.getApplication()
                                .getObjects().getInstanceByName(swap2);
                        RiskyRescue.getApplication().getScenarioManager()
                                .getMap()
                                .findAllObjectPairsAndSwap(swapObj1, swapObj2);
                        break;
                    case REDRAW:
                        RiskyRescue.getApplication().getGameManager()
                                .redrawMap();
                        break;
                    case ADD_TO_SCORE:
                        int points = se.getActionArg(0).getInteger();
                        RiskyRescue.getApplication().getGameManager()
                                .addToScore(points);
                        break;
                    case RANDOM_CHANCE:
                        // Random Chance
                        int threshold = se.getActionArg(0).getInteger();
                        RandomRange random = new RandomRange(0, 9999);
                        int chance = random.generate();
                        if (chance > threshold) {
                            return;
                        }
                        break;
                    case BATTLE:
                        // Hide the game
                        RiskyRescue.getApplication().getGameManager()
                                .hideOutput();
                        // Battle
                        final Battle battle = new Battle();
                        new Thread("Battle") {
                            @Override
                            public void run() {
                                try {
                                    RiskyRescue.getApplication()
                                            .getGameManager();
                                    RiskyRescue.getApplication().getBattle()
                                            .doFixedBattle(Map
                                                    .getTemporaryBattleCopy(),
                                                    battle);
                                } catch (Exception e) {
                                    // Something went wrong in the battle
                                    RiskyRescue.logError(e);
                                }
                            }
                        }.start();
                        break;
                    case RELATIVE_LEVEL_CHANGE:
                        int rDestLevel = se.getActionArg(0).getInteger();
                        RiskyRescue.getApplication().getGameManager()
                                .goToLevelRelative(rDestLevel);
                        break;
                    case UPDATE_GSA:
                        int gsaMod = se.getActionArg(0).getInteger();
                        RiskyRescue.getApplication().getScenarioManager()
                                .getMap().rebuildGSA(gsaMod);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Illegal Action Code: " + code.toString());
                    }
                }
            }
        } catch (Exception e) {
            String beginMsg = "Buggy Internal Script, action #" + actionCounter
                    + ": ";
            String endMsg = e.getMessage();
            String scriptMsg = beginMsg + endMsg;
            RiskyRescue.logNonFatalError(
                    new InternalScriptException(scriptMsg, e));
        }
    }

    private static void validateScriptEntry(InternalScriptEntry se) {
        InternalScriptActionCode code = se.getActionCode();
        int rargc = InternalScriptConstants.ARGUMENT_COUNT_VALIDATION[code
                .ordinal()];
        int aargc;
        if (se.getActionArgs() != null) {
            aargc = se.getActionArgs().length;
        } else {
            aargc = 0;
        }
        if (rargc != aargc) {
            throw new IllegalArgumentException("Expected " + rargc
                    + " arguments, found " + aargc + " arguments!");
        }
        Class<?>[] rargt = InternalScriptConstants.ARGUMENT_TYPE_VALIDATION[code
                .ordinal()];
        if (rargt != null) {
            Class<?>[] aargt = new Class[aargc];
            for (int x = 0; x < aargc; x++) {
                aargt[x] = se.getActionArg(x).getArgumentClass();
                if (!(aargt[x].getName().equals(rargt[x].getName()))) {
                    throw new IllegalArgumentException(
                            "Expected argument of type " + rargt[x].getName()
                                    + " at position " + (x + 1) + ", found "
                                    + aargt[x].getName() + "!");
                }
            }
        }
    }
}
