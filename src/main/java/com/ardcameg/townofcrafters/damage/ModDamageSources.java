package com.ardcameg.townofcrafters.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ModDamageSources {

    public static DamageSource lose_bet(Level level, Entity entity) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ModDamageTypes.LOSE_BET),
                entity
        );
    }

    public static DamageSource guilt(Level level, Entity entity) {
        return new DamageSource(
                level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ModDamageTypes.GUILT),
                entity
        );
    }
}
