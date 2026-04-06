package com.ardcameg.townofcrafters.network;

import com.ardcameg.townofcrafters.TownOfCrafters;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

// "CustomPacketPayload" を実装するレコード。手紙の中身は「表示したいアイテムスタック」。
public record PlayRoleAnimationPayload(ItemStack stack) implements CustomPacketPayload {

    public static final Type<PlayRoleAnimationPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "play_role_animation"));

    // 通信で送るためのエンコーダー/デコーダー
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayRoleAnimationPayload> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            PlayRoleAnimationPayload::stack,
            PlayRoleAnimationPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}