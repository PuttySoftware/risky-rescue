/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures;

import com.puttysoftware.riskyrescue.assets.GameSoundConstants;
import com.puttysoftware.riskyrescue.effects.Effect;
import com.puttysoftware.riskyrescue.spells.Spell;
import com.puttysoftware.riskyrescue.spells.SpellBook;

class PartyMemberSpellBook extends SpellBook {
    // Constructor
    public PartyMemberSpellBook() {
        super(6, true);
        this.setName("Party Member Spell Book");
    }

    @Override
    protected void defineSpells() {
        Effect spell0Effect = new Effect("Poison", 3);
        spell0Effect.setAffectedStat(StatConstants.STAT_CURRENT_HP);
        spell0Effect.setEffect(-1, 1);
        spell0Effect.setScaleStat(StatConstants.STAT_LEVEL);
        spell0Effect.setScaleFactor(1, 1);
        spell0Effect.setMessage(Effect.MESSAGE_INITIAL,
                "You poison the enemy!");
        spell0Effect.setMessage(Effect.MESSAGE_SUBSEQUENT,
                "The enemy loses some health from being poisoned!");
        spell0Effect.setMessage(Effect.MESSAGE_WEAR_OFF,
                "The enemy is no longer poisoned!");
        Spell spell0 = new Spell(spell0Effect, 1, BattleTarget.ONE_ENEMY,
                GameSoundConstants.CAST_SPELL);
        this.spells[0] = spell0;
        Effect spell1Effect = new Effect("Recover", 1);
        spell1Effect.setAffectedStat(StatConstants.STAT_CURRENT_HP);
        spell1Effect.setEffect(5, 1);
        spell1Effect.setScaleStat(StatConstants.STAT_LEVEL);
        spell1Effect.setScaleFactor(3, 2);
        spell1Effect.setMessage(Effect.MESSAGE_INITIAL,
                "You apply a bandage to your wounds!");
        spell1Effect.setMessage(Effect.MESSAGE_SUBSEQUENT,
                "You regain some health!");
        Spell spell1 = new Spell(spell1Effect, 2, BattleTarget.SELF,
                GameSoundConstants.CAST_SPELL);
        this.spells[1] = spell1;
        Effect spell2Effect = new Effect("Weapon Drain", 5);
        spell2Effect.setAffectedStat(StatConstants.STAT_ATTACK);
        spell2Effect.setEffect(4, 5);
        spell2Effect.setMultiply(true);
        spell2Effect.setMessage(Effect.MESSAGE_INITIAL,
                "You drain the enemy's weapon of some of its power!");
        spell2Effect.setMessage(Effect.MESSAGE_SUBSEQUENT,
                "The enemy's attack is decreased!");
        spell2Effect.setMessage(Effect.MESSAGE_WEAR_OFF,
                "The enemy's weapon power has returned!");
        Spell spell2 = new Spell(spell2Effect, 3, BattleTarget.ONE_ENEMY,
                GameSoundConstants.CAST_SPELL);
        this.spells[2] = spell2;
        Effect spell3Effect = new Effect("Armor Drain", 5);
        spell3Effect.setAffectedStat(StatConstants.STAT_DEFENSE);
        spell3Effect.setEffect(4, 5);
        spell3Effect.setMultiply(true);
        spell3Effect.setMessage(Effect.MESSAGE_INITIAL,
                "You drain the enemy's armor of some of its power!");
        spell3Effect.setMessage(Effect.MESSAGE_SUBSEQUENT,
                "The enemy's defense is decreased!");
        spell3Effect.setMessage(Effect.MESSAGE_WEAR_OFF,
                "The enemy's armor power has returned!");
        Spell spell3 = new Spell(spell3Effect, 5, BattleTarget.ONE_ENEMY,
                GameSoundConstants.CAST_SPELL);
        this.spells[3] = spell3;
        Effect spell4Effect = new Effect("Weapon Charge", 5);
        spell4Effect.setAffectedStat(StatConstants.STAT_ATTACK);
        spell4Effect.setEffect(5, 4);
        spell4Effect.setMultiply(true);
        spell4Effect.setMessage(Effect.MESSAGE_INITIAL,
                "You charges your weapon with power!");
        spell4Effect.setMessage(Effect.MESSAGE_SUBSEQUENT,
                "Your attack is increased!");
        spell4Effect.setMessage(Effect.MESSAGE_WEAR_OFF,
                "Your weapon returns to normal!");
        Spell spell4 = new Spell(spell4Effect, 7, BattleTarget.SELF,
                GameSoundConstants.CAST_SPELL);
        this.spells[4] = spell4;
        Effect spell5Effect = new Effect("Armor Charge", 5);
        spell5Effect.setAffectedStat(StatConstants.STAT_DEFENSE);
        spell5Effect.setEffect(5, 4);
        spell5Effect.setMultiply(true);
        spell5Effect.setMessage(Effect.MESSAGE_INITIAL,
                "You charge your armor with power!");
        spell5Effect.setMessage(Effect.MESSAGE_SUBSEQUENT,
                "Your defense is increased!");
        spell5Effect.setMessage(Effect.MESSAGE_WEAR_OFF,
                "Your armor returns to normal!");
        Spell spell5 = new Spell(spell5Effect, 11, BattleTarget.SELF,
                GameSoundConstants.CAST_SPELL);
        this.spells[5] = spell5;
    }
}
