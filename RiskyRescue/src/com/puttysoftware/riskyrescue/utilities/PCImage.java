package com.puttysoftware.riskyrescue.utilities;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PCImage {
    // Enumerations
    private static enum Clothing {
        GREEN, RED, YELLOW, CYAN, MAGENTA, BLUE;
    }

    private static enum Skin {
        DARKEST, DARKER, DARK, RED, TAN, LIGHT, LIGHTER, LIGHTEST;
    }

    private static enum Hair {
        BLACK, DARK_BROWN, BROWN, LIGHT_BROWN, RED, VERY_DARK_GOLD, DARK_GOLD, GOLD, LIGHT_GOLD, VERY_LIGHT_GOLD;
    }

    // Fields
    private final Clothing clothing;
    private final Skin skin;
    private final Hair hair;

    // Constructor
    public PCImage(final int c, final int s, final int h) {
        super();
        this.clothing = Clothing.values()[c];
        this.skin = Skin.values()[s];
        this.hair = Hair.values()[h];
    }

    public static String getPCImageName(final int c, final int s, final int h) { // NO_UCD
                                                                                 // (actually
                                                                                 // used)
        return Integer.toString(c) + Integer.toString(s) + Integer.toString(h);
    }

    public String getImageName() {
        return Integer.toString(this.clothing.ordinal())
                + Integer.toString(this.skin.ordinal())
                + Integer.toString(this.hair.ordinal());
    }

    public static PCImage read(XDataReader worldFile) throws IOException {
        int c = worldFile.readInt();
        int s = worldFile.readInt();
        int h = worldFile.readInt();
        return new PCImage(c, s, h);
    }

    public void write(XDataWriter worldFile) throws IOException {
        worldFile.writeInt(this.clothing.ordinal());
        worldFile.writeInt(this.skin.ordinal());
        worldFile.writeInt(this.hair.ordinal());
    }

    public static String[] getClothingNames() { // NO_UCD (actually used)
        Clothing[] values = Clothing.values();
        String[] names = new String[values.length];
        for (int n = 0; n < names.length; n++) {
            String[] raw = values[n].name().split("_");
            StringBuilder rawBuilder = new StringBuilder();
            for (int r = 0; r < raw.length; r++) {
                rawBuilder.append(raw[r].substring(0, 1).toUpperCase());
                rawBuilder.append(raw[r].substring(1).toLowerCase());
                if (r < raw.length - 1) {
                    rawBuilder.append(" ");
                }
            }
            names[n] = rawBuilder.toString();
        }
        return names;
    }

    public static String[] getSkinNames() { // NO_UCD (actually used)
        Skin[] values = Skin.values();
        String[] names = new String[values.length];
        for (int n = 0; n < names.length; n++) {
            String[] raw = values[n].name().split("_");
            StringBuilder rawBuilder = new StringBuilder();
            for (int r = 0; r < raw.length; r++) {
                rawBuilder.append(raw[r].substring(0, 1).toUpperCase());
                rawBuilder.append(raw[r].substring(1).toLowerCase());
                if (r < raw.length - 1) {
                    rawBuilder.append(" ");
                }
            }
            names[n] = rawBuilder.toString();
        }
        return names;
    }

    public static String[] getHairNames() { // NO_UCD (actually used)
        Hair[] values = Hair.values();
        String[] names = new String[values.length];
        for (int n = 0; n < names.length; n++) {
            String[] raw = values[n].name().split("_");
            StringBuilder rawBuilder = new StringBuilder();
            for (int r = 0; r < raw.length; r++) {
                rawBuilder.append(raw[r].substring(0, 1).toUpperCase());
                rawBuilder.append(raw[r].substring(1).toLowerCase());
                if (r < raw.length - 1) {
                    rawBuilder.append(" ");
                }
            }
            names[n] = rawBuilder.toString();
        }
        return names;
    }
}
