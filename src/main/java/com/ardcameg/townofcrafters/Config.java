package com.ardcameg.townofcrafters;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("tank");


        tankDamageReduction = BUILDER
                .comment("Damage reduction percentage for the Tank")
                .defineInRange("tank_damage_reduction", 0.4, 0.0, 1.0);
        tankCounterDamageMulti = BUILDER
                .comment("The multiplier applied to the [Counterattack] of Tank's [Self-destruction].")
                .defineInRange("tank_counterattack_multi", 2.0, 0.0, 100.0);
        tankCounterDamageDist = BUILDER
                .comment("The damage radius of Tank's [Self-Destruct].")
                .defineInRange("tank_counterattack_distance", 5.0, 0.0, 100.0);

        BUILDER.pop();
    }
    public static final ModConfigSpec.DoubleValue tankDamageReduction;
    public static final ModConfigSpec.DoubleValue tankCounterDamageMulti;
    public static final ModConfigSpec.DoubleValue tankCounterDamageDist;

    static {
        BUILDER.push("gambler");

        gamblerProbability = BUILDER
                .comment("The percentage of Gambler's [All-in!] being triggered.")
                .defineInRange("gambler_probability", 66, 0, 100);
        gamblerDamageMulti = BUILDER
                .comment("Damage multiplier when Gambler's [All-in!] does not trigger (This multiplier is applied last).")
                .defineInRange("gambler_damage_multi", 1.0, 0.0, 100.0);
        gamblerDamageCap = BUILDER
                .comment("The percentage of max health that serves as the damage cap when Gambler's [All-in!] does not trigger.")
                .defineInRange("gambler_damage_cap", 0.75, 0.0, 1.0);

        BUILDER.pop();
    }
    public static final ModConfigSpec.IntValue gamblerProbability;
    public static final ModConfigSpec.DoubleValue gamblerDamageMulti;
    public static final ModConfigSpec.DoubleValue gamblerDamageCap;

    static {
        BUILDER.push("ultimate_kind");


        ultimateKindMinDistance = BUILDER
                .comment("The minimum distance at which friendly Mobs redirects damage to Ultimate Kind.")
                .defineInRange("ultimate_kind_min_distance", 5.0, 1.0, 16.0);
        ultimateKindMaxFriend = BUILDER
                .comment("The maximum value of Ultimate Kind's [Friend].")
                .defineInRange("ultimate_kind_max_distance", 10.0, 0.0, 100.0);

        BUILDER.pop();
    }
    public static final ModConfigSpec.DoubleValue ultimateKindMaxFriend;
    public static final ModConfigSpec.DoubleValue ultimateKindMinDistance;

    public static final ModConfigSpec SPEC = BUILDER.build();
}
