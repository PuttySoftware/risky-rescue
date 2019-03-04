/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.items;

public interface ShopTypes {
    public static final int SHOP_TYPE_WEAPONS = 1;
    public static final int SHOP_TYPE_ARMOR = 2;
    public static final int SHOP_TYPE_HEALER = 3;
    public static final int SHOP_TYPE_REGENERATOR = 4;
    public static final String[] SHOP_NAMES = { "Weapons", "Armor", "Healer",
            "Regenerator" };
}