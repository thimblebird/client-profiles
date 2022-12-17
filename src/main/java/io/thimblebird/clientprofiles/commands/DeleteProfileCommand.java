package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.thimblebird.clientprofiles.config.ClientProfilesConfig;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

public class DeleteProfileCommand {
    private static final SuggestionProvider<CommandSourceStack> AVAILABLE_PROFILES = (command, input) -> SharedSuggestionProvider.suggest(
            ProfileConfig.getProfiles().stream().map(File::getName).map(StringArgumentType::escapeIfRequired), input
    );

    public DeleteProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE).then(
                Commands.literal("delete").then(
                        Commands.argument("profileName", StringArgumentType.string())
                                .suggests(AVAILABLE_PROFILES)
                                .executes((command) -> execute(
                                        command.getSource(),
                                        StringArgumentType.getString(command, "profileName")
                                ))
                ))
        );
    }

    private static int execute(CommandSourceStack source, String profileName) {
        if (ProfileConfig.deleteProfile(profileName)) {
            source.sendSuccess(Component.translatable("§4❌ §rSuccessfully deleted profile: %s", profileName), true);

            if (ProfileConfig.getProfiles().size() > 0 && ClientProfilesConfig.CURRENT_PROFILE.get().equals(profileName)) {
                Optional<File> findFirst = ProfileConfig.getProfiles().stream().findFirst();

                if (findFirst.isPresent()) {
                    String switchTo = findFirst.get().getName();
                    ProfileConfig.switchProfile(switchTo);

                    source.sendSuccess(Component.translatable("§6⚡ §rCurrent profile switched to: %s", switchTo), true);
                    source.sendSuccess(Component.translatable("§9♦ §rPlease §crestart your game§r for changes to take effect."), true);
                }
            }

            return 1;
        }

        boolean isReadOnly = false;

        if (ProfileConfig.profileExists(profileName)) {
            try {
                isReadOnly = ProfileConfig.loadProfile(profileName).readOnly;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't delete profile '%s' for some reason!",
                "Profile exists: [%s]",
                "Profile is read-only: [%s]",
                "Profiles count: [%s]"
        ), profileName, ProfileConfig.profileExists(profileName), String.valueOf(isReadOnly), String.valueOf(ProfileConfig.getProfiles().size())));

        return -1;
    }
}
