package com.ardcameg.townofcrafters;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    // Attachmentのレジストリを作成
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "toc"); // "mymod" はご自身のModIDに

    // プレイヤーのRoleを保持するAttachmentを登録
    public static final Supplier<AttachmentType<Role>> PLAYER_ROLE = ATTACHMENT_TYPES.register("player_role",
            () -> AttachmentType.builder(() -> Role.NONE) // 初期値は Role.NONE
                    .serialize(Codec.STRING.xmap(Role::valueOf, Role::name))
                    // 死亡時（リスポーン時）にデータを引き継ぐ
                    .copyOnDeath()
                    .build());

    // Minerの採掘数を保持するAttachment
    public static final Supplier<AttachmentType<Integer>> WARRIOR_KILLS = ATTACHMENT_TYPES.register("warrior_kills",
            () -> AttachmentType.builder(() -> 0) // 初期値は 0
                    .serialize(Codec.INT)
                    // 死んだらりセットする（引き継がない）
                    .build());

    // Tankのカウンター数を保持するAttachment
    public static final Supplier<AttachmentType<Integer>> TANK_COUNTERATTACK = ATTACHMENT_TYPES.register("tank_counterattack",
            () -> AttachmentType.builder(() -> 0) // 初期値は0
                    .serialize(Codec.INT)
                    .copyOnDeath() // 死んでも引き継ぐ(スキル発動でリセット)
                    .build());

    // Witchが現在持っている有益な効果のID
    public static final Supplier<AttachmentType<String>> WITCH_BUFF = ATTACHMENT_TYPES.register("witch_buff",
            () -> AttachmentType.builder(() -> "") // 初期値は空文字
                    .serialize(Codec.STRING)
                    .copyOnDeath() // リスポーン時にも判定で使うので一応引き継ぐ
                    .build());

    // Witchが現在持っている有害な効果のID
    public static final Supplier<AttachmentType<String>> WITCH_DEBUFF = ATTACHMENT_TYPES.register("witch_debuff",
            () -> AttachmentType.builder(() -> "") // 初期値は空文字
                    .serialize(Codec.STRING)
                    .copyOnDeath()
                    .build());

    // Judgeの[Justice]を保持するAttachment
    public static final Supplier<AttachmentType<Integer>> JUDGE_JUSTICE = ATTACHMENT_TYPES.register("judge_justice",
            () -> AttachmentType.builder(() -> 10) // 初期値は 10
                    .serialize(Codec.INT)
                    .copyOnDeath() // 死んでも引き継ぐ
                    .build());

    // Ultimate Kindの[Friend]を保持するAttachment
    public static final Supplier<AttachmentType<Integer>> KIND_FRIEND = ATTACHMENT_TYPES.register("kind_friend",
            () -> AttachmentType.builder(() -> 0) // 初期値は 0
                    .serialize(Codec.INT)
                    .copyOnDeath() // 死んでも引き継ぐ
                    .build());
}