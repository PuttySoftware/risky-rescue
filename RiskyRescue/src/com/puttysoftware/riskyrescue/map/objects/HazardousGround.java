/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.assets.ObjectImage;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScript;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptActionCode;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntry;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntryArgument;

public class HazardousGround extends MapObject {
    // Fields
    private final int damagePercent;

    // Constructors
    public HazardousGround() {
        super(ObjectImage.HAZARD, false, false);
        this.damagePercent = 5;
    }

    private static InternalScript playSoundHook() {
        InternalScript scpt = new InternalScript();
        InternalScriptEntry entry0 = new InternalScriptEntry();
        entry0.setActionCode(InternalScriptActionCode.SOUND);
        entry0.addActionArg(
                new InternalScriptEntryArgument(SoundConstants.STEP_HAZARD));
        entry0.finalizeActionArgs();
        scpt.addAction(entry0);
        InternalScriptEntry entry1 = new InternalScriptEntry();
        entry1.setActionCode(InternalScriptActionCode.MESSAGE);
        entry1.addActionArg(
                new InternalScriptEntryArgument("Ow, the hazard hurt you!"));
        entry1.finalizeActionArgs();
        scpt.addAction(entry1);
        scpt.finalizeActions();
        return scpt;
    }

    @Override
    public InternalScript getPostMoveScript(final boolean ie, final int dirX,
            final int dirY, final int dirZ) {
        InternalScript gs = HazardousGround.playSoundHook();
        PartyManager.getParty().hurtPartyPercentage(this.damagePercent);
        return gs;
    }

    @Override
    public InternalScript getBattlePostMoveScript(
            final BattleCharacter invoker) {
        InternalScript gs = HazardousGround.playSoundHook();
        invoker.getTemplate().doDamagePercentage(this.damagePercent);
        return gs;
    }

    @Override
    public int getLayer() {
        return MapConstants.LAYER_GROUND;
    }

    @Override
    public int getCustomProperty(int propID) {
        return MapObject.DEFAULT_CUSTOM_VALUE;
    }

    @Override
    public void setCustomProperty(int propID, int value) {
        // Do nothing
    }

    @Override
    public int getMinimumRequiredQuantity(Map map, int level) {
        int regionSizeSquared = map.getRegionSize() ^ 2;
        int mapSize = map.getRows() * map.getColumns();
        int regionsPerMap = mapSize / regionSizeSquared;
        return regionsPerMap / (int) (Math.sqrt(Math.sqrt(mapSize)));
    }

    @Override
    public int getMaximumRequiredQuantity(Map map, int level) {
        int regionSizeSquared = map.getRegionSize() ^ 2;
        int mapSize = map.getRows() * map.getColumns();
        int regionsPerMap = mapSize / regionSizeSquared;
        return regionsPerMap / (int) (Math.sqrt(mapSize));
    }

    @Override
    public boolean isRequired(int level) {
        return true;
    }

    @Override
    public int getMinimumRequiredQuantityInBattle(Map map) {
        int regionSizeSquared = map.getRegionSize() ^ 2;
        int mapSize = map.getRows() * map.getColumns();
        int regionsPerMap = mapSize / regionSizeSquared;
        return regionsPerMap / (int) (Math.sqrt(Math.sqrt(mapSize)));
    }

    @Override
    public int getMaximumRequiredQuantityInBattle(Map map) {
        int regionSizeSquared = map.getRegionSize() ^ 2;
        int mapSize = map.getRows() * map.getColumns();
        int regionsPerMap = mapSize / regionSizeSquared;
        return regionsPerMap / (int) (Math.sqrt(mapSize));
    }

    @Override
    public boolean isRequiredInBattle() {
        return true;
    }

    @Override
    public String getName() {
        return "Hazard";
    }

    @Override
    public String getPluralName() {
        return "Squares of Hazards";
    }

    @Override
    public boolean overridesDefaultPostMove() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Hazards will damage you if you walk on them.";
    }

    @Override
    public String getGameImageNameHook() {
        return "textured";
    }

    @Override
    public String getEditorImageNameHook() {
        return "textured";
    }
}
