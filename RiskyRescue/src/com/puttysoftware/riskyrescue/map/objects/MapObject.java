/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import java.io.IOException;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.riskyrescue.assets.ImageManager;
import com.puttysoftware.riskyrescue.assets.ObjectImage;
import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.map.Map;
import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScript;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptActionCode;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntry;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptEntryArgument;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public abstract class MapObject implements RandomGenerationRule {
    // Properties
    private final ObjectImage imageDesc;
    private final boolean solid;
    private final boolean blocksLOS;
    private MapObject saved;
    public static final int DEFAULT_CUSTOM_VALUE = 0;
    protected static final int CUSTOM_FORMAT_MANUAL_OVERRIDE = -1;

    // Constructors
    protected MapObject(final ObjectImage oi, final boolean isSolid,
            final boolean sightBlock) {
        this.imageDesc = oi;
        this.solid = isSolid;
        this.blocksLOS = sightBlock;
    }

    // Methods
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.blocksLOS ? 1231 : 1237);
        result = prime * result
                + ((this.saved == null) ? 0 : this.saved.hashCode());
        return prime * result + (this.solid ? 1231 : 1237);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MapObject)) {
            return false;
        }
        MapObject other = (MapObject) obj;
        if (this.blocksLOS != other.blocksLOS) {
            return false;
        }
        if (this.saved == null) {
            if (other.saved != null) {
                return false;
            }
        } else if (!this.saved.equals(other.saved)) {
            return false;
        }
        if (this.solid != other.solid) {
            return false;
        }
        return true;
    }

    public final MapObject getSavedObject() {
        return this.saved;
    }

    public final void setSavedObject(MapObject newSaved) {
        this.saved = newSaved;
    }

    /**
     *
     * @param map
     * @param z
     * @return
     */
    public boolean isConditionallySolid(Map map, int z) {
        return this.solid;
    }

    public final boolean isSolid() {
        return this.solid;
    }

    public final boolean isSightBlocking() {
        return this.blocksLOS;
    }

    public final boolean isSolidInBattle() {
        // Handle disabled objects
        if (this.enabledInBattle()) {
            return this.solid;
        } else {
            return false;
        }
    }

    // Scripting
    /**
     *
     * @param ie
     * @param dirX
     * @param dirY
     * @param dirZ
     * @param map
     * @return
     */
    public boolean preMoveCheck(final boolean ie, final int dirX,
            final int dirY, final int dirZ, final Map map) {
        return true;
    }

    public final boolean arrowHitCheck() {
        return !this.isSolid();
    }

    /**
     *
     * @param ie
     * @param dirX
     * @param dirY
     * @param dirZ
     * @return
     */
    public InternalScript getPostMoveScript(final boolean ie, final int dirX,
            final int dirY, final int dirZ) {
        InternalScript scpt = new InternalScript();
        InternalScriptEntry act0 = new InternalScriptEntry();
        act0.setActionCode(InternalScriptActionCode.SOUND);
        act0.addActionArg(new InternalScriptEntryArgument(SoundConstants.STEP));
        act0.finalizeActionArgs();
        scpt.addAction(act0);
        scpt.finalizeActions();
        return scpt;
    }

    /**
     *
     * @param invoker
     * @return
     */
    public InternalScript getBattlePostMoveScript(
            final BattleCharacter invoker) {
        return null;
    }

    /**
     *
     * @param ie
     * @param dirX
     * @param dirY
     * @param dirZ
     * @return
     */
    public static InternalScript getMoveFailedScript(final boolean ie,
            final int dirX, final int dirY, final int dirZ) {
        InternalScript scpt = new InternalScript();
        InternalScriptEntry act0 = new InternalScriptEntry();
        act0.setActionCode(InternalScriptActionCode.SOUND);
        act0.addActionArg(
                new InternalScriptEntryArgument(SoundConstants.ACTION_FAILED));
        act0.finalizeActionArgs();
        scpt.addAction(act0);
        InternalScriptEntry act1 = new InternalScriptEntry();
        act1.setActionCode(InternalScriptActionCode.MESSAGE);
        act1.addActionArg(new InternalScriptEntryArgument("Can't go that way"));
        act1.finalizeActionArgs();
        scpt.addAction(act1);
        scpt.finalizeActions();
        return scpt;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param map
     * @return
     */
    public String gameRenderHook(int x, int y, int z, Map map) {
        return this.getGameCacheName();
    }

    public BufferedImageIcon getImage() {
        return ImageManager.getObjectImage(PartyManager.getMapLevel(),
                this.imageDesc);
    }

    public boolean defersSetProperties() {
        return false;
    }

    public boolean overridesDefaultPostMove() {
        return false;
    }

    public static int getBattleMoveSoundID() {
        return SoundConstants.STEP;
    }

    public String getGameName() {
        return this.getName();
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @param map
     */
    public void determineCurrentAppearance(int x, int y, int z, Map map) {
        // Do nothing
    }

    public boolean hideFromHelp() {
        return false;
    }

    abstract public String getName();

    private String getGameImageName() {
        return this.getGameImageNameHook();
    }

    private String getGameCacheName() {
        return this.getGameImageName();
    }

    final String getEditorImageName() {
        return this.getEditorImageNameHook();
    }

    protected String getGameImageNameHook() {
        return this.getGameName();
    }

    protected String getEditorImageNameHook() {
        return this.getName();
    }

    private String getIdentifier() {
        return this.getName();
    }

    public boolean enabledInBattle() {
        return true;
    }

    public static int getBattleAPCost() {
        return 1;
    }

    abstract public String getPluralName();

    abstract public String getDescription();

    abstract public int getLayer();

    abstract public int getCustomProperty(int propID);

    abstract public void setCustomProperty(int propID, int value);

    public int getCustomFormat() {
        return 0;
    }

    @Override
    public boolean shouldGenerateObject(Map map, int row, int col, int floor,
            int level, int layer) {
        if (layer == MapConstants.LAYER_OBJECT) {
            // Handle object layer
            if (!(this instanceof Empty)) {
                // Limit generation of other objects to 20%, unless required
                if (this.isRequired(-2)) {
                    return true;
                } else {
                    RandomRange r = new RandomRange(1, 100);
                    if (r.generate() <= 20) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                // Generate pass-through objects at 100%
                return true;
            }
        } else {
            // Handle ground layer
            if (this instanceof HazardousGround) {
                // Limit generation of fields to 20%
                RandomRange r = new RandomRange(1, 100);
                if (r.generate() <= 20) {
                    return true;
                } else {
                    return false;
                }
            } else {
                // Generate other ground at 100%
                return true;
            }
        }
    }

    @Override
    public int getMinimumRequiredQuantity(Map map, int level) {
        return RandomGenerationRule.NO_LIMIT;
    }

    @Override
    public int getMaximumRequiredQuantity(Map map, int level) {
        return RandomGenerationRule.NO_LIMIT;
    }

    @Override
    public boolean isRequired(int level) {
        return false;
    }

    @Override
    public boolean shouldGenerateObjectInBattle(Map map, int row, int col,
            int floor, int level, int layer) {
        if (!this.enabledInBattle()) {
            // Don't generate disabled objects
            return false;
        } else {
            // Generate other objects at 100%
            return true;
        }
    }

    @Override
    public int getMinimumRequiredQuantityInBattle(Map map) {
        return RandomGenerationRule.NO_LIMIT;
    }

    @Override
    public int getMaximumRequiredQuantityInBattle(Map map) {
        return RandomGenerationRule.NO_LIMIT;
    }

    @Override
    public boolean isRequiredInBattle() {
        return false;
    }

    public final void writeMapObject(XDataWriter writer) throws IOException {
        writer.writeString(this.getIdentifier());
        int cc = this.getCustomFormat();
        if (cc == MapObject.CUSTOM_FORMAT_MANUAL_OVERRIDE) {
            this.writeMapObjectHook(writer);
        } else {
            for (int x = 0; x < cc; x++) {
                int cx = this.getCustomProperty(x + 1);
                writer.writeInt(cx);
            }
        }
    }

    final MapObject readMapObject(XDataReader reader, String ident, int ver)
            throws IOException {
        if (ident.equals(this.getIdentifier())) {
            int cc = this.getCustomFormat();
            if (cc == MapObject.CUSTOM_FORMAT_MANUAL_OVERRIDE) {
                return this.readMapObjectHook(reader, ver);
            } else {
                for (int x = 0; x < cc; x++) {
                    int cx = reader.readInt();
                    this.setCustomProperty(x + 1, cx);
                }
            }
            return this;
        } else {
            return null;
        }
    }

    /**
     *
     * @param writer
     * @throws IOException
     */
    protected void writeMapObjectHook(XDataWriter writer) throws IOException {
        // Do nothing - but let subclasses override
    }

    /**
     *
     * @param reader
     * @param formatVersion
     * @return
     * @throws IOException
     */
    protected MapObject readMapObjectHook(XDataReader reader, int formatVersion)
            throws IOException {
        // Dummy implementation, subclasses can override
        return this;
    }
}
