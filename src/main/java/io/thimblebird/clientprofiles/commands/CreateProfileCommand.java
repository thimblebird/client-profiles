package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

public class CreateProfileCommand {
    public CreateProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE).then(
                Commands.literal("create").then(
                Commands.argument("profileName", StringArgumentType.word())
                        .executes((command) -> {
                                String profileName = command.getArgument("profileName", String.class);

                                return execute(command.getSource(), profileName);
                        })
                ))
        );
    }

    private static int execute(CommandSourceStack source, String profileName) {
        // create a new profile
        if (!ProfileConfig.profileExists(profileName)) {
            ProfileConfig newProfileConfig = new ProfileConfig(profileName);

            newProfileConfig.credits = source.getTextName();
            newProfileConfig.createProfile("You may include a comment about your profile here.");

            source.sendSuccess(Component.translatable("§6⭐ §rSuccessfully created profile: %s", profileName), true);

            return 1;
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't create profile '%s' for some reason!",
                "Profile exists: [%s]"
        ), profileName, ProfileConfig.profileExists(profileName)));

        return -1;
    }
}
