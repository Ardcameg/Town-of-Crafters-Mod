package com.ardcameg.townofcrafters;

import com.ardcameg.townofcrafters.jobs.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = TownOfCrafters.MODID)
public class RoleEvents {

    // ブロックを壊したとき
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = player.level();

        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;

        // プレイヤーの現在の役職を取得
        Role role = player.getData(ModAttachments.PLAYER_ROLE);

        // Miner: [Even Stones are Ores]
        if (role == Role.MINER) {
            Miner.onMine(event);
        }
    }

    // Mobが死んだとき
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Mobがプレイヤーによって死んだとき
        if(event.getSource().getEntity() instanceof ServerPlayer player) {

            Role role = player.getData(ModAttachments.PLAYER_ROLE);

            // Warrior: 討伐数 +1
            if (role == Role.WARRIOR) {
                Warrior.onKill(player);
            }

            // Judge:[Self-blame]
            if (role == Role.JUDGE) {
                Judge.onKill(event);
            }
        }
        // プレイヤーが死んだとき
        if(event.getEntity() instanceof ServerPlayer player) {
            Role role = player.getData(ModAttachments.PLAYER_ROLE);

            // Warrior: 討伐数リセット
            if (role == Role.WARRIOR) {
                Warrior.onDeath();
            }
            // Tank: [Self-Destruct]
            if (role == Role.TANK) {
                Tank.onDeath(event);
            }
        }
    }

    // プレイヤーのティック
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event){
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer)) return;
        if (!serverPlayer.isAlive()) return;

        Role role = serverPlayer.getData(ModAttachments.PLAYER_ROLE);

        // Warrior - [Morale]
        if(role == Role.WARRIOR){
            Warrior.updateWarriorMorale(serverPlayer);
        }
        // Witch - [Brewing]
        if(role == Role.WITCH) {
            Witch.applyEffects(serverPlayer);
        }


    }

    // ダメージを受けたとき (受ける直前)
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        LivingEntity attacker = (LivingEntity) event.getSource().getEntity();

        UltimateKind.onDamaged(event);

        // プレイヤーが攻撃したとき
        if (attacker instanceof ServerPlayer player) {

            Role attackerRole = attacker.getData(ModAttachments.PLAYER_ROLE);

            // Gambler: [All-in!]
            if (attackerRole == Role.GAMBLER) {
                Gambler.onAttack(event, player);
            }
        }

        // プレイヤーが攻撃を受けたとき
        if (target instanceof ServerPlayer player) {
            // プレイヤーの現在の役職を取得
            Role role = player.getData(ModAttachments.PLAYER_ROLE);

            if (role == Role.TANK) {
                Tank.onDamaged(event);
            }

            if(role == Role.GOLEM) {
                Golem.onDamaged(event);
            }
        }

    }

    // リスポーン時の処理 =====
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Role role = player.getData(ModAttachments.PLAYER_ROLE);

            // リスポーンしたのがWitchなら、効果を引き直す
            if (role == Role.WITCH) {
                Witch.rollEffects(player);
            }
        }
    }
}