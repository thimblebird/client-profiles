package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.thimblebird.clientprofiles.config.ClientProfilesConfig;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.IOException;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

public class ClientProfileCommand {
    public ClientProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE).executes((command) -> execute(command.getSource()))
        );
    }

    private static int execute(CommandSourceStack source) {
        // output current profile
        String currentProfile = ClientProfilesConfig.CURRENT_PROFILE.get();

        if (currentProfile.length() > 0 && ProfileConfig.profileExists(currentProfile)) {
            ProfileConfig profileConfig;
            boolean hasDisplayName;
            String displayName;
            boolean readOnly;
            try {
                profileConfig = ProfileConfig.loadProfile(currentProfile);
                displayName = profileConfig.displayName;
                hasDisplayName = !displayName.equals(currentProfile);
                readOnly = profileConfig.readOnly;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (readOnly) {
                currentProfile += "§d*§r";
            }

            if (!hasDisplayName) {
                source.sendSuccess(Component.translatable("§6Current profile§r: %s", currentProfile), true);
            } else {
                currentProfile = "§7" + currentProfile;
                source.sendSuccess(Component.translatable("§6Current profile§r: %s §8[%s§8]", displayName, currentProfile), true);
            }

            return 1;
        }

        source.sendFailure(Component.translatable(String.join(
                " ",
                "No profile selected at the moment.",
                "Run '/clientprofile list' to see available profiles or '/clientprofile switch <profileName>'",
                "if you already know a profile you would like to switch to."
        )));

        return -1;
    }
}
