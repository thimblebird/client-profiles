package io.thimblebird.clientprofiles;

import com.mojang.logging.LogUtils;
import io.thimblebird.clientprofiles.config.ClientProfilesConfig;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import java.util.HashMap;

@Mod(ClientProfiles.MOD_ID)
public class ClientProfiles {
    public static final String MOD_ID = "clientprofiles";
    public static final String MOD_COMMANDS_NAMESPACE = "clientprofile";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ClientProfiles() {
        // create default mod config
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientProfilesConfig.SPEC);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            if (ClientProfilesConfig.CREATE_DEFAULT.get()) {
                // setup default profile config
                HashMap<String, Object> options = new HashMap<>();

                options.put("displayName", "Default Profile");
                options.put("credits", "You ♥");
                options.put("readOnly", true);

                ProfileConfig defaultProfileConfig = new ProfileConfig("default", options);
                defaultProfileConfig.createProfile("This is the default profile; created when first starting the game.");
            }
        }
    }
}
