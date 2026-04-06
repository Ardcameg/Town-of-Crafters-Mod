package com.ardcameg.townofcrafters.network;

import com.ardcameg.townofcrafters.TownOfCrafters;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = TownOfCrafters.MODID)
public class ModNetwork {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(TownOfCrafters.MODID);

        registrar.playToClient(
                PlayRoleAnimationPayload.TYPE,
                PlayRoleAnimationPayload.CODEC,
                ModNetwork::handlePlayRoleAnimation
        );
        registrar.playToClient(
                SyncRoleHudPayload.TYPE,
                SyncRoleHudPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    // 現在の文字と送られてきた文字が違う場合
                    if (!com.ardcameg.townofcrafters.client.RoleHudOverlay.currentText.equals(payload.text())) {
                        // 新しい文字と色を設定し、タイマーをリセットする
                        com.ardcameg.townofcrafters.client.RoleHudOverlay.currentText = payload.text();
                        com.ardcameg.townofcrafters.client.RoleHudOverlay.currentColor = payload.color();
                        com.ardcameg.townofcrafters.client.RoleHudOverlay.popTimer = com.ardcameg.townofcrafters.client.RoleHudOverlay.ANIMATION_TICKS;
                    }
                })
        );
    }

    // パケットをクライアントが受け取った時の処理
    private static void handlePlayRoleAnimation(PlayRoleAnimationPayload payload, IPayloadContext context) {
        // クライアント側の処理として予約
        context.enqueueWork(() -> {
            // マイクラの画面に「トーテムのようにアイテムを浮かび上がらせる」メソッドを呼び出す
            Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack());
        });
    }
}