/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.assets;

import com.puttysoftware.riskyrescue.assets.data.ImageDataManager;

public class MonsterImageNames {
    // Fields
    private static final int CACHE_ROWS = 5;
    private static String[][] CACHE;
    private static boolean CACHE_CREATED = false;
    private static boolean[] INDEX_CACHE_CREATED = new boolean[MonsterImageNames.CACHE_ROWS];

    public static String[] getAllNames(final int index) {
        if (!MonsterImageNames.CACHE_CREATED) {
            MonsterImageNames.CACHE = new String[MonsterImageNames.CACHE_ROWS][];
            MonsterImageNames.CACHE_CREATED = true;
            if (!MonsterImageNames.INDEX_CACHE_CREATED[index]) {
                MonsterImageNames.CACHE[index] = ImageDataManager
                        .getMonsterImageData(index);
                MonsterImageNames.INDEX_CACHE_CREATED[index] = true;
            }
        }
        return MonsterImageNames.CACHE[index];
    }
}
