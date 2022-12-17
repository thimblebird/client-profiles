package io.thimblebird.clientprofiles.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.thimblebird.clientprofiles.ClientProfiles;
import io.thimblebird.clientprofiles.util.ClassUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;
import static io.thimblebird.clientprofiles.ClientProfiles.MOD_ID;

public class ProfileConfig {
    private final String name;
    private final Path profilesPath;

    @SuppressWarnings("unused")
    public String displayName;
    @SuppressWarnings("unused")
    public String credits;
    public boolean readOnly = false;
    @SuppressWarnings("unused")
    public ArrayList<String> lockedFiles = new ArrayList<>();

    public ProfileConfig(String profileName) {
        this.name = profileName;
        this.profilesPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);

        // default options
        HashMap<String, Object> options = new HashMap<>();
        options.put("displayName", profileName);
        options.put("credits", "You ♥");
        options.put("lockedFiles", new ArrayList<>());
        options.put("readOnly", false);

        setFields(options);
    }

    public ProfileConfig(String profileName, HashMap<String, ?> options) {
        this.name = profileName;
        this.profilesPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);

        setFields(options);
    }

    // mass-set (default) values
    private void setFields(HashMap<String, ?> options) {
        ArrayList<String> fieldNames = ClassUtils.getFieldNames(this);
        options.forEach((key, value) -> {
            try {
                if (fieldNames.contains(key)) {
                    setField(this.getClass().getField(key), value);
                } else {
                    ClientProfiles.LOGGER.warn("Couldn't find a profile config option named '{}'! (and couldn't set it to '{}', either)", key, value);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings("unused")
    private <T> void setField(Field field, T value) throws IllegalAccessException {
        field.set(this, value);
    }

    @SuppressWarnings("unused")
    private Object getField(Field field) throws IllegalAccessException {
        return field.get(this);
    }

    @SuppressWarnings("unused")
    private static String getConfigName() {
        //return String.join("--", "_clientprofile", this.name);
        return "_clientprofile";
    }

    @SuppressWarnings("unused")
    private static String getConfigFileName() {
        return getConfigName() + ".toml";
    }

    @SuppressWarnings("unused")
    private static String getConfigRootPath() {
        //return String.join(".", MOD_ID, this.name);
        return MOD_COMMANDS_NAMESPACE;
    }

    @SuppressWarnings("unused")
    private static String getConfigPath(String part) {
        return String.join(".", getConfigRootPath(), part);
    }

    @SuppressWarnings("unused")
    public static Path getProfilesPath() {
        return FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
    }

    @SuppressWarnings("unused")
    public static Path getProfilePath(String profileName) {
        return getProfilesPath().resolve(profileName);
    }

    public void createProfile(String profileComment) {
        Path profileDir = this.profilesPath.resolve(this.name);
        File profile = profileDir.toFile();
        boolean profileDirCreated;

        // create directory structure
        try {
            FileUtils.createParentDirectories(profile);
            profileDirCreated = new File(profileDir.toString()).mkdir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (profileDirCreated) {
            CommentedFileConfig config = CommentedFileConfig.of(profileDir.resolve(getConfigFileName()));
            config.load();
            config.clear(); // config builder? parse mode "add"?

            if (profileComment.length() > 0) {
                config.setComment(getConfigRootPath(), profileComment);
            }

            Arrays.stream(this.getClass().getFields()).toList().forEach(field -> {
                try {
                    config.set(getConfigPath(field.getName()), field.get(this));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            config.save();
            config.close();
        }
    }

    public static ProfileConfig loadProfile(String profileName) throws IOException {
        if (!profileExists(profileName)) {
            throw new IOException("Couldn't load non-existing profile: " + profileName);
        }

        CommentedFileConfig config = CommentedFileConfig.of(getProfilePath(profileName).resolve(getConfigFileName()));
        HashMap<String, Object> options = new HashMap<>();

        config.load();

        for (Field field : Arrays.stream(ProfileConfig.class.getFields()).toList()) {
            String fieldName = field.getName();
            String fieldPath = getConfigPath(fieldName);

            if (config.contains(fieldPath)) {
                options.put(fieldName, config.get(fieldPath));
            }
        }

        config.close();

        return new ProfileConfig(profileName, options);
    }

    public static boolean deleteProfile(String profileName) {
        if (profileExists(profileName)) {
            try {
                if (!loadProfile(profileName).readOnly) {
                    FileUtils.deleteDirectory(getProfilePath(profileName).toFile());

                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public static void switchProfile(String profileName) {
        if (profileExists(profileName)) {
            Path rootDir = getProfilePath(profileName);
            Path destDir = FMLPaths.CONFIGDIR.get();

            Collection<File> existingFilesAndDirs = FileUtils.listFilesAndDirs(destDir.toFile(), TrueFileFilter.INSTANCE, null);
            existingFilesAndDirs.remove(destDir.toFile());
            existingFilesAndDirs.remove(destDir.resolve(MOD_ID).toFile());
            existingFilesAndDirs.remove(destDir.resolve(MOD_ID + "-client.toml").toFile());

            try {
                for (File file : existingFilesAndDirs) {
                    FileUtils.forceDelete(file);
                }

                FileUtils.copyDirectory(rootDir.toFile(), destDir.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ClientProfilesConfig.CURRENT_PROFILE.set(profileName);
        }
    }

    public static boolean profileExists(String profileName) {
        try {
            return FileUtils.directoryContains(getProfilesPath().toFile(), getProfilePath(profileName).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean profilesExists() {
        try {
            Path profilesPath = getProfilesPath();
            return !FileUtils.isEmptyDirectory(profilesPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentProfileName() {
        return ClientProfilesConfig.CURRENT_PROFILE.get();
    }

    public static Collection<File> getProfiles() {
        File rootDir = getProfilesPath().toFile();
        Collection<File> filesAndDirs = FileUtils.listFilesAndDirs(rootDir, TrueFileFilter.INSTANCE, null);
        filesAndDirs.removeIf((thing) -> !thing.isDirectory());
        filesAndDirs.remove(rootDir);
        return filesAndDirs;
    }
}
