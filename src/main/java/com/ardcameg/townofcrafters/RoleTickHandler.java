package com.ardcameg.townofcrafters;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = TownOfCrafters.MODID)
public class RoleTickHandler {

    private static final Map<UUID, String> lastHudTexts = new HashMap<>();

    private static final ResourceLocation ROLE_BREAK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_break_speed");
    private static final ResourceLocation ROLE_ATTACK_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_attack_damage");
    private static final ResourceLocation ROLE_ATTACK_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_attack_speed");
    private static final ResourceLocation ROLE_MAX_HEALTH_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_max_health");
    private static final ResourceLocation ROLE_MOVEMENT_SPEED_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_movement_speed");
    private static final ResourceLocation ROLE_GRAVITY_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_gravity");
    private static final ResourceLocation ROLE_KNOCKBACK_RES_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_knockback_res");
    private static final ResourceLocation ROLE_FALL_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_fall_damage");
    private static final ResourceLocation ROLE_STEP_HEIGHT_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_step_height");
    private static final ResourceLocation ROLE_INTERACTION_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_interaction");
    private static final ResourceLocation ROLE_SCALE_ID = ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_scale");

    // 役職記憶用
    private static final Map<UUID, Role> lastPlayerRole = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        Role currentRole = player.getData(ModAttachments.PLAYER_ROLE);
        Role previousRole = lastPlayerRole.getOrDefault(player.getUUID(), Role.NONE);

        // 役職が変更された時だけ、ステータスを更新する
        if (currentRole != previousRole) {
            // 全役職共通の補正を一度確実にリセットする
            removeRoleModifiers(player);

            // 役職に応じて、補正を付与する
            switch (currentRole) {
                case MINER:
                    applyRoleModifier(player, Attributes.BLOCK_BREAK_SPEED, ROLE_BREAK_SPEED_ID, 4.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.ATTACK_DAMAGE, ROLE_ATTACK_DAMAGE_ID, -0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.ATTACK_SPEED, ROLE_ATTACK_SPEED_ID, -0.75, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    break;
                case WARRIOR:
                    applyRoleModifier(player, Attributes.ATTACK_DAMAGE, ROLE_ATTACK_DAMAGE_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.ATTACK_SPEED, ROLE_ATTACK_SPEED_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.BLOCK_BREAK_SPEED, ROLE_BREAK_SPEED_ID, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    break;
                case TANK:
                    applyRoleModifier(player, Attributes.MAX_HEALTH, ROLE_MAX_HEALTH_ID, 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.KNOCKBACK_RESISTANCE, ROLE_KNOCKBACK_RES_ID, 1.0, AttributeModifier.Operation.ADD_VALUE);
                    applyRoleModifier(player, Attributes.ATTACK_SPEED, ROLE_ATTACK_SPEED_ID, -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.MOVEMENT_SPEED, ROLE_MOVEMENT_SPEED_ID, -0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.GRAVITY, ROLE_GRAVITY_ID, 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    break;
                case WITCH:
                    break;
                case BUILDER:
                    applyRoleModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, ROLE_FALL_DAMAGE_ID, -1.0, AttributeModifier.Operation.ADD_VALUE);
                    applyRoleModifier(player, Attributes.STEP_HEIGHT, ROLE_STEP_HEIGHT_ID, 0.6, AttributeModifier.Operation.ADD_VALUE);
                    applyRoleModifier(player, Attributes.BLOCK_INTERACTION_RANGE, ROLE_INTERACTION_ID, 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.ATTACK_DAMAGE, ROLE_ATTACK_DAMAGE_ID, -1.0, AttributeModifier.Operation.ADD_VALUE);
                    applyRoleModifier(player, Attributes.MAX_HEALTH, ROLE_MAX_HEALTH_ID, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    break;
                case JUDGE:
                    applyRoleModifier(player, Attributes.ATTACK_DAMAGE, ROLE_ATTACK_DAMAGE_ID, 2.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    applyRoleModifier(player, Attributes.ATTACK_SPEED, ROLE_ATTACK_SPEED_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                    break;
                case ULTIMATE_KIND:
                    break;
                case GOLEM:
                    applyRoleModifier(player, Attributes.KNOCKBACK_RESISTANCE, ROLE_KNOCKBACK_RES_ID, 1.0, AttributeModifier.Operation.ADD_VALUE);
                    break;
            }

            // 今回の役職を記憶する
            lastPlayerRole.put(player.getUUID(), currentRole);
        }


        // HUDに表示するテキストを作成
        String currentHudText = "";
        // 固有数値がある役職のみ、数字を文字列にする
        switch (currentRole) {
            case WARRIOR:
                currentHudText = String.valueOf(player.getData(ModAttachments.WARRIOR_KILLS));
                break;
            case TANK:
                currentHudText = String.valueOf(player.getData(ModAttachments.TANK_COUNTERATTACK));
                break;
            case JUDGE:
                currentHudText = String.valueOf(player.getData(ModAttachments.JUDGE_JUSTICE));
                break;
            case ULTIMATE_KIND:
                currentHudText = String.valueOf(player.getData(ModAttachments.KIND_FRIEND));
                break;
            default:
                // それ以外の役職（Miner等）は画面に何も表示しないため空文字
                currentHudText = "";
                break;
        }

        // 前回送信したテキストと違っていたら、文字と「色」のパケットを送信
        String previousText = lastHudTexts.getOrDefault(player.getUUID(), "");
        if (!currentHudText.equals(previousText)) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                    (net.minecraft.server.level.ServerPlayer) player,
                    new com.ardcameg.townofcrafters.network.SyncRoleHudPayload(currentHudText, currentRole.getColor())
            );
            lastHudTexts.put(player.getUUID(), currentHudText);
        }
    }

    private static void removeRoleModifiers(Player player) {
        removeSingleModifier(player, Attributes.BLOCK_BREAK_SPEED, ROLE_BREAK_SPEED_ID);
        removeSingleModifier(player, Attributes.ATTACK_DAMAGE, ROLE_ATTACK_DAMAGE_ID);
        removeSingleModifier(player, Attributes.ATTACK_SPEED, ROLE_ATTACK_SPEED_ID);
        removeSingleModifier(player, Attributes.MAX_HEALTH, ROLE_MAX_HEALTH_ID);
        removeSingleModifier(player, Attributes.MOVEMENT_SPEED, ROLE_MOVEMENT_SPEED_ID);
        removeSingleModifier(player, Attributes.GRAVITY, ROLE_GRAVITY_ID);
        removeSingleModifier(player, Attributes.KNOCKBACK_RESISTANCE, ROLE_KNOCKBACK_RES_ID);
        removeSingleModifier(player, Attributes.FALL_DAMAGE_MULTIPLIER, ROLE_FALL_DAMAGE_ID);
        removeSingleModifier(player, Attributes.STEP_HEIGHT, ROLE_STEP_HEIGHT_ID);
        removeSingleModifier(player, Attributes.BLOCK_INTERACTION_RANGE, ROLE_INTERACTION_ID);
        removeSingleModifier(player, Attributes.SCALE, ROLE_SCALE_ID);
    }

    private static void removeSingleModifier(Player player, Holder<Attribute> attribute, ResourceLocation modifierId) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }
    private static void applyRoleModifier(Player player, Holder<Attribute> attribute, ResourceLocation modifierId,
                                          double value, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        AttributeModifier existingModifier = instance.getModifier(modifierId);
        if (existingModifier != null) {
            if (existingModifier.amount() == value && existingModifier.operation() == operation) {
                return;
            }
            instance.removeModifier(modifierId);
        }

        instance.addTransientModifier(new AttributeModifier(modifierId, value, operation));
    }

    // プレイヤーが死んでリスポーンした時、能力が初期化されてしまうのへの対策
    @SubscribeEvent
    public static void onPlayerRespawn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        // 記憶している前回の役職を一旦消去する
        // これにより、リスポーン直後のTickで強制的に能力が再付与される！
        lastPlayerRole.remove(event.getEntity().getUUID());
    }

    // ディメンション移動でも能力が消えることがあるため、同様にリセットする
    @SubscribeEvent
    public static void onPlayerChangeDimension(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        lastPlayerRole.remove(event.getEntity().getUUID());
    }
}