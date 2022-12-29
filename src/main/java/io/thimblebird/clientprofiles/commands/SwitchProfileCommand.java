package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import io.thimblebird.clientprofiles.util.ProfileUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.io.File;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

public class SwitchProfileCommand {
    private static final SuggestionProvider<CommandSourceStack> AVAILABLE_PROFILES = (command, input) -> SharedSuggestionProvider.suggest(
            ProfileUtils.getAll().stream().map(File::getName).map(StringArgumentType::escapeIfRequired), input
    );

    public SwitchProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE)
                        .then(
                            Commands.literal("switch")
                                    .then(
                                            Commands.argument("profileName", StringArgumentType.string())
                                                    .suggests(AVAILABLE_PROFILES)
                                                    .executes((command) -> execute(
                                                            command.getSource(),
                                                            StringArgumentType.getString(command, "profileName")
                                                    ))
                                    )
                        )
        );
    }

    private static int execute(CommandSourceStack source, String profileName) {
        // switch to the selected profile
        if (ProfileUtils.exists(profileName)) {
            String currentProfileId = ProfileUtils.getId();

            if (currentProfileId.isEmpty() || !currentProfileId.equals(profileName)) {
                ProfileConfig.switchProfile(profileName);

                source.sendSuccess(Component.translatable("§6⚡ §rSuccessfully switched profile to: %s", profileName), true);
                source.sendSuccess(Component.translatable("§9♦ §rPlease §crestart your game§r for changes to take effect."), true);

                return 1;
            } else {
                source.sendFailure(Component.translatable("§9� §rNo reason to switch; already using profile: %s", profileName));

                return 0;
            }
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't switch to profile '%s' for some reason!",
                "Profile exists: [%s]"
        ), profileName, ProfileUtils.exists(profileName)));

        return -1;
    }
}
