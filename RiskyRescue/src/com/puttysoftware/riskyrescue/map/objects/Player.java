/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import java.io.IOException;

import com.puttysoftware.riskyrescue.creatures.party.PartyMember;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Player extends BattleCharacter {
    // Constructors
    public Player(final PartyMember hero) {
        super(hero);
        this.setSavedObject(new Empty());
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
        return "Player";
    }

    @Override
    public String getPluralName() {
        return "Players";
    }

    @Override
    public String getDescription() {
        return "This is you - the Player.";
    }
}