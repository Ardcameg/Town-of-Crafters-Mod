package com.ardcameg.townofcrafters.jobs;

import com.ardcameg.townofcrafters.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class Warrior {
    public static void onKill(ServerPlayer player) {
        int kills = player.getData(ModAttachments.WARRIOR_KILLS);
        player.setData(ModAttachments.WARRIOR_KILLS, kills + 1);
    }

    public static void onDeath() {
        // MobAttachmentで処理されるが、形式上...
    }

    public static void updateWarriorMorale(ServerPlayer serverPlayer) {
        int kills = serverPlayer.getData(ModAttachments.WARRIOR_KILLS);

        // 討伐数に応じて、必要な効果を付与していく
        if (kills >= 1000) serverPlayer.addEffect(new MobEffectInstance(MobEffects.HUNGER, 25, 3, false, false, false)); // Hunger Lv.4

        if (kills >= 500) serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25, 4, false, false, false)); // Strength Lv.5
        else if (kills >= 10) serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25, 0, false, false, false)); // Strength Lv.1

        if (kills >= 200) serverPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25, 2, false, false, false)); // Speed Lv.3
        if (kills >= 100) serverPlayer.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 25, 4, false, false, false)); // HP Boost Lv.5
        if (kills >= 50) serverPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 25, 0, false, false, false)); // Night Vision Lv.1
        if (kills >= 25) serverPlayer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 25, 0, false, false, false)); // Resistance Lv.1
    }
}
