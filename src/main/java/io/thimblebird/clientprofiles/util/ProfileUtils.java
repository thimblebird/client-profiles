package io.thimblebird.clientprofiles.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_ID;

public class ProfileUtils {
    @SuppressWarnings("unused")
    public static Path getProfilesPath() {
        return FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
    }

    @SuppressWarnings("unused")
    public static Path getPath(String profileId) {
        return getProfilesPath().resolve(profileId);
    }

    public static boolean exists(String profileId) {
        try {
            return FileUtils.directoryContains(getProfilesPath().toFile(), getPath(profileId).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean profilesExist() {
        try {
            Path profilesPath = getProfilesPath();
            return !FileUtils.isEmptyDirectory(profilesPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getId() {
        try {
            return ProfileConfig.loadProfile().id;
        } catch (Exception ignored) {}

        return "";
    }

    public static String getId(Path profileRootDir) {
        HashMap<String, Object> options = getConfig(profileRootDir);

        if (!options.containsKey("id")) return "";

        return options.get("id").toString();
    }

    public static HashMap<String, Object> getConfig(Path profileRootDir) {
        CommentedFileConfig config = CommentedFileConfig.of(profileRootDir.resolve(ConfigUtils.getFileName()));
        HashMap<String, Object> options = new HashMap<>();

        config.load();

        for (Field field : Arrays.stream(ProfileConfig.class.getFields()).toList()) {
            String fieldName = field.getName();
            String fieldPath = ConfigUtils.getPath(fieldName);

            if (config.contains(fieldPath)) {
                options.put(fieldName, config.get(fieldPath));
            }
        }

        config.close();

        return options;
    }

    public static Collection<File> getAll() {
        File rootDir = getProfilesPath().toFile();
        Collection<File> filesAndDirs = FileUtils.listFilesAndDirs(rootDir, TrueFileFilter.INSTANCE, null);

        filesAndDirs.removeIf((thing) -> !thing.isDirectory());
        filesAndDirs.remove(rootDir);

        return filesAndDirs;
    }
}
