package io.thimblebird.clientprofiles;

import com.mojang.logging.LogUtils;
import io.thimblebird.clientprofiles.config.ClientProfilesConfig;
import io.thimblebird.clientprofiles.config.ProfileConfig;
import io.thimblebird.clientprofiles.util.ProfileUtils;
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
            if (!ClientProfilesConfig.ENABLE.get()) {
                LOGGER.warn("Client Profiles are disabled; see `/config/clientprofiles-client.toml` to enable again.");
                return;
            }

            if (ClientProfilesConfig.CREATE_DEFAULT.get()) {
                // setup default profile config
                HashMap<String, Object> options = new HashMap<>();

                options.put("displayName", "Default");
                options.put("credits", "Every developer of every mod ♥");
                options.put("readOnly", true);

                ProfileConfig defaultProfileConfig = new ProfileConfig("default", options);
                defaultProfileConfig.createProfile("Copy this profile and create your own! (remember to change `readOnly` to `false`)");
            }

            // switch to default profile if there is no active profile currently
            if (ProfileUtils.getId().isEmpty() && ProfileUtils.exists(ClientProfilesConfig.DEFAULT_PROFILE.get())) {
                ProfileConfig.switchProfile(ClientProfilesConfig.DEFAULT_PROFILE.get());
            }
        }
    }
}
