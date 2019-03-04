/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.creatures.monsters;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.riskyrescue.RiskyRescue;
import com.puttysoftware.riskyrescue.assets.ImageManager;
import com.puttysoftware.riskyrescue.assets.MonsterNames;

abstract class BothRandomBaseMonster extends BaseMonster {
    // Constructors
    BothRandomBaseMonster() {
        super();
        this.image = this.getInitialImage();
    }

    @Override
    public boolean randomAppearance() {
        return true;
    }

    @Override
    public boolean randomFaith() {
        return true;
    }

    @Override
    protected BufferedImageIcon getInitialImage() {
        if (this.getLevel() == 0) {
            return null;
        } else {
            int dungeonIndex = RiskyRescue.getApplication().getScenarioManager()
                    .getMap().getActiveLevelNumber();
            final String[] types = MonsterNames.getAllNames(dungeonIndex);
            final RandomRange r = new RandomRange(0, types.length - 1);
            int nameIndex = r.generate();
            this.setType(types[nameIndex]);
            return ImageManager.getMonsterImage(dungeonIndex, nameIndex);
        }
    }

    @Override
    public void loadMonster() {
        this.image = this.getInitialImage();
    }
}
