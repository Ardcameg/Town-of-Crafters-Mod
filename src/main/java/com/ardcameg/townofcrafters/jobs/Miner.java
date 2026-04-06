package com.ardcameg.townofcrafters.jobs;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

public class Miner {
    public static void onMine(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ServerLevel serverlevel = (ServerLevel) player.level();

        // 壊したブロックが「石」または「深層岩」かチェック
        if (state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE)) {
            // 経験値を計算
            int extraExp = player.getRandom().nextInt(2) + 1;
            state.getBlock().popExperience(serverlevel, event.getPos(), extraExp);
        }

        return;
    }
}
