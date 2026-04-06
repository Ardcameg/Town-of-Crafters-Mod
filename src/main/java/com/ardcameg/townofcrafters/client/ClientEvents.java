package com.ardcameg.townofcrafters.client;

import com.ardcameg.townofcrafters.TownOfCrafters;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class ClientEvents {

    // HUDの登録
    @EventBusSubscriber(modid = TownOfCrafters.MODID, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "role_hud"), RoleHudOverlay::render);
        }
    }

    // 毎Tickタイマーを減らす処理
    @EventBusSubscriber(modid = TownOfCrafters.MODID, value = Dist.CLIENT)
    public static class GameClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (RoleHudOverlay.popTimer > 0) {
                RoleHudOverlay.popTimer--;
            }
        }
    }
}