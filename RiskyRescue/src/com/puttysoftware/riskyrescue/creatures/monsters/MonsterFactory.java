/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures.monsters;

public class MonsterFactory {
    private MonsterFactory() {
        // Do nothing
    }

    public static BaseMonster getNewMonsterInstance() {
        return new BothRandomScalingStaticMonster();
    }
}
