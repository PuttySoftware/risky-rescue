/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell

 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue;

import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.io.File;

import javax.swing.JOptionPane;

import com.puttysoftware.commondialogs.CommonDialogs;
import com.puttysoftware.fileutils.DirectoryUtilities;
import com.puttysoftware.integration.NativeIntegration;
import com.puttysoftware.riskyrescue.assets.LogoManager;
import com.puttysoftware.riskyrescue.assets.SoundConstants;
import com.puttysoftware.riskyrescue.assets.SoundManager;
import com.puttysoftware.riskyrescue.creatures.party.PartyManager;
import com.puttysoftware.riskyrescue.prefs.PreferencesManager;
import com.puttysoftware.riskyrescue.scenario.ScenarioManager;

public class RiskyRescue {
    // Constants
    private static Application application;
    private static final String PROGRAM_NAME = "RiskyRescue";
    public static final int GENERATOR_RANDOMNESS_MAX = 6;

    // Methods
    public static Application getApplication() {
        return RiskyRescue.application;
    }

    public static void logError(final Throwable t) {
        SoundManager.playSound(SoundConstants.FATAL_ERROR);
        Support.getErrorLogger().logError(t);
    }

    public static void logNonFatalError(final RuntimeException re) {
        Support.getNonFatalLogger().logNonFatalError(re);
    }

    public static String getProgramName() {
        return RiskyRescue.PROGRAM_NAME;
    }

    public static void newScenario() {
        if (Support.getScenario() != null) {
            Support.deleteScenario();
            RiskyRescue.application.getScenarioManager().setMap(null);
        }
        // Create scenario
        Support.createScenario();
        // Heal party
        PartyManager.revivePartyFully();
    }

    public static void main(final String[] args) {
        try {
            String suffix;
            if (Support.inDebugMode()) {
                suffix = " (DEBUG)";
            } else {
                suffix = "";
            }
            NativeIntegration ni = new NativeIntegration();
            // Integrate with host platform
            ni.configureLookAndFeel();
            // Set defaults
            CommonDialogs.setDefaultTitle(RiskyRescue.PROGRAM_NAME + suffix);
            CommonDialogs.setIcon(LogoManager.getMicroLogo());
            // Initialization
            Support.preInit();
            RiskyRescue.application = new Application();
            RiskyRescue.application.postConstruct(ni);
            Application.playLogoSound();
            RiskyRescue.application.getGUIManager().showGUI();
            // Register platform hooks
            ni.setAboutHandler(RiskyRescue.application.getAboutDialog());
            ni.setPreferencesHandler(new PrefsBoxer());
            ni.setQuitHandler(new Quitter());
        } catch (Throwable t) {
            RiskyRescue.logError(t);
        }
    }

    static class PrefsBoxer implements PreferencesHandler {
        public PrefsBoxer() {
            super();
        }

        @Override
        public void handlePreferences(PreferencesEvent inE) {
            PreferencesManager.showPrefs();
        }
    }

    static class Quitter implements QuitHandler {
        public Quitter() {
            super();
        }

        @Override
        public void handleQuitRequestWith(QuitEvent event,
                QuitResponse response) {
            ScenarioManager mm = RiskyRescue.getApplication()
                    .getScenarioManager();
            boolean saved = true;
            int status;
            if (mm.getDirty()) {
                status = ScenarioManager.showSaveDialog();
                if (status == JOptionPane.YES_OPTION) {
                    saved = mm.saveGame();
                } else if (status == JOptionPane.CANCEL_OPTION) {
                    saved = false;
                } else {
                    mm.setDirty(false);
                }
            }
            if (saved) {
                PreferencesManager.writePrefs();
                // Run cleanup task
                try {
                    File dirToDelete = new File(
                            System.getProperty("java.io.tmpdir")
                                    + File.pathSeparator + "RiskyRescue");
                    DirectoryUtilities.removeDirectory(dirToDelete);
                } catch (Throwable t) {
                    // Ignore
                }
                response.performQuit();
            } else {
                response.cancelQuit();
            }
        }
    }
}
