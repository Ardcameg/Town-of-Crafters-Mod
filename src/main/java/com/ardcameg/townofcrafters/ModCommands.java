package com.ardcameg.townofcrafters;

import com.ardcameg.townofcrafters.jobs.*;
import com.ardcameg.townofcrafters.network.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;

@EventBusSubscriber(modid = TownOfCrafters.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("role")

                        // ===== /role info =====
                        .then(Commands.literal("info")
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();

                                Role role = player.getData(ModAttachments.PLAYER_ROLE);

                                role.sendRoleInfoToPlayer(player);
                                return 1;
                            })
                        ) // ← info の設定はここまで

                        // ===== /role set <roleName> =====
                        .then(Commands.literal("set")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("roleName", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();

                                            String roleStr = StringArgumentType.getString(context, "roleName").toUpperCase();

                                            try {
                                                // 文字列からEnumを取得
                                                Role newRole = Role.valueOf(roleStr);
                                                // プレイヤーにデータをセット
                                                player.setData(ModAttachments.PLAYER_ROLE, newRole);

                                                // その役職を象徴するアイテム表示
                                                displayRoleItem(player, newRole);

                                                // Witchになったら即座にロール(抽選)を回す
                                                if (newRole == Role.WITCH) {
                                                    Witch.rollEffects(player);
                                                }

                                                context.getSource().sendSuccess(() -> Component.literal("Successfully set role to: §a" + newRole.getDisplayName()), true);
                                                return 1;
                                            } catch (IllegalArgumentException e) {
                                                // 存在しない役職名が入力された場合
                                                context.getSource().sendFailure(Component.literal("§cUnknown role! Available roles: Miner, Warrior, Tank, Witch, Gambler, Builder, Judge"));
                                                return 0;
                                            }
                                        })
                                )
                        ) // ← set の設定はここまで

                        .then(Commands.literal("random")
                            .requires(source -> source.hasPermission(2))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();

                                // ランダムに役職を選ぶ
                                Role[] allRoles = Role.values();
                                Role[] rolesPoll = Arrays.copyOfRange(allRoles, 1, allRoles.length);
                                Role randomRole = rolesPoll[player.getRandom().nextInt(rolesPoll.length)];

                                // プレイヤーにデータをセット
                                player.setData(ModAttachments.PLAYER_ROLE, randomRole);

                                // その役職を象徴するアイテム表示
                                displayRoleItem(player, randomRole);

                                // Witchになったら即座にロールを回す
                                if (randomRole == Role.WITCH) {
                                    Witch.rollEffects(player);
                                }

                                randomRole.sendRoleInfoToPlayer(player);

                                return 1;
                            })
                        ) // ← random の設定はここまで
        );
    }

    private static void displayRoleItem(ServerPlayer player, Role role) {
        ItemStack displayStack = new ItemStack(role.getDisplayItem());
        PacketDistributor.sendToPlayer(player, new PlayRoleAnimationPayload(displayStack));
    }
}