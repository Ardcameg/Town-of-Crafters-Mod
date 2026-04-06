package com.ardcameg.townofcrafters.jobs;

import com.ardcameg.townofcrafters.Config;
import com.ardcameg.townofcrafters.ModAttachments;
import com.ardcameg.townofcrafters.Role;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class UltimateKind {

    // 無限ループを防ぐための安全装置フラグ
    private static boolean isDischarging = false; // 放出攻撃中か
    private static boolean isIntercepting = false; // 肩代わり処理中か

    public static void onDamaged(LivingDamageEvent.Pre event) {
        LivingEntity victim = event.getEntity();
        DamageSource source = event.getSource();

        if (event.getNewDamage() <= 0) return;

        // 放出: 自分が敵から攻撃されたとき
        if (victim instanceof ServerPlayer player && player.getData(ModAttachments.PLAYER_ROLE) == Role.ULTIMATE_KIND) {
            // 自分が「肩代わり」でダメージを受けている最中は、放出を発動させない
            if (!isIntercepting && source.getEntity() instanceof Enemy attacker) {
                int friend = player.getData(ModAttachments.KIND_FRIEND);
                if (friend > 0) {
                    // ダメージを無効化
                    event.setNewDamage(0.0f);

                    // 放出ダメージを与える（安全装置をONにする)
                    isDischarging = true;
                    try {
                        ((LivingEntity)attacker).hurt(player.damageSources().magic(), friend);
                    } finally {
                        isDischarging = false;
                    }

                    // [Friend]をリセット
                    player.setData(ModAttachments.KIND_FRIEND, 0);
                    return;
                }
            }
        }
        // 放出: 自分が友好Mobを攻撃したとき
        if (source.getEntity() instanceof ServerPlayer attacker && attacker.getData(ModAttachments.PLAYER_ROLE) == Role.ULTIMATE_KIND) {
            if (victim instanceof Mob && !(victim instanceof Enemy)) {
                int friend = attacker.getData(ModAttachments.KIND_FRIEND);

                // 元々のパンチのダメージを無効化
                event.setNewDamage(0.0f);

                if (friend > 0) {
                    isDischarging = true;
                    try {
                        victim.hurt(attacker.damageSources().magic(), friend);
                    } finally {
                        isDischarging = false;
                    }
                    attacker.setData(ModAttachments.KIND_FRIEND, 0);
                }
                return;
            }
        }

        // 肩代わり: 友好Mobがダメージを受けたとき
        if (victim instanceof Mob && !(victim instanceof Enemy)) {
            // もしこれが「放出ダメージ」なら、肩代わりしない
            if (isDischarging) return;

            Level level = victim.level();

            float minDistance = (float) Config.ultimateKindMinDistance.getAsDouble();
            float maxDistance = (float) Config.ultimateKindMaxFriend.getAsDouble();

            // 範囲内の全プレイヤーを取得 とりあえず最大値を決めておく
            AABB searchBox = victim.getBoundingBox().inflate(maxDistance);
            List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, searchBox,
                    p -> p.getData(ModAttachments.PLAYER_ROLE) == Role.ULTIMATE_KIND);

            ServerPlayer savior = null;
            for (ServerPlayer p : players) {
                int friend = p.getData(ModAttachments.KIND_FRIEND);
                // 範囲計算
                double radius = Math.max(minDistance, friend);

                if (p.distanceTo(victim) <= radius) {
                    savior = p; // 条件を満たしたUltimate Kindを見つけた
                    break;
                }
            }

            if (savior != null) {
                float damageToTake = event.getNewDamage();

                // 友好Mobのダメージを0にする
                event.setNewDamage(0.0f);

                // 肩代わりとして自分がダメージを受ける (安全装置ON)
                isIntercepting = true;
                try {
                    savior.hurt(source, damageToTake);
                } finally {
                    isIntercepting = false;
                }

                // [Friend]: +1
                int currentFriend = savior.getData(ModAttachments.KIND_FRIEND);
                savior.setData(ModAttachments.KIND_FRIEND, currentFriend + 1);
            }
        }
    }
}