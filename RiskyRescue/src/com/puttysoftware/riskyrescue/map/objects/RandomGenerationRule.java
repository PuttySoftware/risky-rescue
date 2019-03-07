/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.map.objects;

import com.puttysoftware.riskyrescue.map.Map;

public interface RandomGenerationRule {
    public static final int NO_LIMIT = 0;

    public boolean shouldGenerateObject(Map map, int row, int col, int floor,
            int level, int layer);

    public int getMinimumRequiredQuantity(Map map, int level);

    public int getMaximumRequiredQuantity(Map map, int level);

    public boolean isRequired(int level);

    public boolean shouldGenerateObjectInBattle(Map map, int row, int col,
            int floor, int level, int layer);

    public int getMinimumRequiredQuantityInBattle(Map map);

    public int getMaximumRequiredQuantityInBattle(Map map);

    public boolean isRequiredInBattle();
}
