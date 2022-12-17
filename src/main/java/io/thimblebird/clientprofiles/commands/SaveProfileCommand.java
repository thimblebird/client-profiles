package io.thimblebird.clientprofiles.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_COMMANDS_NAMESPACE;

@SuppressWarnings("unused")
public class SaveProfileCommand {
    public SaveProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(MOD_COMMANDS_NAMESPACE).then(
                Commands.literal("save").executes((command) -> execute(command.getSource())))
        );
    }

    private static int execute(CommandSourceStack source) {
        String currentProfile = ProfileConfig.getCurrentProfileName();

        if (ProfileConfig.profileExists(currentProfile)) {
            /*
            // scramble together files and folders to save based on profile options
            Collection<File> includeFiles = new ArrayList<>();
            Collection<File> includeDirectories = new ArrayList<>();

            Path rootDir = FMLPaths.GAMEDIR.get();
            for (File dir : FileSystemReader.getDirs(rootDir)) {
                if (dir.getName().equals("config")) continue;
                if (dir.getPath())

                includeDirectories.add(dir);
            }

            source.sendSuccess(Component.translatable("Successfully overwritten '" + currentProfile + "'! (not actually, yet)"), true);
            */

            source.sendFailure(Component.literal("This command isn't implemented yet™. Please manually put files into the respective profile folder."));

            return 1;
        }

        source.sendFailure(Component.translatable(String.join(
                "\n",
                "Couldn't save profile '%s' for some reason!",
                "Profile exists: [%s]"
        ), currentProfile, ProfileConfig.profileExists(currentProfile)));

        return -1;
    }
}
