package com.ardcameg.townofcrafters.jobs;

import com.ardcameg.townofcrafters.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Witch {

    // 良性エフェクトのリスト (バニラのHolder形式で管理)
    private static final List<Holder<MobEffect>> BUFFS = Arrays.asList(
            MobEffects.WATER_BREATHING, // Lv.1
            MobEffects.MOVEMENT_SPEED,  // Lv.5
            MobEffects.NIGHT_VISION,    // Lv.1
            MobEffects.LUCK,            // Lv.1
            MobEffects.REGENERATION,    // Lv.3
            MobEffects.HERO_OF_THE_VILLAGE, // Lv.5
            MobEffects.DAMAGE_RESISTANCE, // Lv.1
            MobEffects.DAMAGE_BOOST     // Lv.5
    );

    // 悪性エフェクトのリスト
    private static final List<Holder<MobEffect>> DEBUFFS = Arrays.asList(
            MobEffects.BLINDNESS,       // Lv.1
            MobEffects.DARKNESS,        // Lv.1
            MobEffects.GLOWING,         // Lv.1
            MobEffects.JUMP,            // Lv.5
            MobEffects.DIG_SLOWDOWN,    // Lv.2
            MobEffects.POISON,          // Lv.1
            MobEffects.UNLUCK,          // Lv.1
            MobEffects.WEAKNESS,        // Lv.1
            MobEffects.HUNGER           // Lv.3
    );

    /**
     * リスポーン時、または就職直後に呼ばれる、効果の再抽選メソッド
     * @param player 対象のプレイヤー
     */
    public static void rollEffects(ServerPlayer player) {
        // ランダムに1つずつ選ぶ
        Holder<MobEffect> chosenBuff = BUFFS.get(player.getRandom().nextInt(BUFFS.size()));
        Holder<MobEffect> chosenDebuff = DEBUFFS.get(player.getRandom().nextInt(DEBUFFS.size()));

        // HolderからRegistry Key(文字列ID)を取り出して保存する
        ResourceLocation buffId = chosenBuff.getKey().location();
        ResourceLocation debuffId = chosenDebuff.getKey().location();

        player.setData(ModAttachments.WITCH_BUFF, buffId.toString());
        player.setData(ModAttachments.WITCH_DEBUFF, debuffId.toString());
    }

    /**
     * 毎Tick呼ばれる、効果を付与し続けるメソッド
     * @param player 対象のプレイヤー
     */
    public static void applyEffects(ServerPlayer player) {
        String buffStr = player.getData(ModAttachments.WITCH_BUFF);
        String debuffStr = player.getData(ModAttachments.WITCH_DEBUFF);

        // まだ抽選されていない場合は、今すぐ抽選する
        if (buffStr.isEmpty() || debuffStr.isEmpty()) {
            rollEffects(player);
            return; // 次のTickから
        }

        // 保存された文字列からMobEffectを取得
        applySpecificEffect(player, buffStr, true);
        applySpecificEffect(player, debuffStr, false);
    }

    // 文字列IDからエフェクトを探して付与するヘルパー
    private static void applySpecificEffect(ServerPlayer player, String effectIdStr, boolean isBuff) {
        ResourceLocation id = ResourceLocation.tryParse(effectIdStr);
        if (id == null) return;

        Optional<Holder.Reference<MobEffect>> effectOpt = BuiltInRegistries.MOB_EFFECT.getHolder(id);
        if (effectOpt.isPresent()) {
            Holder<MobEffect> effect = effectOpt.get();
            int level = determineLevel(effect, isBuff);

            // アイコンやパーティクルは非表示で、毎Tick上書きする
            player.addEffect(new MobEffectInstance(effect, 25, level, false, false, false));
        }
    }

    // 仕様に合わせたレベル(Amplifier)の決定
    private static int determineLevel(Holder<MobEffect> effect, boolean isBuff) {
        // 内部のレベル指定は 実際のLv - 1
        if (isBuff) {
            if (effect.equals(MobEffects.MOVEMENT_SPEED)) return 4; // Lv.5
            if (effect.equals(MobEffects.REGENERATION)) return 2; // Lv.3
            if (effect.equals(MobEffects.HERO_OF_THE_VILLAGE)) return 4; // Lv.5
            if (effect.equals(MobEffects.DAMAGE_BOOST)) return 4; // Lv.5
            return 0; // その他は Lv.1
        } else {
            if (effect.equals(MobEffects.JUMP)) return 4; // Lv.5
            if (effect.equals(MobEffects.DIG_SLOWDOWN)) return 1; // Lv.2
            if (effect.equals(MobEffects.HUNGER)) return 2; // Lv.3
            return 0; // その他は Lv.1
        }
    }
}