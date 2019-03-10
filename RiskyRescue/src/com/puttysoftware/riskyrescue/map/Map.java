/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.riskyrescue.Support;
import com.puttysoftware.riskyrescue.map.objects.Empty;
import com.puttysoftware.riskyrescue.map.objects.MapObject;
import com.puttysoftware.riskyrescue.map.objects.Tile;
import com.puttysoftware.riskyrescue.scenario.FormatConstants;
import com.puttysoftware.riskyrescue.scripts.internal.InternalScriptArea;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Map implements MapConstants {
    // Properties
    private LayeredTower[] mapData;
    private int startW;
    private int locW;
    private int saveW;
    private int levelCount;
    private int activeLevel;
    private String mapTitle;
    private PrefixIO xmlPrefixHandler;
    private SuffixIO xmlSuffixHandler;
    private final String mapBasePath;
    private static final int MAX_LEVELS = 5;

    // Constructors
    public Map() {
        this.mapData = new LayeredTower[Map.MAX_LEVELS];
        this.levelCount = 0;
        this.startW = 0;
        this.locW = 0;
        this.saveW = 0;
        this.activeLevel = 0;
        this.xmlPrefixHandler = null;
        this.xmlSuffixHandler = null;
        this.mapTitle = "Untitled Map";
        this.mapBasePath = Support.getScenario().getBasePath() + File.separator
                + "maps" + File.separator;
    }

    // Methods
    public Map createMaps() {
        File mapDir = new File(this.mapBasePath);
        if (!mapDir.exists()) {
            mapDir.mkdirs();
        }
        return this;
    }

    public static int getLastLevelNumber() {
        return Map.MAX_LEVELS - 1;
    }

    public static Map getTemporaryBattleCopy() {
        Map temp = new Map();
        temp.addLevel(Support.getBattleMapSize(), Support.getBattleMapSize(),
                Support.getBattleMapFloorSize());
        temp.fillLevel(new Tile(), new Empty());
        return temp;
    }

    public void rebuildGSA(int mod) {
        this.mapData[this.activeLevel].rebuildGSA(mod);
    }

    public void setXPrefixHandler(PrefixIO xph) {
        this.xmlPrefixHandler = xph;
    }

    public void setXSuffixHandler(SuffixIO xsh) {
        this.xmlSuffixHandler = xsh;
    }

    public int getRegionSize() {
        return this.mapData[this.activeLevel].getRegionSize();
    }

    public boolean isLevelOffsetValid(int level) {
        return (this.activeLevel + level) >= 0;
    }

    public void switchLevelOffset(int level) {
        this.switchLevelInternal(this.activeLevel + level);
    }

    private void switchLevelInternal(int level) {
        if (this.activeLevel != level) {
            this.activeLevel = level;
        }
    }

    public boolean doesLevelExistOffset(int level) {
        if (this.activeLevel + level < 0) {
            return false;
        } else if (this.activeLevel + level >= this.levelCount) {
            return false;
        } else {
            return true;
        }
    }

    public void resetVisibleSquares() {
        this.mapData[this.activeLevel].resetVisibleSquares();
    }

    public void updateVisibleSquares(int xp, int yp, int zp) {
        this.mapData[this.activeLevel].updateVisibleSquares(xp, yp, zp);
    }

    public boolean addLevel(final int rows, final int cols, final int floors) {
        if (this.levelCount < Map.MAX_LEVELS) {
            this.levelCount++;
            this.activeLevel = this.levelCount - 1;
            this.mapData[this.activeLevel] = new LayeredTower(rows, cols,
                    floors);
            return true;
        } else {
            return false;
        }
    }

    public MapObject getBattleCell(final int row, final int col) {
        return this.mapData[this.activeLevel].getCell(row, col, 0,
                MapConstants.LAYER_OBJECT);
    }

    public MapObject getBattleGround(final int row, final int col) {
        return this.mapData[this.activeLevel].getCell(row, col, 0,
                MapConstants.LAYER_GROUND);
    }

    public MapObject getCell(final int row, final int col, final int floor,
            final int extra) {
        return this.mapData[this.activeLevel].getCell(row, col, floor, extra);
    }

    public int getPlayerLocationX() {
        return this.mapData[this.activeLevel].getPlayerRow();
    }

    public int getPlayerLocationY() {
        return this.mapData[this.activeLevel].getPlayerColumn();
    }

    public int getPlayerLocationZ() {
        return this.mapData[this.activeLevel].getPlayerFloor();
    }

    public int getPlayerLocationW() {
        return this.locW;
    }

    public void savePlayerLocation() {
        this.saveW = this.locW;
        this.mapData[this.activeLevel].savePlayerLocation();
    }

    public void restorePlayerLocation() {
        this.locW = this.saveW;
        this.mapData[this.activeLevel].restorePlayerLocation();
    }

    public int getRows() {
        return this.mapData[this.activeLevel].getRows();
    }

    public int getColumns() {
        return this.mapData[this.activeLevel].getColumns();
    }

    public boolean hasNote(int x, int y, int z) {
        return this.mapData[this.activeLevel].hasNote(y, x, z);
    }

    public void createNote(int x, int y, int z) {
        this.mapData[this.activeLevel].createNote(y, x, z);
    }

    public MapNote getNote(int x, int y, int z) {
        return this.mapData[this.activeLevel].getNote(y, x, z);
    }

    public void findAllObjectPairsAndSwap(final MapObject o1,
            final MapObject o2) {
        this.mapData[this.activeLevel].findAllObjectPairsAndSwap(o1, o2);
    }

    public boolean isSquareVisible(int x1, int y1, int x2, int y2) {
        return this.mapData[this.activeLevel].isSquareVisible(x1, y1, x2, y2);
    }

    public void setBattleCell(final MapObject mo, final int row,
            final int col) {
        this.mapData[this.activeLevel].setCell(mo, row, col, 0,
                MapConstants.LAYER_OBJECT);
    }

    public void setCell(final MapObject mo, final int row, final int col,
            final int floor, final int extra) {
        this.mapData[this.activeLevel].setCell(mo, row, col, floor, extra);
    }

    public void offsetPlayerLocationX(final int newPlayerRow) {
        this.mapData[this.activeLevel].offsetPlayerRow(newPlayerRow);
    }

    public void offsetPlayerLocationY(final int newPlayerColumn) {
        this.mapData[this.activeLevel].offsetPlayerColumn(newPlayerColumn);
    }

    public void offsetPlayerLocationZ(final int newPlayerFloor) {
        this.mapData[this.activeLevel].offsetPlayerFloor(newPlayerFloor);
    }

    private void fillLevel(MapObject bottom, MapObject top) {
        this.mapData[this.activeLevel].fill(bottom, top);
    }

    public void fillLevelRandomly(final MapObject pass1FillBottom,
            final MapObject pass1FillTop) {
        this.mapData[this.activeLevel].fillRandomly(this, this.activeLevel,
                pass1FillBottom, pass1FillTop);
    }

    public ArrayList<InternalScriptArea> getScriptAreasAtPoint(Point p, int z) {
        return this.mapData[this.activeLevel].getScriptAreasAtPoint(p, z);
    }

    public Map readMapX() throws IOException {
        Map m = new Map();
        // Attach handlers
        m.setXPrefixHandler(this.xmlPrefixHandler);
        m.setXSuffixHandler(this.xmlSuffixHandler);
        int version = 0;
        // Create metafile reader
        try (XDataReader metaReader = new XDataReader(
                this.mapBasePath + File.separator + "metafile.xml", "map")) {
            // Read metafile
            version = m.readMapMetafileX(metaReader);
        } catch (IOException ioe) {
            throw ioe;
        }
        // Create data reader
        try (XDataReader dataReader = m.getLevelReaderX()) {
            // Read data
            m.readMapLevelX(dataReader, version);
        } catch (IOException ioe) {
            throw ioe;
        }
        return m;
    }

    private XDataReader getLevelReaderX() throws IOException {
        return new XDataReader(this.mapBasePath + File.separator + "level"
                + this.activeLevel + ".xml", "level");
    }

    private int readMapMetafileX(XDataReader reader) throws IOException {
        int ver = FormatConstants.LATEST_SCENARIO_FORMAT;
        if (this.xmlPrefixHandler != null) {
            ver = this.xmlPrefixHandler.readPrefix(reader);
        }
        int levels = reader.readInt();
        this.levelCount = levels;
        this.startW = reader.readInt();
        this.locW = reader.readInt();
        this.saveW = reader.readInt();
        this.mapTitle = reader.readString();
        if (this.xmlSuffixHandler != null) {
            this.xmlSuffixHandler.readSuffix(reader, ver);
        }
        return ver;
    }

    private void readMapLevelX(XDataReader reader, int formatVersion)
            throws IOException {
        if (formatVersion == FormatConstants.SCENARIO_FORMAT_1) {
            this.mapData[this.activeLevel] = LayeredTower
                    .readXLayeredTower(reader, formatVersion);
        } else {
            throw new IOException("Unknown map format version!");
        }
    }

    public void writeMapX() throws IOException {
        // Create metafile writer
        try (XDataWriter metaWriter = new XDataWriter(
                this.mapBasePath + File.separator + "metafile.xml", "map")) {
            // Write metafile
            this.writeMapMetafileX(metaWriter);
        } catch (IOException ioe) {
            throw ioe;
        }
        // Create data writer
        try (XDataWriter dataWriter = this.getLevelWriterX()) {
            // Write data
            this.writeMapLevelX(dataWriter);
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    private XDataWriter getLevelWriterX() throws IOException {
        return new XDataWriter(this.mapBasePath + File.separator + "level"
                + this.activeLevel + ".xml", "level");
    }

    private void writeMapMetafileX(XDataWriter writer) throws IOException {
        if (this.xmlPrefixHandler != null) {
            this.xmlPrefixHandler.writePrefix(writer);
        }
        writer.writeInt(this.levelCount);
        writer.writeInt(this.startW);
        writer.writeInt(this.locW);
        writer.writeInt(this.saveW);
        writer.writeString(this.mapTitle);
        if (this.xmlSuffixHandler != null) {
            this.xmlSuffixHandler.writeSuffix(writer);
        }
    }

    private void writeMapLevelX(XDataWriter writer) throws IOException {
        // Write the level
        this.mapData[this.activeLevel].writeXLayeredTower(writer);
    }
}
