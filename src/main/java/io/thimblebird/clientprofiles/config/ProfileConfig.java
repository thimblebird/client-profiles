package io.thimblebird.clientprofiles.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.thimblebird.clientprofiles.ClientProfiles;
import io.thimblebird.clientprofiles.util.ClassUtils;
import io.thimblebird.clientprofiles.util.ConfigUtils;
import io.thimblebird.clientprofiles.util.ProfileUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

import static io.thimblebird.clientprofiles.ClientProfiles.*;

public class ProfileConfig {
    private final Path profilesPath;

    @SuppressWarnings("unused")
    public String id;
    @SuppressWarnings("unused")
    public String displayName;
    @SuppressWarnings("unused")
    public String credits;
    public boolean readOnly = false;

    public ProfileConfig(String profileId) {
        this.profilesPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
        this.id = profileId;

        // default options
        HashMap<String, Object> options = new HashMap<>();
        options.put("id", profileId);
        options.put("displayName", profileId);
        options.put("credits", "You ♥");
        options.put("readOnly", false);

        setFields(options);
    }

    public ProfileConfig(String profileId, HashMap<String, ?> options) {
        this.profilesPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
        this.id = profileId;

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

    public void createProfile(String profileComment) {
        Path profileDir = this.profilesPath.resolve(this.id);
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
            CommentedFileConfig config = CommentedFileConfig.of(profileDir.resolve(ConfigUtils.getFileName()));
            config.load();
            config.clear(); // config builder? parse mode "add"?

            if (profileComment.length() > 0) {
                config.setComment(ConfigUtils.getRootPath(), profileComment);
            }

            Arrays.stream(this.getClass().getFields()).toList().forEach(field -> {
                try {
                    config.set(ConfigUtils.getPath(field.getName()), field.get(this));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            config.save();
            config.close();
        }
    }

    public static ProfileConfig loadProfile(String profileId) throws IOException {
        if (!ProfileUtils.exists(profileId)) {
            throw new IOException("Couldn't load non-existing profile: " + profileId);
        }

        return new ProfileConfig(profileId, ProfileUtils.getConfig(ProfileUtils.getPath(profileId)));
    }

    // current profile (reads `_clientprofile.toml` in the root directory, if present)
    public static ProfileConfig loadProfile() throws IOException {
        Path rootDir = FMLPaths.CONFIGDIR.get();
        Path rootProfile = rootDir.resolve(ConfigUtils.getFileName());

        if (!FileUtils.directoryContains(rootDir.toFile(), rootProfile.toFile())) {
            throw new IOException("No active profile; `" + ConfigUtils.getFileName() + "` not found");
        }

        HashMap<String, Object> options = ProfileUtils.getConfig(rootDir);
        String profileId = options.get("id").toString();

        if (!ProfileUtils.exists(profileId)) {
            throw new IOException("Couldn't load non-existing profile: " + profileId);
        }

        return new ProfileConfig(profileId, options);
    }

    public static boolean deleteProfile(String profileId) {
        if (ProfileUtils.exists(profileId)) {
            try {
                if (!loadProfile(profileId).readOnly) {
                    FileUtils.deleteDirectory(ProfileUtils.getPath(profileId).toFile());

                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public static void switchProfile(String profileId) {
        if (ProfileUtils.exists(profileId)) {
            Path newProfileRootDir = ProfileUtils.getPath(profileId);
            Path configRootDir = FMLPaths.CONFIGDIR.get();
            String currentProfileName = ProfileUtils.getId();

            try {
                // if the current profile is read-only, delete everything, discarding changes
                if (ProfileUtils.exists(currentProfileName)) {
                    Path currentProfileRootDir = ProfileUtils.getPath(currentProfileName);
                    ProfileConfig currentProfile = loadProfile(currentProfileName);

                    for (File file : ConfigUtils.getRootFiles()) {
                        if (!currentProfile.readOnly) {
                            // otherwise, cut all files into the current profile's directory, effectively "saving" changes
                            FileUtils.copyToDirectory(file, currentProfileRootDir.toFile());
                        }
                        FileUtils.forceDelete(file);
                    }
                }

                // copy profile files into root
                FileUtils.copyDirectory(newProfileRootDir.toFile(), configRootDir.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean saveProfile(String profileId) {
        if (ProfileUtils.exists(profileId)) {
            // copy files from root into profile dir
            try {
                Path profileRootDir = ProfileUtils.getPath(profileId);
                ProfileConfig profile = loadProfile(profileId);

                if (!profile.readOnly) {
                    // create a backup profile directory in case something goes wrong
                    Path backupProfileDir = profileRootDir.getParent().resolve("___tmp_backup-" + profileId);

                    try {
                        FileUtils.copyDirectory(profileRootDir.toFile(), backupProfileDir.toFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileUtils.deleteDirectory(profileRootDir.toFile());

                    if (new File(profileRootDir.toString()).mkdir()) {
                        // copy root files into the profile directory
                        for (File file : ConfigUtils.getRootFiles()) {
                            FileUtils.copyToDirectory(file, profileRootDir.toFile());
                        }

                        FileUtils.deleteDirectory(backupProfileDir.toFile());

                        return true;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }
}
