package com.ardcameg.townofcrafters.jobs;

import com.ardcameg.townofcrafters.Config;
import com.ardcameg.townofcrafters.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class Tank {
    public static void onDeath(LivingDeathEvent event) {
        float damageMulti = Config.tankCounterDamageMulti.get().floatValue();
        float damageDist = Config.tankCounterDamageDist.get().floatValue();

        ServerPlayer player = (ServerPlayer) event.getEntity();

        // 殺したのがモンスターなら爆発
        if (event.getSource().getEntity() instanceof Enemy) {
            int counter = player.getData(ModAttachments.TANK_COUNTERATTACK);
            float explosionDamage = counter * damageMulti;

            // 自分を中心とした立方体エリアを作成し、Mobを取得
            AABB area = player.getBoundingBox().inflate(damageDist);
            List<Mob> targets = player.level().getEntitiesOfClass(Mob.class, area, entity -> entity instanceof Enemy);

            // 取得した全モンスターにダメージを与える
            for (Mob target : targets) {
                // プレイヤーによる攻撃としてダメージを与える
                target.hurt(player.damageSources().playerAttack(player), explosionDamage);
            }

            // リセット
            player.setData(ModAttachments.TANK_COUNTERATTACK, 0);
        }
    }

    public static void onDamaged(LivingDamageEvent.Pre event) {
        float damageReduction = Config.tankDamageReduction.get().floatValue();

        ServerPlayer player = (ServerPlayer) event.getEntity();

        // ダメージを -40% (0.6倍) にする
        float originalDamage = event.getNewDamage();
        event.setNewDamage(originalDamage * (1.0f - damageReduction));

        // 攻撃元が敵だったらカウンター蓄積
        if (event.getSource().getEntity() instanceof Enemy) {
            int counter = player.getData(ModAttachments.TANK_COUNTERATTACK);
            player.setData(ModAttachments.TANK_COUNTERATTACK, counter + 1);
        }
    }
}
