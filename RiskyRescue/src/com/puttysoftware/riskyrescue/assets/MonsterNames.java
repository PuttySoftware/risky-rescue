/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.assets;

import com.puttysoftware.riskyrescue.assets.data.ImageDataManager;
import com.puttysoftware.riskyrescue.assets.data.MonsterDataManager;

public class MonsterNames {
    // Fields
    private static final int CACHE_ROWS = 5;
    private static String[][] NAME_CACHE;
    private static boolean NAME_CACHE_CREATED = false;
    private static boolean[] NAME_INDEX_CACHE_CREATED = new boolean[MonsterNames.CACHE_ROWS];
    private static String[][] IMAGE_CACHE;
    private static boolean IMAGE_CACHE_CREATED = false;
    private static boolean[] IMAGE_INDEX_CACHE_CREATED = new boolean[MonsterNames.CACHE_ROWS];

    public static String[] getAllNames(final int index) {
        if (!MonsterNames.NAME_CACHE_CREATED) {
            MonsterNames.NAME_CACHE = new String[MonsterNames.CACHE_ROWS][];
            MonsterNames.NAME_CACHE_CREATED = true;
            if (!MonsterNames.NAME_INDEX_CACHE_CREATED[index]) {
                MonsterNames.NAME_CACHE[index] = MonsterDataManager
                        .getMonsterData(index);
                MonsterNames.NAME_INDEX_CACHE_CREATED[index] = true;
            }
        }
        return MonsterNames.NAME_CACHE[index];
    }

    public static String getImageName(final int dungeonIndex,
            final int nameIndex) {
        if (!MonsterNames.IMAGE_CACHE_CREATED) {
            MonsterNames.IMAGE_CACHE = new String[MonsterNames.CACHE_ROWS][];
            MonsterNames.IMAGE_CACHE_CREATED = true;
            if (!MonsterNames.IMAGE_INDEX_CACHE_CREATED[dungeonIndex]) {
                MonsterNames.IMAGE_CACHE[dungeonIndex] = ImageDataManager
                        .getMonsterImageData(dungeonIndex);
                MonsterNames.IMAGE_INDEX_CACHE_CREATED[dungeonIndex] = true;
            }
        }
        return MonsterNames.IMAGE_CACHE[dungeonIndex][nameIndex];
    }
}
