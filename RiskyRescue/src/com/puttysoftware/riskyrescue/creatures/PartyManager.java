/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell


 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures;

import java.io.IOException;

import javax.swing.JFrame;

import com.puttysoftware.riskyrescue.utilities.PCImage;
import com.puttysoftware.riskyrescue.utilities.PCImagePickerDialog;
import com.puttysoftware.riskyrescue.utilities.PCNameGenerator;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PartyManager {
    // Fields
    private static PCImage buddyPCI;
    private static Party party;
    private static final int PARTY_SIZE = 2;

    // Constructors
    private PartyManager() {
        // Do nothing
    }

    // Methods
    public static boolean createParty(JFrame owner) {
        PartyManager.party = new Party(PartyManager.PARTY_SIZE);
        int mem = 0;
        PartyMember hero = PartyManager.createHero(owner);
        if (hero != null) {
            PartyManager.party.addPartyMember(hero);
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
        String name = PCNameGenerator.generate(); // Make a random buddy name
        PartyMember buddy = new PartyMember(PartyManager.buddyPCI, name);
        PartyManager.party.addPartyMember(buddy);
    }

    private static PartyMember createHero(JFrame owner) {
        String name = PCNameGenerator.generate(); // Make a random hero name
        PCImage pci = PCImagePickerDialog.showDialog(owner, "Pick Hero Image");
        if (pci != null) {
            PCImage newBuddyPCI = PCImagePickerDialog.showDialog(owner,
                    "Pick Buddy Image");
            if (newBuddyPCI != null) {
                PartyManager.buddyPCI = newBuddyPCI;
                return new PartyMember(pci, name);
            }
        }
        return null;
    }

    public static String showCreationDialog(JFrame owner, String labelText,
            String title, String[] input, String[] descriptions) {
        return ListWithDescDialog.showDialog(owner, null, labelText, title,
                input, input[0], descriptions[0], descriptions);
    }
}
