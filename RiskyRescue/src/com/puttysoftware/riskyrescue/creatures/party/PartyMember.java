/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures.party;

import java.io.IOException;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.page.Page;
import com.puttysoftware.riskyrescue.assets.ImageManager;
import com.puttysoftware.riskyrescue.creatures.Creature;
import com.puttysoftware.riskyrescue.creatures.PrestigeConstants;
import com.puttysoftware.riskyrescue.creatures.StatConstants;
import com.puttysoftware.riskyrescue.items.ItemInventory;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScript;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptActionCode;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntry;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntryArgument;
import com.puttysoftware.riskyrescue.spells.SpellBook;
import com.puttysoftware.riskyrescue.utilities.PCImage;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PartyMember extends Creature {
    // Fields
    private final String name;
    private final PCImage pci;
    private static final int START_GOLD = 0;
    private static final double BASE_COEFF = 10.0;

    // Constructors
    PartyMember(final PCImage i, final String n) {
        super();
        this.name = n;
        this.pci = i;
        this.setLevel(1);
        this.setStrength(StatConstants.GAIN_STRENGTH);
        this.setBlock(StatConstants.GAIN_BLOCK);
        this.setVitality(StatConstants.GAIN_VITALITY);
        this.setIntelligence(StatConstants.GAIN_INTELLIGENCE);
        this.setAgility(StatConstants.GAIN_AGILITY);
        this.setLuck(StatConstants.GAIN_LUCK);
        this.healAndRegenerateFully();
        this.setGold(PartyMember.START_GOLD);
        this.setExperience(0L);
        Page nextLevelEquation = new Page(3, 1, 0, true);
        double value = BASE_COEFF;
        nextLevelEquation.setCoefficient(1, value);
        nextLevelEquation.setCoefficient(2, value);
        nextLevelEquation.setCoefficient(3, value);
        this.setToNextLevel(nextLevelEquation);
        this.setSpellBook(new PartyMemberSpellBook());
    }

    // Methods
    @Override
    public BufferedImageIcon getInitialImage() {
        return ImageManager.getPlayerImage(this.pci);
    }

    public String getXPString() {
        return this.getExperience() + "/" + this.getToNextLevelValue();
    }

    // Transformers
    @Override
    protected InternalScript levelUpHook() {
        this.offsetStrength(StatConstants.GAIN_STRENGTH);
        this.offsetBlock(StatConstants.GAIN_BLOCK);
        this.offsetVitality(StatConstants.GAIN_VITALITY);
        this.offsetIntelligence(StatConstants.GAIN_INTELLIGENCE);
        this.offsetAgility(StatConstants.GAIN_AGILITY);
        this.offsetLuck(StatConstants.GAIN_LUCK);
        this.healAndRegenerateFully();
        InternalScript levelUpScript = new InternalScript();
        InternalScriptEntry act0 = new InternalScriptEntry();
        act0.setActionCode(InternalScriptActionCode.UPDATE_GSA);
        act0.addActionArg(new InternalScriptEntryArgument(
                PartyManager.getParty().getDungeonLevel() - this.getLevel()));
        act0.finalizeActionArgs();
        levelUpScript.addAction(act0);
        levelUpScript.finalizeActions();
        return levelUpScript;
    }

    private void loadPartyMember(final int newLevel, final int chp,
            final int cmp, final int newGold, final int newLoad,
            final long newExperience, final boolean[] known,
            final long[] pres) {
        this.setLevel(newLevel);
        this.setCurrentHP(chp);
        this.setCurrentMP(cmp);
        this.setGold(newGold);
        this.setLoad(newLoad);
        this.setExperience(newExperience);
        SpellBook book = new PartyMemberSpellBook();
        for (int x = 0; x < known.length; x++) {
            if (known[x]) {
                book.learnSpellByID(x);
            }
        }
        for (int x = 0; x < pres.length; x++) {
            this.setPrestigeValue(x, pres[x]);
        }
        this.setSpellBook(book);
    }

    @Override
    public int getCapacity() {
        return Math.max(StatConstants.MIN_CAPACITY, super.getCapacity());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static PartyMember read(XDataReader worldFile) throws IOException {
        int strength = worldFile.readInt();
        int block = worldFile.readInt();
        int agility = worldFile.readInt();
        int vitality = worldFile.readInt();
        int intelligence = worldFile.readInt();
        int luck = worldFile.readInt();
        int lvl = worldFile.readInt();
        int cHP = worldFile.readInt();
        int cMP = worldFile.readInt();
        int gld = worldFile.readInt();
        int apr = worldFile.readInt();
        int spr = worldFile.readInt();
        int ipr = worldFile.readInt();
        int tpr = worldFile.readInt();
        int load = worldFile.readInt();
        long exp = worldFile.readLong();
        PCImage pci = PCImage.read(worldFile);
        int max = worldFile.readInt();
        boolean[] known = new boolean[max];
        for (int x = 0; x < max; x++) {
            known[x] = worldFile.readBoolean();
        }
        long[] prestige = new long[PrestigeConstants.MAX_PRESTIGE];
        for (int x = 0; x < PrestigeConstants.MAX_PRESTIGE; x++) {
            prestige[x] = worldFile.readLong();
        }
        String n = worldFile.readString();
        PartyMember pm = new PartyMember(pci, n);
        pm.setStrength(strength);
        pm.setBlock(block);
        pm.setAgility(agility);
        pm.setVitality(vitality);
        pm.setIntelligence(intelligence);
        pm.setLuck(luck);
        pm.setAttacksPerRound(apr);
        pm.setSpellsPerRound(spr);
        pm.setItemsPerRound(ipr);
        pm.setStealsPerRound(tpr);
        pm.setItems(ItemInventory.readItemInventory(worldFile));
        pm.loadPartyMember(lvl, cHP, cMP, gld, load, exp, known, prestige);
        return pm;
    }

    public void write(XDataWriter worldFile) throws IOException {
        worldFile.writeInt(this.getStrength());
        worldFile.writeInt(this.getBlock());
        worldFile.writeInt(this.getAgility());
        worldFile.writeInt(this.getVitality());
        worldFile.writeInt(this.getIntelligence());
        worldFile.writeInt(this.getLuck());
        worldFile.writeInt(this.getLevel());
        worldFile.writeInt(this.getCurrentHP());
        worldFile.writeInt(this.getCurrentMP());
        worldFile.writeInt(this.getGold());
        worldFile.writeInt(this.getAttacksPerRound());
        worldFile.writeInt(this.getSpellsPerRound());
        worldFile.writeInt(this.getItemsPerRound());
        worldFile.writeInt(this.getStealsPerRound());
        worldFile.writeInt(this.getLoad());
        worldFile.writeLong(this.getExperience());
        this.pci.write(worldFile);
        int max = this.getSpellBook().getSpellCount();
        worldFile.writeInt(max);
        for (int x = 0; x < max; x++) {
            worldFile.writeBoolean(this.getSpellBook().isSpellKnown(x));
        }
        for (int x = 0; x < PrestigeConstants.MAX_PRESTIGE; x++) {
            worldFile.writeLong(this.getPrestigeValue(x));
        }
        worldFile.writeString(this.getName());
        this.getItems().writeItemInventory(worldFile);
    }
}
