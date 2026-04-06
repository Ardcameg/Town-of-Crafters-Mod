package com.ardcameg.townofcrafters;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

// 役職の種類を定義する列挙型
public enum Role {
    NONE("None", Items.BARRIER, 0xFFFFFF,
            "Blade Meets Fate.",
            List.of(),
            ""),
    MINER("Miner", Items.GOLDEN_PICKAXE, 0xBC763C,
            "Strike While It’s Ore.",
            List.of(
                    new RoleStat("Block Break Speed", "+400%", true),
                    new RoleStat("Attack Damage", "-20%", false),
                    new RoleStat("Attack Speed", "-75%", false)
            ),
            "> [Even Stones are Ores] Earn experience points when you mine stone or deepslate."),
    WARRIOR("Warrior", Items.IRON_SWORD, 0xAF0000,
            "Blade Meets Fate.",
            List.of(
                    new RoleStat("Attack Damage", "+50%", true),
                    new RoleStat("Attack Speed", "+50%", true),
                    new RoleStat("Block Break Speed", "-50%", false)
            ),
            "> [Morale] Gain various effects based on the number of mobs you've killed."),
    TANK("Tank", Items.SHIELD, 0x8F8F8F,
            "Stand Like Stone.",
            List.of(
                    new RoleStat("Max Health", "+100%", true),
                    new RoleStat("Knockback Resistance", "+999%", true),
                    new RoleStat("Damage Taken", "-40%", true),
                    new RoleStat("Attack Speed", "-25%", false),
                    new RoleStat("Movement Speed", "-20%", false),
                    new RoleStat("Gravity", "+25%", false)
            ),
            """
                > [Self-destruct] When killed by a monster, deals 100 damage to all monsters within a 5-block radius.
                > The initial value of [Self-destruct] is 0.
                """),
    WITCH("Witch", Items.POTION, 0xFF00AA,
            "Brew Your Own Success.",
            List.of(),
            """
                > [Brewing] One beneficial potion effect and one harmful potion effect are randomly applied.
                        When you respawn, each effect is randomly changed.
                """),
    GAMBLER("Gambler", Items.GOLD_NUGGET, 0xFFEE00,
            "Raise!",
            List.of(),
            """
                > [All-in!] When you attack, you may instantly defeat the opponent.
                    Otherwise, you take damage equal to the target’s current health (capped at 75% of your max health).
                """),
    BUILDER("Builder", Items.OAK_PLANKS, 0x70340C,
            "Shape the World.",
            List.of(
                    new RoleStat("Fall Damage", "-999%", true),
                    new RoleStat("Step Height", "+100%", true),
                    new RoleStat("Block Interaction Range", "+200%", true),
                    new RoleStat("Attack Damage", "-999%", false),
                    new RoleStat("Max Health", "-50%", false)),
            ""),
    JUDGE("Judge", Items.WRITTEN_BOOK, 0xCC00FF,
            "That's Wrong!",
            List.of(
                    new RoleStat("Attack Damage", "+250%", true),
                    new RoleStat("Attack Speed", "+50%", true)),
            """
                > [Self-blame] When you kill a monster, increase [Justice] by 1. When you kill a non-monster,
                        deal damage to yourself equal to your [Justice] value, and reset [Justice].
                > The initial value of [Justice] is 10, and from the second time onward, it becomes half of the max health.
                """),
    ULTIMATE_KIND("Ultimate Kind", Items.POPPY, 0xFFB6C1,
            "Too much kindness invites death.",
            List.of(),
            """
                > [Kind-Heart]  Absorbs damage dealt to all friendly mobs within range, increasing [Friend] by 1 per instance.
                    The range is 1 block per [Friend] (minimum: 5). When attacked by an enemy mob, or when attacking a friendly mob,
                        the attack is nullified, and damage equal to the value of [Friend] is dealt, resetting [Friend].This damage is not absorbed.
                > The initial value of [Friend] is 0.
                """),
    GOLEM("Golem", Items.CONDUIT, 0x006A6C,
            "Core of Fragility",
            List.of(
                    new RoleStat("Damage Taken", "-999%", true),
                    new RoleStat("Knockback Resistance", "+999%", true)),
            "> [Achilles' Heel] If you take even 1 point of fall damage, magic damage, or drowning damage, you will die.");

    private final String displayName; // 表示名
    private final Item displayItem; // シンボル
    private final int color; // 色
    private final String flavorText; // フレーバーテキスト
    private final List<RoleStat> stats; // ステータス変化
    private final String skillDescription; // パッシブスキルの説明

    Role(String displayName, Item displayItem, int color, String flavorText, List<RoleStat> stats, String skillDescription) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.color = color;
        this.flavorText = flavorText;
        this.stats = stats;
        this.skillDescription = skillDescription;
    }

    public String getDisplayName() { return displayName; }
    public Item getDisplayItem() { return displayItem; }
    public int getColor() { return color; }

    public void sendRoleInfoToPlayer(ServerPlayer player) {
        // ヘッダー
        player.sendSystemMessage(Component.literal("======= Role =======").withStyle(ChatFormatting.GREEN));

        // タイトルとフレーバーテキスト
        // Component を .append() することで、1行の中で色を変えられます
        player.sendSystemMessage(Component.literal(this.ordinal() + ". ")
                .append(Component.literal(this.displayName).withColor(this.color))
                .append(Component.literal(" - " + this.flavorText).withStyle(ChatFormatting.WHITE))
        );

        // ステータス変化 (バフなら緑の+、デバフなら赤の- を自動で付ける)
        for (RoleStat stat : this.stats) {
            if (stat.isPositive()) {
                player.sendSystemMessage(Component.literal("+ " + stat.name() + ": " + stat.value()).withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.literal("- " + stat.name() + ": " + stat.value()).withStyle(ChatFormatting.RED));
            }
        }

        // スキルの説明
        if (!this.skillDescription.isEmpty()) {
            // "\n" で改行されているテキストを分割して、1行ずつ送信する
            for (String line : this.skillDescription.split("\n")) {
                player.sendSystemMessage(Component.literal(line).withColor(this.color));
            }
        }

        // 5. フッター
        player.sendSystemMessage(Component.literal("======================").withStyle(ChatFormatting.GREEN));
    }
}