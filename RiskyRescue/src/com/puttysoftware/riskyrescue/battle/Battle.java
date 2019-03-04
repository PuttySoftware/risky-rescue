/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.battle;

import com.puttysoftware.riskyrescue.creatures.PartyManager;
import com.puttysoftware.riskyrescue.creatures.monsters.BaseMonster;
import com.puttysoftware.riskyrescue.creatures.monsters.MonsterFactory;
import com.puttysoftware.riskyrescue.map.objects.BattleCharacter;

public class Battle {
    // Fields
    private final BaseMonster[] monsterArray;
    private static final int MAX_MONSTERS = 5;

    // Constructors
    public Battle() {
        super();
        this.monsterArray = new BaseMonster[Battle.MAX_MONSTERS];
        // Fill array with monsters
        int numMonsters = PartyManager.getParty().getActivePCCount();
        for (int x = 0; x < numMonsters; x++) {
            this.monsterArray[x] = MonsterFactory.getNewMonsterInstance();
        }
    }

    // Methods
    private BaseMonster[] compactMonsterArray() {
        BaseMonster[] temp = new BaseMonster[this.monsterArray.length];
        System.arraycopy(this.monsterArray, 0, temp, 0,
                this.monsterArray.length);
        for (int x = 0; x < temp.length; x++) {
            if (temp[x] == null) {
                if (x < temp.length - 1) {
                    temp[x] = temp[x + 1];
                }
            }
        }
        return temp;
    }

    public BattleCharacter[] getBattlers() {
        BaseMonster[] compacted = this.compactMonsterArray();
        BattleCharacter[] battlerArray = new BattleCharacter[compacted.length];
        for (int x = 0; x < battlerArray.length; x++) {
            if (compacted[x] != null) {
                battlerArray[x] = new BattleCharacter(compacted[x]);
            }
        }
        return battlerArray;
    }
}
