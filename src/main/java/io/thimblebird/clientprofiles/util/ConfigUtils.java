package io.thimblebird.clientprofiles.util;

import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;
import static io.thimblebird.clientprofiles.ClientProfiles.MOD_ID;

public class ConfigUtils {
    @SuppressWarnings("unused")
    private static String getName() {
        return "_clientprofile";
    }

    @SuppressWarnings("unused")
    public static String getFileName() {
        return getName() + ".toml";
    }

    @SuppressWarnings("unused")
    public static String getRootPath() {
        return MOD_COMMANDS_NAMESPACE;
    }

    @SuppressWarnings("unused")
    public static String getPath(String part) {
        return String.join(".", getRootPath(), part);
    }

    public static Collection<File> getRootFiles() {
        Path configRootDir = FMLPaths.CONFIGDIR.get();
        Collection<File> rootFiles = FileUtils.listFilesAndDirs(
                configRootDir.toFile(),
                TrueFileFilter.INSTANCE,
                null
        );

        // remove the mod's files from this collection
        rootFiles.remove(configRootDir.toFile()); // root dir
        rootFiles.remove(configRootDir.resolve(MOD_ID).toFile()); // "clientprofiles" directory
        rootFiles.remove(configRootDir.resolve(MOD_ID + "-client.toml").toFile()); // mod config

        return rootFiles;
    }

    public static void clearConfigDir() {
        try {
            for (File file : getRootFiles()) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
