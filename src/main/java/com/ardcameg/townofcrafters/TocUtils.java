package com.ardcameg.townofcrafters;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

public final class TocUtils {

    /**
     * 指定したエンティティに見た目だけの雷を発生させる
     * @param entity
     */
    public static void lightningEffect(LivingEntity entity) {
        ServerLevel serverLevel = (ServerLevel) entity.level();

        LightningBolt visualLightning = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
        visualLightning.setVisualOnly(true);
        visualLightning.setPos(entity.position());
        serverLevel.addFreshEntity(visualLightning);
    }
}
