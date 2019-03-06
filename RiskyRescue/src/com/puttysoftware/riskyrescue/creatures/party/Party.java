/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures.party;

import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.battle.VictorySpoilsDescription;
import com.puttysoftware.riskyrescue.creatures.Creature;
import com.puttysoftware.riskyrescue.map.objects.BattleCharacter;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScript;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptActionCode;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntry;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntryArgument;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Party {
    // Fields
    private PartyMember[] members;
    private BattleCharacter[] battlers;
    private int leaderID;
    private int activePCs;
    private int dungeonLevel;

    // Constructors
    private Party() {
        this.members = null;
        this.battlers = null;
        this.leaderID = 0;
        this.activePCs = 0;
        this.dungeonLevel = 1;
    }

    Party(PartyMember[] newMembers) {
        this.members = newMembers;
        this.battlers = null;
        this.leaderID = 0;
        this.activePCs = 0;
        this.dungeonLevel = 1;
    }

    Party(final int maxMembers) {
        this.members = new PartyMember[maxMembers];
        this.leaderID = 0;
        this.activePCs = 0;
        this.dungeonLevel = 1;
    }

    // Methods
    private void generateBattleCharacters() {
        BattleCharacter[] tempChars = new BattleCharacter[this.members.length];
        int nnc = 0;
        for (int x = 0; x < tempChars.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                tempChars[x] = new BattleCharacter(pm);
                nnc++;
            }
        }
        BattleCharacter[] chars = new BattleCharacter[nnc];
        nnc = 0;
        for (int x = 0; x < tempChars.length; x++) {
            if (tempChars[x] != null) {
                chars[nnc] = tempChars[x];
                nnc++;
            }
        }
        this.battlers = chars;
    }

    public BattleCharacter[] getBattleCharacters() {
        if (this.battlers == null) {
            this.generateBattleCharacters();
        }
        return this.battlers;
    }

    public ArrayList<InternalScript> checkPartyLevelUp() {
        ArrayList<InternalScript> retVal = new ArrayList<>();
        for (int x = 0; x < this.battlers.length; x++) {
            // Level Up Check
            if (this.battlers[x].getTemplate().checkLevelUp()) {
                InternalScript scpt = this.battlers[x].getTemplate().levelUp();
                if (scpt != null) {
                    retVal.add(scpt);
                }
                SoundManager.playSound(SoundConstants.LEVEL_UP);
                CommonDialogs
                        .showTitledDialog(
                                this.battlers[x].getTemplate().getName()
                                        + " reached level " + this.battlers[x]
                                                .getTemplate().getLevel()
                                        + "!",
                                "Level Up");
                InternalScript levelUpScript = new InternalScript();
                InternalScriptEntry act0 = new InternalScriptEntry();
                act0.setActionCode(InternalScriptActionCode.ADD_TO_SCORE);
                act0.addActionArg(new InternalScriptEntryArgument(Math.max(1,
                        (10 * this.battlers[x].getTemplate().getLevel() - 1)
                                / this.activePCs)));
                act0.finalizeActionArgs();
                levelUpScript.addAction(act0);
                levelUpScript.finalizeActions();
                retVal.add(levelUpScript);
            }
        }
        return retVal;
    }

    public void stripPartyEffects() {
        for (int x = 0; x < this.members.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                // Strip All Effects
                pm.stripAllEffects();
            }
        }
    }

    public void distributeVictorySpoils(VictorySpoilsDescription vsd,
            int otherLevel) {
        int divMod = this.battlers.length;
        int monLen = vsd.getMonsterCount();
        for (int x = 0; x < divMod; x++) {
            // Distribute Victory Spoils
            for (int y = 0; y < monLen; y++) {
                this.battlers[x].getTemplate();
                this.battlers[x].getTemplate()
                        .offsetExperience(Creature.getAdjustedExperience(
                                vsd.getExpPerMonster(y),
                                this.getLeader().getLevel(), otherLevel)
                                / divMod);
            }
            this.battlers[x].getTemplate()
                    .offsetGold(vsd.getGoldWon() / divMod);
        }
    }

    public long getPartyMaxToNextLevel() {
        long largest = Integer.MIN_VALUE;
        for (int x = 0; x < this.members.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                if (pm.getToNextLevelValue() > largest) {
                    largest = pm.getToNextLevelValue();
                }
            }
        }
        return largest;
    }

    public void hurtPartyPercentage(int mod) {
        for (int x = 0; x < this.members.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                // Hurt Party Member
                pm.doDamagePercentage(mod);
            }
        }
    }

    void revivePartyFully() {
        for (int x = 0; x < this.members.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                // Revive Party Member
                pm.healAndRegenerateFully();
            }
        }
    }

    public PartyMember getLeader() {
        return this.getMember(this.leaderID);
    }

    public PartyMember getMember(final int index) {
        if (index == 1 && this.activePCs != 2) {
            // Buddy inactive
            return null;
        }
        return this.members[index];
    }

    public int getActivePCCount() {
        return this.activePCs;
    }

    public int getDungeonLevel() {
        return this.dungeonLevel;
    }

    public String getDungeonLevelString() {
        return "" + this.dungeonLevel;
    }

    public void increaseDungeonLevel() {
        if (this.dungeonLevel < Creature.getMaximumLevel()) {
            this.dungeonLevel++;
        }
    }

    public void decreaseDungeonLevel() {
        if (this.dungeonLevel > 0) {
            this.dungeonLevel--;
        }
    }

    public boolean isAlive() {
        boolean result = false;
        for (int x = 0; x < this.members.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                result = result || pm.isAlive();
            }
        }
        return result;
    }

    private String[] buildNameList() {
        String[] tempNames = new String[this.members.length];
        int nnc = 0;
        for (int x = 0; x < tempNames.length; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                tempNames[x] = pm.getName();
                nnc++;
            }
        }
        String[] names = new String[nnc];
        nnc = 0;
        for (int x = 0; x < tempNames.length; x++) {
            if (tempNames[x] != null) {
                names[nnc] = tempNames[x];
                nnc++;
            }
        }
        return names;
    }

    PartyMember pickOnePartyMemberCreate() {
        String[] pickNames = this.buildNameList();
        String response = CommonDialogs.showInputDialog("Pick 1 Party Member",
                "Create Party", pickNames, pickNames[0]);
        if (response != null) {
            int loc = this.findMember(response, 0, this.members.length);
            if (loc != -1) {
                return this.getMember(loc);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    void addHero(PartyMember hero) {
        this.members[0] = hero;
        this.activePCs = 1;
        this.generateBattleCharacters();
    }

    void addBuddy(PartyMember buddy) {
        this.members[1] = buddy;
    }

    void activateBuddy() {
        this.activePCs = 2;
        this.generateBattleCharacters();
    }

    private int findMember(String name, int start, int limit) {
        for (int x = start; x < limit; x++) {
            PartyMember pm = this.getMember(x);
            if (pm != null) {
                if (pm.getName().equals(name)) {
                    return x;
                }
            }
        }
        return -1;
    }

    static Party read(XDataReader worldFile) throws IOException {
        int memCount = worldFile.readInt();
        int lid = worldFile.readInt();
        int apc = worldFile.readInt();
        int dl = worldFile.readInt();
        Party pty = new Party();
        pty.leaderID = lid;
        pty.activePCs = apc;
        pty.members = new PartyMember[memCount];
        pty.dungeonLevel = dl;
        for (int z = 0; z < memCount; z++) {
            boolean present = worldFile.readBoolean();
            if (present) {
                pty.members[z] = PartyMember.read(worldFile);
            }
        }
        return pty;
    }

    void write(XDataWriter worldFile) throws IOException {
        worldFile.writeInt(this.members.length);
        worldFile.writeInt(this.leaderID);
        worldFile.writeInt(this.activePCs);
        worldFile.writeInt(this.dungeonLevel);
        for (int z = 0; z < this.members.length; z++) {
            if (this.members[z] == null) {
                worldFile.writeBoolean(false);
            } else {
                worldFile.writeBoolean(true);
                this.members[z].write(worldFile);
            }
        }
    }
}
