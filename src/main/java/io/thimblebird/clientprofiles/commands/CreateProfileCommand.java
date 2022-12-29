package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import io.thimblebird.clientprofiles.util.ProfileUtils;
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
        if (!ProfileUtils.exists(profileName)) {
            ProfileConfig newProfileConfig = new ProfileConfig(profileName);

            newProfileConfig.credits = source.getTextName();
            newProfileConfig.createProfile("You may include a profile description here.");

            source.sendSuccess(Component.translatable("§6⭐§r Successfully created profile: %s", profileName), true);

            return 1;
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't create profile '%s' for some reason!",
                "Profile exists: [%s]"
        ), profileName, ProfileUtils.exists(profileName)));

        return -1;
    }
}
