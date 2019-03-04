/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map;

import com.puttysoftware.storage.ObjectStorage;

class LowLevelNoteDataStore extends ObjectStorage {
    // Constructor
    LowLevelNoteDataStore(int... shape) {
        super(shape);
    }

    // Methods
    public MapNote getNote(int... loc) {
        return (MapNote) this.getCell(loc);
    }

    public void setNote(MapNote obj, int... loc) {
        this.setCell(obj, loc);
    }
}
