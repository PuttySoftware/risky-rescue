package com.puttysoftware.riskyrescue.game;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.riskyrescue.Support;
import com.puttysoftware.riskyrescue.creatures.PartyManager;
import com.puttysoftware.riskyrescue.creatures.PartyMember;

public final class InventoryViewer {
    private InventoryViewer() {
        // Do nothing
    }

    public static void showEquipmentDialog() {
        String title;
        if (Support.inDebugMode()) {
            title = "Equipment (DEBUG)";
        } else {
            title = "Equipment";
        }
        PartyMember member = PartyManager.getParty().getLeader();
        if (member != null) {
            String[] equipString = member.getItems()
                    .generateEquipmentStringArray();
            CommonDialogs.showInputDialog("Equipment", title, equipString,
                    equipString[0]);
        }
    }
}
