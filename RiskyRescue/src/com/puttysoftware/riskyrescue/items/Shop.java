/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.items;

import javax.swing.JOptionPane;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.riskyrescue.Support;
import com.puttysoftware.riskyrescue.assets.GameSoundConstants;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.creatures.PartyManager;
import com.puttysoftware.riskyrescue.creatures.PartyMember;

public class Shop implements ShopTypes {
    // Fields
    private final int type;
    private int index;
    private int defaultChoice;
    private String[] choices;
    private String[] typeChoices;
    private int typeDefault;
    private String typeResult;
    private int typeIndex;
    private boolean handIndex;
    private String result;
    private int cost;
    private final int inflationRate;

    // Constructors
    public Shop(final int shopType) {
        super();
        this.type = shopType;
        this.index = 0;
        this.inflationRate = 100;
    }

    // Methods
    static int getEquipmentCost(final int x) {
        return (9 * (x + 1) * (x + 2)) / 2;
    }

    private static int getHealingCost(final int x, final int y, final int z) {
        return (int) (Math.log10(x) * ((z - y)));
    }

    private static int getRegenerationCost(final int x, final int y,
            final int z) {
        int diff = z - y;
        if (diff == 0) {
            return 0;
        } else {
            int cost = (int) ((Math.log(x) / Math.log(2)) * diff);
            if (cost < 1) {
                return 1;
            } else {
                return cost;
            }
        }
    }

    private String getShopNameFromType() {
        if (Support.inDebugMode()) {
            return ShopTypes.SHOP_NAMES[this.type - 1] + " (DEBUG)";
        } else {
            return ShopTypes.SHOP_NAMES[this.type - 1];
        }
    }

    public void showShop() {
        this.index = 0;
        this.defaultChoice = 0;
        this.choices = null;
        this.typeChoices = null;
        this.typeDefault = 0;
        this.typeResult = null;
        this.typeIndex = 0;
        this.handIndex = false;
        this.result = null;
        this.cost = 0;
        boolean valid = this.shopStage1();
        if (valid) {
            valid = this.shopStage2();
            if (valid) {
                valid = this.shopStage3();
                if (valid) {
                    valid = this.shopStage4();
                    if (valid) {
                        valid = this.shopStage5();
                        if (valid) {
                            this.shopStage6();
                        }
                    }
                }
            }
        }
    }

    private boolean shopStage1() {
        // Stage 1
        if (this.type == ShopTypes.SHOP_TYPE_WEAPONS) {
            this.typeChoices = WeaponConstants.getWeaponChoices();
            this.typeDefault = 0;
        } else if (this.type == ShopTypes.SHOP_TYPE_ARMOR) {
            this.typeIndex = EquipmentSlotConstants.SLOT_OFFHAND;
        }
        if (this.typeChoices != null) {
            this.typeResult = CommonDialogs.showInputDialog("Select Type",
                    this.getShopNameFromType(), this.typeChoices,
                    this.typeChoices[this.typeDefault]);
            if (this.typeResult == null) {
                return false;
            }
            this.typeIndex = 0;
            for (this.typeIndex = 0; this.typeIndex < this.typeChoices.length; this.typeIndex++) {
                if (this.typeResult.equals(this.typeChoices[this.typeIndex])) {
                    break;
                }
            }
            if (this.typeIndex == this.typeChoices.length) {
                return false;
            }
            if (this.type == ShopTypes.SHOP_TYPE_ARMOR && this.typeIndex > 1) {
                // Adjust typeIndex, position 2 is blank
                this.typeIndex++;
            }
        }
        return true;
    }

    private boolean shopStage2() {
        // Stage 2
        if (this.type == ShopTypes.SHOP_TYPE_WEAPONS) {
            if (this.typeResult.equals(this.typeChoices[0])) {
                this.choices = EquipmentFactory.createOneHandedWeaponNames();
                // Choose Hand
                String[] handChoices = WeaponConstants.getHandChoices();
                int handDefault = 0;
                String handResult = CommonDialogs.showInputDialog("Select Hand",
                        this.getShopNameFromType(), handChoices,
                        handChoices[handDefault]);
                if (handResult == null) {
                    return false;
                }
                if (handResult.equals(handChoices[0])) {
                    this.handIndex = true;
                } else {
                    this.handIndex = false;
                }
            } else {
                this.choices = EquipmentFactory.createTwoHandedWeaponNames();
            }
        } else if (this.type == ShopTypes.SHOP_TYPE_ARMOR) {
            this.choices = EquipmentFactory.createArmorNames(this.typeIndex);
        } else if (this.type == ShopTypes.SHOP_TYPE_HEALER
                || this.type == ShopTypes.SHOP_TYPE_REGENERATOR) {
            this.choices = new String[10];
            int x;
            for (x = 0; x < this.choices.length; x++) {
                this.choices[x] = Integer.toString((x + 1) * 10) + "%";
            }
            this.defaultChoice = 9;
        } else {
            // Invalid shop type
            return false;
        }
        return true;
    }

    private boolean shopStage3() {
        // Stage 3
        PartyMember playerCharacter = PartyManager.getParty().getLeader();
        // Check
        if (this.type == ShopTypes.SHOP_TYPE_HEALER && playerCharacter
                .getCurrentHP() == playerCharacter.getMaximumHP()) {
            CommonDialogs.showDialog("You don't need healing.");
            return false;
        } else if (this.type == ShopTypes.SHOP_TYPE_REGENERATOR
                && playerCharacter.getCurrentMP() == playerCharacter
                        .getMaximumMP()) {
            CommonDialogs.showDialog("You don't need regeneration.");
            return false;
        }
        this.result = CommonDialogs.showInputDialog("Select",
                this.getShopNameFromType(), this.choices,
                this.choices[this.defaultChoice]);
        if (this.result == null) {
            return false;
        }
        if (this.index == -1) {
            return false;
        }
        this.index = 0;
        for (this.index = 0; this.index < this.choices.length; this.index++) {
            if (this.result.equals(this.choices[this.index])) {
                break;
            }
        }
        return true;
    }

    private boolean shopStage4() {
        // Stage 4
        PartyMember playerCharacter = PartyManager.getParty().getLeader();
        this.cost = 0;
        if ((this.type == ShopTypes.SHOP_TYPE_WEAPONS)
                || (this.type == ShopTypes.SHOP_TYPE_ARMOR)) {
            this.cost = Shop.getEquipmentCost(this.index);
            if (this.type == ShopTypes.SHOP_TYPE_WEAPONS) {
                if (this.typeResult != null) {
                    if (this.typeIndex == 1) {
                        // Two-Handed Weapon, price doubled
                        this.cost *= 2;
                    }
                }
            }
        } else if (this.type == ShopTypes.SHOP_TYPE_HEALER) {
            this.cost = Shop.getHealingCost(playerCharacter.getLevel(),
                    playerCharacter.getCurrentHP(),
                    playerCharacter.getMaximumHP());
        } else if (this.type == ShopTypes.SHOP_TYPE_REGENERATOR) {
            this.cost = Shop.getRegenerationCost(playerCharacter.getLevel(),
                    playerCharacter.getCurrentMP(),
                    playerCharacter.getMaximumMP());
        }
        // Handle inflation
        double actualInflation = this.inflationRate / 100.0;
        double inflatedCost = this.cost * actualInflation;
        this.cost = (int) inflatedCost;
        // Confirm
        final int stage4Confirm = CommonDialogs.showConfirmDialog(
                "This will cost " + this.cost + " Gold. Are you sure?",
                this.getShopNameFromType());
        if (stage4Confirm == JOptionPane.NO_OPTION
                || stage4Confirm == JOptionPane.CLOSED_OPTION) {
            return false;
        }
        if (Support.inDebugMode()) {
            CommonDialogs.showTitledDialog(
                    "Debug mode is enabled, so this purchase is free.",
                    this.getShopNameFromType());
            this.cost = 0;
        }
        return true;
    }

    private boolean shopStage5() {
        // Stage 5
        PartyMember playerCharacter = PartyManager.getParty().getLeader();
        if (playerCharacter.getGold() < this.cost) {
            CommonDialogs.showErrorDialog("Not Enough Gold!",
                    this.getShopNameFromType());
            return false;
        }
        return true;
    }

    private void shopStage6() {
        // Stage 6
        PartyMember playerCharacter = PartyManager.getParty().getLeader();
        // Play transact sound
        SoundManager.playSound(GameSoundConstants.TRANSACT);
        if (this.type == ShopTypes.SHOP_TYPE_WEAPONS) {
            playerCharacter.offsetGold(-this.cost);
            if (this.typeResult.equals(this.typeChoices[0])) {
                Equipment bought = EquipmentFactory
                        .createOneHandedWeapon(this.index, 0);
                playerCharacter.getItems().equipOneHandedWeapon(playerCharacter,
                        bought, this.handIndex, true);
            } else {
                Equipment bought = EquipmentFactory
                        .createTwoHandedWeapon(this.index, 0);
                playerCharacter.getItems().equipTwoHandedWeapon(playerCharacter,
                        bought, true);
            }
        } else if (this.type == ShopTypes.SHOP_TYPE_ARMOR) {
            playerCharacter.offsetGold(-this.cost);
            Equipment bought = EquipmentFactory.createArmor(this.index,
                    this.typeIndex, 0);
            playerCharacter.getItems().equipArmor(playerCharacter, bought,
                    true);
        } else if (this.type == ShopTypes.SHOP_TYPE_HEALER) {
            playerCharacter.offsetGold(-this.cost);
            playerCharacter.healPercentage((this.index + 1) * 10);
        } else if (this.type == ShopTypes.SHOP_TYPE_REGENERATOR) {
            playerCharacter.offsetGold(-this.cost);
            playerCharacter.regeneratePercentage((this.index + 1) * 10);
        }
    }
}
