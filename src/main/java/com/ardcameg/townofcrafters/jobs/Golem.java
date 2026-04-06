package com.ardcameg.townofcrafters.jobs;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class Golem {
    public static void onDamaged(LivingDamageEvent.Pre event) {

        DamageSource source = event.getSource();

        if(source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

        if(source.is(DamageTypes.FALL) || source.is(DamageTypes.FALLING_BLOCK) ||
                source.is(DamageTypes.MAGIC) || source.is(DamageTypes.DROWN) ||
                source.is(DamageTypes.INDIRECT_MAGIC)) {

                event.setNewDamage(Float.MAX_VALUE);
        }else {
            event.setNewDamage(0.0f);
        }
    }
}
