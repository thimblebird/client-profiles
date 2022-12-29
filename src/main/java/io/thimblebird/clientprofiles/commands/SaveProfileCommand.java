package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import io.thimblebird.clientprofiles.util.ProfileUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.IOException;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

@SuppressWarnings("unused")
public class SaveProfileCommand {
    public SaveProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE)
                        .then(
                            Commands.literal("save").executes((command) -> execute(command.getSource()))
                        )
        );
    }

    private static int execute(CommandSourceStack source) {
        String currentProfile = ProfileUtils.getId();

        if (currentProfile.isEmpty()) {
            source.sendFailure(Component.translatable(String.join(
                    " ",
                    "Couldn't save profile! No (existing) profile selected.\n",
                    "Run '/clientprofile list' to see available profiles or '/clientprofile switch <profileName>'",
                    "if you already know a profile you would like to switch to."
            )));

            return -1;
        }

        boolean profileExists = ProfileUtils.exists(currentProfile);

        if (profileExists && ProfileConfig.saveProfile(currentProfile)) {
            source.sendSuccess(Component.translatable("§6⚡ §rSuccessfully saved profile: %s", currentProfile), true);

            return 1;
        }

        boolean isReadOnly = false;

        if (ProfileUtils.exists(currentProfile)) {
            try {
                isReadOnly = ProfileConfig.loadProfile(currentProfile).readOnly;
            } catch (IOException ignored) {}
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't save profile '%s' for some reason!",
                "Profile exists: [%s]",
                "Profile is read-only: [%s]"
        ), currentProfile, profileExists, String.valueOf(isReadOnly)));

        return -1;
    }
}
