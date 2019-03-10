/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.items;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Equipment extends Item {
    // Properties
    private final int equipCat;
    private final int materialID;
    private int firstSlotUsed;
    private int secondSlotUsed;
    private boolean conditionalSlot;

    // Constructors
    private Equipment(Item i, int equipCategory, int newMaterialID) {
        super(i.getName(), i.getInitialUses(), i.getWeightPerUse());
        this.equipCat = equipCategory;
        this.materialID = newMaterialID;
        this.firstSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.secondSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.conditionalSlot = false;
    }

    Equipment(final String itemName, final int itemInitialUses,
            final int itemWeightPerUse, int equipCategory,
            final int newMaterialID) {
        super(itemName, itemInitialUses, itemWeightPerUse);
        this.equipCat = equipCategory;
        this.materialID = newMaterialID;
        this.firstSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.secondSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.conditionalSlot = false;
    }

    // Methods
    final int getFirstSlotUsed() {
        return this.firstSlotUsed;
    }

    final void setFirstSlotUsed(int newFirstSlotUsed) {
        this.firstSlotUsed = newFirstSlotUsed;
    }

    final int getSecondSlotUsed() {
        return this.secondSlotUsed;
    }

    final void setSecondSlotUsed(int newSecondSlotUsed) {
        this.secondSlotUsed = newSecondSlotUsed;
    }

    final void setConditionalSlot(boolean newConditionalSlot) {
        this.conditionalSlot = newConditionalSlot;
    }

    public final int getEquipCategory() {
        return this.equipCat;
    }

    static Equipment readEquipment(XDataReader dr) throws IOException {
        Item i = Item.readItem(dr);
        if (i == null) {
            // Abort
            return null;
        }
        int matID = dr.readInt();
        int eCat = dr.readInt();
        Equipment ei = new Equipment(i, eCat, matID);
        ei.firstSlotUsed = dr.readInt();
        ei.secondSlotUsed = dr.readInt();
        ei.conditionalSlot = dr.readBoolean();
        return ei;
    }

    final void writeEquipment(XDataWriter dw) throws IOException {
        super.writeItem(dw);
        dw.writeInt(this.materialID);
        dw.writeInt(this.equipCat);
        dw.writeInt(this.firstSlotUsed);
        dw.writeInt(this.secondSlotUsed);
        dw.writeBoolean(this.conditionalSlot);
    }
}
