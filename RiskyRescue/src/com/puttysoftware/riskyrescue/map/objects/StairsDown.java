/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import com.puttysoftware.riskyrescue.assets.GameSoundConstants;
import com.puttysoftware.riskyrescue.assets.ObjectImage;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScript;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptActionCode;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntry;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntryArgument;

public class StairsDown extends MapObject {
    // Fields
    private final InternalScript postMoveScript;

    // Constructors
    public StairsDown() {
        super(ObjectImage.DOWN, false, false);
        // Create post-move script
        InternalScript scpt = new InternalScript();
        InternalScriptEntry entry1 = new InternalScriptEntry();
        entry1.setActionCode(InternalScriptActionCode.RELATIVE_LEVEL_CHANGE);
        entry1.addActionArg(new InternalScriptEntryArgument(1));
        entry1.finalizeActionArgs();
        scpt.addAction(entry1);
        InternalScriptEntry entry2 = new InternalScriptEntry();
        entry2.setActionCode(InternalScriptActionCode.SOUND);
        entry2.addActionArg(new InternalScriptEntryArgument(
                GameSoundConstants.DOWN));
        entry2.finalizeActionArgs();
        scpt.addAction(entry2);
        scpt.finalizeActions();
        this.postMoveScript = scpt;
    }

    @Override
    public String getName() {
        return "Stairs Into";
    }

    @Override
    public String getPluralName() {
        return "Sets of Stairs Into";
    }

    @Override
    public InternalScript getPostMoveScript(final boolean ie, final int dirX,
            final int dirY, final int dirZ) {
        PartyManager.getParty().increaseDungeonLevel();
        return this.postMoveScript;
    }
    
    @Override
    public int getLayer() {
        return MapConstants.LAYER_OBJECT;
    }

    @Override
    public String getDescription() {
        return "Stairs Into lead deeper into the depths of the dungeon.";
    }

    @Override
    public int getCustomFormat() {
        return 0;
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
    public boolean isRequired() {
        return true;
    }

    @Override
    public int getMinimumRequiredQuantity(Map map) {
        return map.getRows() * 2;
    }

    @Override
    public int getMaximumRequiredQuantity(Map map) {
        return map.getRows() * 4;
    }
}
