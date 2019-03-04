/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.riskyrescue.map.MapConstants;
import com.puttysoftware.riskyrescue.scenario.FormatConstants;
import com.puttysoftware.xio.XDataReader;

public class MapObjectList {
    // Fields
    private final ArrayList<MapObject> allObjects;

    // Constructor
    public MapObjectList() {
        MapObject[] allDefaultObjects = { new Empty(), new Tile(),
                new HazardousGround(), new Player(), new Wall(), new WallOff(),
                new WallOn(), new ClosedDoor(), new OpenDoor(), new Button(),
                new Teleport(), new StairsDown(), new StairsUp(),
                new SealingWall(), new ArmorShop(), new HealShop(),
                new Regenerator(), new WeaponsShop() };
        this.allObjects = new ArrayList<>();
        for (int x = 0; x < allDefaultObjects.length; x++) {
            this.allObjects.add(allDefaultObjects[x]);
        }
    }

    // Methods
    private MapObject[] getAllObjects() {
        return this.allObjects.toArray(new MapObject[this.allObjects.size()]);
    }

    public String[] getAllDescriptions() {
        MapObject[] objs = this.getAllObjects();
        String[] tempAllDescriptions = new String[objs.length];
        int x;
        int count = 0;
        for (x = 0; x < objs.length; x++) {
            if (!objs[x].hideFromHelp()) {
                tempAllDescriptions[count] = objs[x].getDescription();
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            String[] allDescriptions = new String[count];
            for (x = 0; x < count; x++) {
                allDescriptions[x] = tempAllDescriptions[x];
            }
            return allDescriptions;
        }
    }

    public MapObject[] getAllGroundLayerObjects() {
        MapObject[] objs = this.getAllObjects();
        MapObject[] tempAllGroundLayerObjects = new MapObject[this
                .getAllObjects().length];
        int objectCount = 0;
        for (int x = 0; x < objs.length; x++) {
            if (objs[x].getLayer() == MapConstants.LAYER_GROUND) {
                tempAllGroundLayerObjects[x] = objs[x];
            }
        }
        for (int x = 0; x < tempAllGroundLayerObjects.length; x++) {
            if (tempAllGroundLayerObjects[x] != null) {
                objectCount++;
            }
        }
        MapObject[] allGroundLayerObjects = new MapObject[objectCount];
        objectCount = 0;
        for (int x = 0; x < tempAllGroundLayerObjects.length; x++) {
            if (tempAllGroundLayerObjects[x] != null) {
                allGroundLayerObjects[objectCount] = tempAllGroundLayerObjects[x];
                objectCount++;
            }
        }
        return allGroundLayerObjects;
    }

    public final MapObject[] getAllRequired(int layer) {
        MapObject[] objs = this.getAllObjects();
        MapObject[] tempAllRequired = new MapObject[objs.length];
        int x;
        int count = 0;
        for (x = 0; x < objs.length; x++) {
            if ((objs[x].getLayer() == layer) && objs[x].isRequired()) {
                tempAllRequired[count] = objs[x];
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            MapObject[] allRequired = new MapObject[count];
            for (x = 0; x < count; x++) {
                allRequired[x] = tempAllRequired[x];
            }
            return allRequired;
        }
    }

    public final MapObject[] getAllNotRequired(int layer) {
        MapObject[] objs = this.getAllObjects();
        MapObject[] tempAllWithoutPrereq = new MapObject[objs.length];
        int x;
        int count = 0;
        for (x = 0; x < objs.length; x++) {
            if ((objs[x].getLayer() == layer) && !(objs[x].isRequired())) {
                tempAllWithoutPrereq[count] = objs[x];
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            MapObject[] allWithoutPrereq = new MapObject[count];
            for (x = 0; x < count; x++) {
                allWithoutPrereq[x] = tempAllWithoutPrereq[x];
            }
            return allWithoutPrereq;
        }
    }

    public final MapObject[] getAllRequiredInBattle(int layer) {
        MapObject[] objs = this.getAllObjects();
        MapObject[] tempAllRequired = new MapObject[objs.length];
        int x;
        int count = 0;
        for (x = 0; x < objs.length; x++) {
            if ((objs[x].getLayer() == layer) && objs[x].isRequiredInBattle()) {
                tempAllRequired[count] = objs[x];
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            MapObject[] allRequired = new MapObject[count];
            for (x = 0; x < count; x++) {
                allRequired[x] = tempAllRequired[x];
            }
            return allRequired;
        }
    }

    public final MapObject[] getAllNotRequiredInBattle(int layer) {
        MapObject[] objs = this.getAllObjects();
        MapObject[] tempAllWithoutPrereq = new MapObject[objs.length];
        int x;
        int count = 0;
        for (x = 0; x < objs.length; x++) {
            if ((objs[x].getLayer() == layer)
                    && !(objs[x].isRequiredInBattle())) {
                tempAllWithoutPrereq[count] = objs[x];
                count++;
            }
        }
        if (count == 0) {
            return null;
        } else {
            MapObject[] allWithoutPrereq = new MapObject[count];
            for (x = 0; x < count; x++) {
                allWithoutPrereq[x] = tempAllWithoutPrereq[x];
            }
            return allWithoutPrereq;
        }
    }

    public final MapObject getInstanceByName(String name) {
        if (name == null) {
            // No object specified, give up
            return null;
        }
        MapObject[] objs = this.getAllObjects();
        MapObject instance = null;
        int x;
        for (x = 0; x < objs.length; x++) {
            if (objs[x].getName().equals(name)) {
                instance = objs[x];
                break;
            }
        }
        return instance;
    }

    public MapObject readMapObjectX(XDataReader reader, int formatVersion)
            throws IOException {
        MapObject[] objs = this.getAllObjects();
        MapObject o = null;
        String UID = "";
        if (formatVersion == FormatConstants.SCENARIO_FORMAT_1) {
            UID = reader.readString();
        }
        for (int x = 0; x < objs.length; x++) {
            MapObject instance = objs[x];
            if (formatVersion == FormatConstants.SCENARIO_FORMAT_1) {
                o = instance.readMapObject(reader, UID, formatVersion);
            }
            if (o != null) {
                return o;
            }
        }
        // Failed, object not found
        return null;
    }
}
