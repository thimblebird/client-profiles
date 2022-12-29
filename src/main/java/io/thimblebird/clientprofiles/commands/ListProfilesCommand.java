package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import io.thimblebird.clientprofiles.util.ProfileUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

public class ListProfilesCommand {
    public ListProfilesCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE)
                        .then(
                            Commands.literal("list").executes((command) -> execute(command.getSource()))
                        )
        );
    }

    private static int execute(CommandSourceStack source) {
        Collection<File> profiles = ProfileUtils.getAll();

        if (profiles.size() > 0) {
            source.sendSuccess(Component.translatable("Available profiles: §7(§d*§7 = read-only)"), true);

            String profileId;
            for (File profile : profiles) {
                boolean hasDisplayName;
                profileId = ProfileUtils.getId(profile.toPath());

                String prefix = " §8⏵§r ";
                // highlight current profile
                if (!ProfileUtils.getId().isEmpty() && profileId.equals(ProfileUtils.getId())) {
                    prefix = " §6⏵§r ";
                }

                ProfileConfig profileConfig;
                String displayName;
                boolean readOnly;
                try {
                    profileConfig = ProfileConfig.loadProfile(profileId);
                    displayName = profileConfig.displayName;
                    if (displayName == null) {
                        hasDisplayName = false;
                    } else {
                        hasDisplayName = !displayName.equals(profileId);
                    }
                    readOnly = profileConfig.readOnly;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (readOnly) {
                    profileId += "§d*§r";
                }

                if (!hasDisplayName) {
                    source.sendSuccess(Component.literal(prefix + profileId), true);
                } else {
                    profileId = "§7" + profileId;
                    source.sendSuccess(Component.literal(prefix + displayName + " §8[" + profileId + "§8]"), true);
                }
            }

            return 1;
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't list profiles for some reason!",
                "Profiles exists: [%s]"
        ), ProfileUtils.profilesExist()));

        return -1;
    }
}
