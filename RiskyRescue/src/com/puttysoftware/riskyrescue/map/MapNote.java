/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class MapNote {
    // Fields
    private String contents;

    // Constructor
    public MapNote() {
        this.contents = "Empty Note";
    }

    // Methods
    public String getContents() {
        return this.contents;
    }

    public void setContents(String newContents) {
        this.contents = newContents;
    }

    static MapNote readNote(XDataReader reader) throws IOException {
        MapNote mn = new MapNote();
        mn.contents = reader.readString();
        return mn;
    }

    void writeNote(XDataWriter writer) throws IOException {
        writer.writeString(this.contents);
    }
}
