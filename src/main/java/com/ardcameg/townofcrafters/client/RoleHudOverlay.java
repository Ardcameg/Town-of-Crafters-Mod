package com.ardcameg.townofcrafters.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class RoleHudOverlay {

    // ============================= カスタマイズ =============================
    public static final float BASE_SCALE = 2.0f; // デフォルトの文字の大きさ
    public static final float MARGIN_RATIO = 0.8f; // 余白の広さ

    public static final int ANIMATION_TICKS = 5; // アニメーションにかかる時間
    public static final float START_SCALE = 5.0f; // 出現時の「相対的な」大きさ
    public static final float START_ROTATION = 270.0f; // 回転する角度(60分法)

    public static String currentText = "";
    public static int currentColor = 0xFFFFFF; // 初期色
    public static int popTimer = 0;

    public static void render(GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker deltaTracker) {
        if (currentText.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int screenHeight = guiGraphics.guiHeight();

        // ベースの大きさにした時の、実際の文字の高さ
        float actualTextHeight = font.lineHeight * BASE_SCALE;

        // 余白を文字の高さに比例させて計算する
        float margin = actualTextHeight * MARGIN_RATIO;

        // 描画の基準座標
        float x = margin;
        float y = screenHeight - actualTextHeight - margin;

        guiGraphics.pose().pushPose();

        // 基準位置へ移動し、ベースとなる大きさに画面全体を拡大
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(BASE_SCALE, BASE_SCALE, 1.0f);

        if (popTimer == 0) {
            guiGraphics.drawString(font, currentText, 0, 0, currentColor, true);
        } else {
            float progress = (float) popTimer / ANIMATION_TICKS;
            float ease = progress * progress * progress; // 最初は勢いよく、最後はゆっくり

            float animScale = 1.0f + (START_SCALE - 1.0f) * ease;
            float angle = START_ROTATION * ease;

            int textWidth = font.width(currentText);
            int textHeight = font.lineHeight;

            guiGraphics.pose().pushPose();

            // 文字の「中心座標」を基準にするため移動
            guiGraphics.pose().translate(textWidth / 2.0f, textHeight / 2.0f, 0);
            // 回転
            guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angle));
            // 拡大縮小
            guiGraphics.pose().scale(animScale, animScale, 1.0f);
            // 中心座標を元に戻す
            guiGraphics.pose().translate(-(textWidth / 2.0f), -(textHeight / 2.0f), 0);

            // アニメーション中の文字を描画
            guiGraphics.drawString(font, currentText, 0, 0, currentColor, true);

            guiGraphics.pose().popPose();
        }

        guiGraphics.pose().popPose();
    }
}