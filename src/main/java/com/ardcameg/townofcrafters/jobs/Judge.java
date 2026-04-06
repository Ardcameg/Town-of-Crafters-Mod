package com.ardcameg.townofcrafters.jobs;

import com.ardcameg.townofcrafters.ModAttachments;
import com.ardcameg.townofcrafters.damage.ModDamageSources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class Judge {

    public static void onKill(LivingDeathEvent event){
        ServerPlayer serverPlayer = (ServerPlayer) event.getSource().getEntity();
        
        int justice = serverPlayer.getData(ModAttachments.JUDGE_JUSTICE);

        if (event.getEntity() instanceof Enemy) {
            // モンスターを倒した場合はJusticeを+1
            serverPlayer.setData(ModAttachments.JUDGE_JUSTICE, justice + 1);
        } else {
            // モンスター以外を倒した場合
            // Justiceと同じ値のダメージを自分に与え、リセット
            serverPlayer.hurt(ModDamageSources.guilt(serverPlayer.level(), serverPlayer), (float) justice);
            serverPlayer.setData(ModAttachments.JUDGE_JUSTICE, (int)(serverPlayer.getMaxHealth() / 2));
        }
        
    }
}
