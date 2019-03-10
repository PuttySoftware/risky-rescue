/*  Risky Rescue: A Roguelike RPG
 Copyright (C) 2013-2014 Eric Ahnell


 Any questions should be directed to the author via email at: support@puttysoftware.com
 */
package com.puttysoftware.riskyrescue.assets.data;

import java.util.ArrayList;

import com.puttysoftware.fileutils.ResourceStreamReader;

public class ImageDataManager {
    public static String[] getMonsterImageData(int index) {
        try (final ResourceStreamReader rsr = new ResourceStreamReader(
                ImageDataManager.class.getResourceAsStream(
                        "/assets/data/image/monster" + index + ".txt"))) {
            // Fetch data
            final ArrayList<String> data = new ArrayList<>();
            String raw = "0";
            while (raw != null) {
                raw = rsr.readString();
                data.add(raw);
            }
            Object[] arr = data.toArray();
            String[] tempres = new String[arr.length];
            int count = 0;
            for (int x = 0; x < arr.length; x++) {
                if (arr[x] != null) {
                    tempres[x] = arr[x].toString();
                    count++;
                }
            }
            String[] res = new String[count];
            count = 0;
            for (int x = 0; x < tempres.length; x++) {
                if (tempres[x] != null) {
                    res[count] = tempres[x];
                    count++;
                }
            }
            return res;
        } catch (final Exception e) {
            return null;
        }
    }

    public static String[] getObjectImageData() {
        try (final ResourceStreamReader rsr = new ResourceStreamReader(
                ImageDataManager.class.getResourceAsStream(
                        "/assets/data/image/object.txt"))) {
            // Fetch data
            final ArrayList<String> data = new ArrayList<>();
            String raw = "0";
            while (raw != null) {
                raw = rsr.readString();
                data.add(raw);
            }
            Object[] arr = data.toArray();
            String[] tempres = new String[arr.length];
            int count = 0;
            for (int x = 0; x < arr.length; x++) {
                if (arr[x] != null) {
                    tempres[x] = arr[x].toString();
                    count++;
                }
            }
            String[] res = new String[count];
            count = 0;
            for (int x = 0; x < tempres.length; x++) {
                if (tempres[x] != null) {
                    res[count] = tempres[x];
                    count++;
                }
            }
            return res;
        } catch (final Exception e) {
            return null;
        }
    }

    public static String[] getStatImageData() {
        try (final ResourceStreamReader rsr = new ResourceStreamReader(
                ImageDataManager.class
                        .getResourceAsStream("/assets/data/image/stats.txt"))) {
            // Fetch data
            final ArrayList<String> data = new ArrayList<>();
            String raw = "0";
            while (raw != null) {
                raw = rsr.readString();
                data.add(raw);
            }
            Object[] arr = data.toArray();
            String[] tempres = new String[arr.length];
            int count = 0;
            for (int x = 0; x < arr.length; x++) {
                if (arr[x] != null) {
                    tempres[x] = arr[x].toString();
                    count++;
                }
            }
            String[] res = new String[count];
            count = 0;
            for (int x = 0; x < tempres.length; x++) {
                if (tempres[x] != null) {
                    res[count] = tempres[x];
                    count++;
                }
            }
            return res;
        } catch (final Exception e) {
            return null;
        }
    }
}
