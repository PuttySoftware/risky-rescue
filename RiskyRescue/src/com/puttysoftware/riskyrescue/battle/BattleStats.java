/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.battle;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.riskyrescue.assets.ImageManager;
import com.puttysoftware.riskyrescue.assets.StatImage;
import com.puttysoftware.riskyrescue.map.objects.BattleCharacter;

class BattleStats {
    // Fields
    private Container statsPane;
    private JLabel nameLabel;
    private JLabel teamLabel;
    private JLabel hpLabel;
    private JLabel mpLabel;
    private JLabel attLabel;
    private JLabel defLabel;
    private JLabel apLabel;
    private JLabel attLeftLabel;
    private JLabel splLabel;
    private JLabel stlLabel;

    // Constructors
    public BattleStats() {
        this.setUpGUI();
        this.updateIcons();
    }

    // Methods
    public Container getStatsPane() {
        return this.statsPane;
    }

    public void updateStats(BattleCharacter bc) {
        if (bc != null) {
            this.nameLabel.setText(bc.getName());
            this.teamLabel.setText(bc.getTeamString());
            this.hpLabel.setText(bc.getTemplate().getHPString());
            this.mpLabel.setText(bc.getTemplate().getMPString());
            this.attLabel.setText(bc.getTemplate().getAttackString());
            this.defLabel.setText(bc.getTemplate().getDefenseString());
            this.apLabel.setText(bc.getAPString());
            this.attLeftLabel.setText(bc.getAttackString());
            this.splLabel.setText(bc.getSpellString());
            this.stlLabel.setText(bc.getStealString());
        }
    }

    private void setUpGUI() {
        this.statsPane = new Container();
        this.statsPane.setLayout(new GridLayout(13, 1));
        this.nameLabel = new JLabel("", null, SwingConstants.LEFT);
        this.teamLabel = new JLabel("", null, SwingConstants.LEFT);
        this.hpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.mpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.attLabel = new JLabel("", null, SwingConstants.LEFT);
        this.defLabel = new JLabel("", null, SwingConstants.LEFT);
        this.apLabel = new JLabel("", null, SwingConstants.LEFT);
        this.attLeftLabel = new JLabel("", null, SwingConstants.LEFT);
        this.splLabel = new JLabel("", null, SwingConstants.LEFT);
        this.stlLabel = new JLabel("", null, SwingConstants.LEFT);
        this.statsPane.add(this.nameLabel);
        this.statsPane.add(this.teamLabel);
        this.statsPane.add(this.hpLabel);
        this.statsPane.add(this.mpLabel);
        this.statsPane.add(this.attLabel);
        this.statsPane.add(this.defLabel);
        this.statsPane.add(this.apLabel);
        this.statsPane.add(this.attLeftLabel);
        this.statsPane.add(this.splLabel);
        this.statsPane.add(this.stlLabel);
    }

    private void updateIcons() {
        BufferedImageIcon nameImage = ImageManager.getStatImage(StatImage.NAME);
        this.nameLabel.setIcon(nameImage);
        BufferedImageIcon teamImage = ImageManager.getStatImage(StatImage.TEAM);
        this.teamLabel.setIcon(teamImage);
        BufferedImageIcon hpImage = ImageManager.getStatImage(StatImage.HEALTH);
        this.hpLabel.setIcon(hpImage);
        BufferedImageIcon mpImage = ImageManager.getStatImage(StatImage.MAGIC);
        this.mpLabel.setIcon(mpImage);
        BufferedImageIcon attImage = ImageManager
                .getStatImage(StatImage.MELEE_ATTACK);
        this.attLabel.setIcon(attImage);
        BufferedImageIcon defImage = ImageManager
                .getStatImage(StatImage.DEFENSE);
        this.defLabel.setIcon(defImage);
        BufferedImageIcon apImage = ImageManager
                .getStatImage(StatImage.ACTIONS);
        this.apLabel.setIcon(apImage);
        BufferedImageIcon attLeftImage = ImageManager
                .getStatImage(StatImage.ATTACKS);
        this.attLeftLabel.setIcon(attLeftImage);
        BufferedImageIcon spImage = ImageManager.getStatImage(StatImage.SPELLS);
        this.splLabel.setIcon(spImage);
        BufferedImageIcon stImage = ImageManager.getStatImage(StatImage.STEALS);
        this.stlLabel.setIcon(stImage);
    }
}
