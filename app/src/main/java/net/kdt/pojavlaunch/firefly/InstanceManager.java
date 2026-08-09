package net.kdt.pojavlaunch.firefly;

import com.movtery.ui.subassembly.customprofilepath.ProfilePathManager;

import net.kdt.pojavlaunch.firefly.value.launcherprofiles.MinecraftProfile;

import java.io.File;
import java.util.Locale;

/**
 * Prism-Launcher-like per-instance folders.
 *
 * Every created profile gets its own game directory under
 * <pre>{launcher}/instances/&lt;profile name&gt;</pre> while the legacy default
 * profile keeps using the shared {@code .minecraft} folder.
 */
public class InstanceManager {
    public static final String INSTANCES_DIR = "instances";

    private InstanceManager() {
    }

    /**
     * Resolve the absolute game directory of a profile.
     */
    public static File getInstanceDir(MinecraftProfile profile) {
        return Tools.getGameDirPath(profile);
    }

    /**
     * Resolve the mods folder of a profile, creating it if necessary.
     */
    public static File getModsDir(MinecraftProfile profile) {
        File modsDir = new File(getInstanceDir(profile), "mods");
        modsDir.mkdirs();
        return modsDir;
    }

    /**
     * Give a profile its own instance folder, based on its name and version.
     * No-op if the profile already has a custom game directory.
     */
    public static void assignInstanceFolder(MinecraftProfile profile) {
        if (profile.gameDir != null && !profile.gameDir.isEmpty()) return;

        String base = profile.name != null && !profile.name.isEmpty() ? profile.name : "instance";
        if (profile.lastVersionId != null && !profile.lastVersionId.isEmpty()) {
            base = base + " " + profile.lastVersionId;
        }
        profile.gameDir = INSTANCES_DIR + "/" + sanitize(base);

        File dir = new File(ProfilePathManager.getCurrentPath(), profile.gameDir);
        dir.mkdirs();
        new File(dir, "mods").mkdirs();
    }

    /**
     * Make a string safe to use as a single folder name.
     */
    public static String sanitize(String name) {
        if (name == null || name.isEmpty()) return "instance";
        String cleaned = name.trim().replaceAll("[\\\\/:*?\"<>| \\t\\n]", "_");
        if (cleaned.length() > 80) cleaned = cleaned.substring(0, 80);
        return cleaned.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
    }
}
