/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import java.io.IOException;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Buddy extends BattleCharacter {
    // Constructors
    public Buddy() {
        super(PartyManager.getParty().getMember(1));
        this.setSavedObject(new Empty());
    }

    @Override
    public boolean preMoveCheck(boolean inIe, int inDirX, int inDirY,
            int inDirZ, Map inMap) {
        // Your buddy disappears...
        inMap.setCell(this.getSavedObject(), inDirX, inDirY, inDirZ,
                this.getLayer());
        // ...and joins your party!
        PartyManager.addBuddy();
        SoundManager.playSound(SoundConstants.VICTORY);
        CommonDialogs
                .showDialog("You have found your buddy! Time to head out!");
        return false;
    }

    @Override
    public int getLayer() {
        return MapConstants.LAYER_OBJECT;
    }

    @Override
    public int getCustomFormat() {
        return MapObject.CUSTOM_FORMAT_MANUAL_OVERRIDE;
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
    protected void writeMapObjectHook(XDataWriter writer) throws IOException {
        this.getSavedObject().writeMapObject(writer);
    }

    @Override
    protected MapObject readMapObjectHook(XDataReader reader, int formatVersion)
            throws IOException {
        this.setSavedObject(
                new MapObjectList().readMapObjectX(reader, formatVersion));
        return this;
    }

    @Override
    public String getName() {
        return "Buddy";
    }

    @Override
    public String getPluralName() {
        return "Buddies";
    }

    @Override
    public String getDescription() {
        return "This is your buddy.";
    }
}