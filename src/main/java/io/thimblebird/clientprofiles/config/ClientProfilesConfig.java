package io.thimblebird.clientprofiles.config;

import net.minecraftforge.common.ForgeConfigSpec;

import static io.thimblebird.clientprofiles.ClientProfiles.MOD_ID;

public class ClientProfilesConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE;
    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_PROFILE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CREATE_DEFAULT;

    static {
        BUILDER.push(MOD_ID);

        ENABLE = BUILDER.comment("Enable the use of client profiles").define("enable", true);
        DEFAULT_PROFILE = BUILDER.comment("Default client profile").define("defaultProfile", "default");
        CREATE_DEFAULT = BUILDER.comment("Creates (and overwrites if present) a default client profile on game start").define("createDefault", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
