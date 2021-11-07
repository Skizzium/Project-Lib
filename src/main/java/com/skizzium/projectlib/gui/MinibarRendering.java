package com.skizzium.projectlib.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;

public class MinibarRendering {
    private static int calculateMinibarWidth(boolean isStartOrEnd, PL_LerpingBossEvent parent) {
        return 182 / parent.getMinibars().size() - (isStartOrEnd ? 5 : 3);
    }
    
    // Old, currently doesn't work.
    public static void drawMinibar(PoseStack pose, LerpingMinibar bossEvent) {
        PL_LerpingBossEvent parent = null;

        for (LerpingBossEvent event : Minecraft.getInstance().gui.getBossOverlay().events.values()) {
            if (event instanceof PL_LerpingBossEvent && ((PL_LerpingBossEvent) event).getMinibars().contains(bossEvent)) {
                parent = (PL_LerpingBossEvent) event;
                break;
            }
        }

        if (parent != null) {
            float offset;
            if (parent.getMinibars().indexOf(bossEvent) == 0) {
                offset = 191.0F;
            }
            else if (parent.getMinibars().indexOf(bossEvent) == parent.getMinibars().size() - 1) {
                offset = 265.0F;
            }
            else {
                offset = 228.0F;
            }
            GuiComponent.blit(pose, parent.xPos + (36 * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, offset, (float)(bossEvent.getCustomColor().ordinal() * 2 * 2), 36, 2, 256, 300);

            int i = (int) (bossEvent.getProgress() * 37.0F);
            if (i > 0) {
                GuiComponent.blit(pose, parent.xPos + (36 * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, offset, (float)(bossEvent.getCustomColor().ordinal() * 2 * 2 + 2), i, 2, 256, 300);
            }
        }
    }

    /* 
     * Quick note on how I'm rendering the mini-bars:
     * I'm first rendering the beginning parts, then the fillers and then the ending parts.
     * The parts are ordered as follows: first bar beginning, default beginning, filler, default ending, last bar ending
     */
    public static void drawCustomColoredMinibar(PoseStack pose, int color, LerpingMinibar bossEvent) {
        PL_LerpingBossEvent parent = null;

        for (LerpingBossEvent event : Minecraft.getInstance().gui.getBossOverlay().events.values()) {
            if (event instanceof PL_LerpingBossEvent && ((PL_LerpingBossEvent) event).getMinibars().contains(bossEvent)) {
                parent = (PL_LerpingBossEvent) event;
                break;
            }
        }

        if (parent != null) {
            boolean isStart = parent.getMinibars().indexOf(bossEvent) == 0;
            boolean isEnd = parent.getMinibars().indexOf(bossEvent) == parent.getMinibars().size() - 1;
            boolean isStartOrEnd = isStart || isEnd;
            int width = calculateMinibarWidth(isStartOrEnd, parent);
            int averageWidth = 182 / parent.getMinibars().size();
            
            BarRendering.blit(pose, parent.xPos + (averageWidth * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, isStart ? 0.0F : 5.0F, 3.0F, isStart ? 4 : 2, 2, 5, 16, color);
            for (int i = 0; i <= width; i++) {
                BarRendering.blit(pose, parent.xPos + (averageWidth * parent.getMinibars().indexOf(bossEvent) + i + (isStart ? 5 : 3)), parent.yPos + 5, 0, 8.0F, 3.0F, 1, 2, 5, 16, color);
            }
            BarRendering.blit(pose, parent.xPos + (averageWidth * parent.getMinibars().indexOf(bossEvent) + (isEnd ? width + 2 : averageWidth)), parent.yPos + 5, 0, isEnd ? 12.0F : 10.0F, 3.0F, isEnd ? 4 : 1, 2, 5, 16, color);
            
//            int i = (int) (bossEvent.getProgress() * 37.0F);
//            if (i > 0) {
//                BarRendering.blit(pose, parent.xPos + (averageWidth * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, isStart ? 0.0F : 5.0F, 3.0F, Math.min(i, (isStart ? 4 : 2)), 2, 5, 16, color);
//            }
        }
    }
}
