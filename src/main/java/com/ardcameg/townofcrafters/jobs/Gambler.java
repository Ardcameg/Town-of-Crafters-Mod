package com.ardcameg.townofcrafters.jobs;

import com.ardcameg.townofcrafters.Config;
import com.ardcameg.townofcrafters.damage.ModDamageSources;
import com.ardcameg.townofcrafters.TocUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class Gambler {

    public static void onAttack(LivingDamageEvent.Pre event, ServerPlayer player) {
        int probability = Config.gamblerProbability.get();

        LivingEntity attacker  = player;

        if (event.getEntity() instanceof Enemy) {
            // 確率判定
            if (attacker.getRandom().nextInt(100) < probability) {
                success(event);
            } else {
                fail(event, attacker);
            }
        }
    }

    private static void success(LivingDamageEvent.Pre event) {

        // ワンパン
        TocUtils.lightningEffect(event.getEntity());
        event.setNewDamage(event.getEntity().getMaxHealth());

    }

    private static void fail(LivingDamageEvent.Pre event, LivingEntity attacker) {
        LivingEntity entity = event.getEntity();

        float damageMulti = Config.gamblerDamageMulti.get().floatValue();
        float damageCapP = Config.gamblerDamageCap.get().floatValue();
        float damage = entity.getHealth();
        float cap = attacker.getMaxHealth() * damageCapP;
        damage = Math.min(damage, cap); // ダメージの上限を適用

        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);

        // ダメージを反射
        attacker.hurt(ModDamageSources.lose_bet(attacker.level(), attacker), damage);
        event.setNewDamage(0f);
    }
}
