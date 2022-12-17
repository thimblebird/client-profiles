package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.thimblebird.clientprofiles.config.ProfileConfig;
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
                Commands.literal(MOD_COMMANDS_NAMESPACE).then(
                Commands.literal("list").executes((command) -> execute(command.getSource())))
        );
    }

    private static int execute(CommandSourceStack source) {
        Collection<File> profiles = ProfileConfig.getProfiles();

        if (profiles.size() > 0) {
            source.sendSuccess(Component.translatable("Available profiles: §7(§d*§7 = read-only)"), true);

            String profileName;
            for (File profile : profiles) {
                boolean hasDisplayName;
                profileName = profile.getName();

                String prefix = " §8⏵§r ";
                // highlight current profile
                if (profileName.equals(ProfileConfig.getCurrentProfileName())) {
                    prefix = " §6⏵§r ";
                }

                ProfileConfig profileConfig;
                String displayName;
                boolean readOnly;
                try {
                    profileConfig = ProfileConfig.loadProfile(profileName);
                    displayName = profileConfig.displayName;
                    hasDisplayName = !displayName.equals(profileName);
                    readOnly = profileConfig.readOnly;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (readOnly) {
                    profileName += "§d*§r";
                }

                if (!hasDisplayName) {
                    source.sendSuccess(Component.literal(prefix + profileName), true);
                } else {
                    profileName = "§7" + profileName;
                    source.sendSuccess(Component.literal(prefix + displayName + " §8[" + profileName + "§8]"), true);
                }
            }

            return 1;
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't list profiles for some reason!",
                "Profiles exists: [%s]"
        ), ProfileConfig.profilesExists()));

        return -1;
    }
}
