package io.thimblebird.clientprofiles.events;

import io.thimblebird.clientprofiles.ClientProfiles;
import io.thimblebird.clientprofiles.commands.*;
import io.thimblebird.clientprofiles.config.ClientProfilesConfig;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ClientProfiles.MOD_ID)
public class CommandsEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterClientCommandsEvent event) {
        if (!ClientProfilesConfig.ENABLE.get()) return;

        new ClientProfileCommand(event.getDispatcher());
        new CreateProfileCommand(event.getDispatcher());
        new DeleteProfileCommand(event.getDispatcher());
        new ListProfilesCommand(event.getDispatcher());
        new SaveProfileCommand(event.getDispatcher());
        new SwitchProfileCommand(event.getDispatcher());
    }
}
