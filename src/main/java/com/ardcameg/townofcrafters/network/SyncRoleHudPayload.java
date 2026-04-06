package com.ardcameg.townofcrafters.network;

import com.ardcameg.townofcrafters.TownOfCrafters;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncRoleHudPayload(String text, int color) implements CustomPacketPayload {
    public static final Type<SyncRoleHudPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TownOfCrafters.MODID, "sync_role_hud"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRoleHudPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.text());
                buf.writeInt(payload.color());
            },
            buf -> new SyncRoleHudPayload(buf.readUtf(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}