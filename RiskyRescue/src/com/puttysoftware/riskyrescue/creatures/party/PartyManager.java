/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell


 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures.party;

import java.io.IOException;

import javax.swing.JFrame;

import com.puttysoftware.riskyrescue.utilities.PCImage;
import com.puttysoftware.riskyrescue.utilities.PCImagePickerDialog;
import com.puttysoftware.riskyrescue.utilities.PCNameGenerator;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PartyManager {
    // Fields
    private static Party party;

    // Constructors
    private PartyManager() {
        // Do nothing
    }

    // Methods
    public static boolean createParty(JFrame owner) {
        PartyManager.party = new Party();
        int mem = 0;
        PartyMember hero = PartyManager.createHero(owner);
        if (hero != null) {
            PartyManager.party.addHero(hero);
            mem++;
        }
        if (mem == 0) {
            return false;
        }
        return true;
    }

    public static Party getParty() {
        return PartyManager.party;
    }

    public static void loadGameHook(XDataReader partyFile) throws IOException {
        boolean containsPCData = partyFile.readBoolean();
        if (containsPCData) {
            PartyManager.party = Party.read(partyFile);
        }
    }

    public static void saveGameHook(XDataWriter partyFile) throws IOException {
        if (PartyManager.party != null) {
            partyFile.writeBoolean(true);
            PartyManager.party.write(partyFile);
        } else {
            partyFile.writeBoolean(false);
        }
    }

    public static void revivePartyFully() {
        if (PartyManager.party != null) {
            PartyManager.party.revivePartyFully();
        }
    }

    public static void addBuddy() {
        PartyManager.party.activateBuddy();
    }

    private static PartyMember createHero(JFrame owner) {
        String heroName = PCNameGenerator.generate(); // Make a random hero name
        String buddyName = PCNameGenerator.generate(); // Make a random
                                                       // buddy name
        PCImage pci = PCImagePickerDialog.showDialog(owner,
                heroName + " (Hero): Pick Image");
        if (pci != null) {
            PCImage newBuddyPCI = PCImagePickerDialog.showDialog(owner,
                    buddyName + " (Buddy): Pick Image");
            if (newBuddyPCI != null) {
                PCImage buddyPCI = newBuddyPCI;
                PartyMember buddy = new PartyMember(buddyPCI, buddyName);
                PartyManager.party.addBuddy(buddy);
                return new PartyMember(pci, heroName);
            }
        }
        return null;
    }
}
