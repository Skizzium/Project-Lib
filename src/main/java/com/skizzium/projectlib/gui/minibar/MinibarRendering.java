package com.skizzium.projectlib.gui.minibar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skizzium.projectlib.gui.BarRendering;
import com.skizzium.projectlib.gui.PL_LerpingBossEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;

public class MinibarRendering {
    private static PL_LerpingBossEvent getParent(LerpingMinibar minibar) {
        for (LerpingBossEvent event : Minecraft.getInstance().gui.getBossOverlay().events.values()) {
            if (event instanceof PL_LerpingBossEvent && ((PL_LerpingBossEvent) event).getMinibars().contains(minibar)) {
                return (PL_LerpingBossEvent) event;
            }
        }
        return null;
    }
    
    public static void drawMinibar(PoseStack pose, LerpingMinibar bossEvent) {
        PL_LerpingBossEvent parent = getParent(bossEvent);
        
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
        PL_LerpingBossEvent parent = getParent(bossEvent);

        if (parent != null) {
            boolean isStart = parent.getMinibars().indexOf(bossEvent) == 0;
            boolean isEnd = parent.getMinibars().indexOf(bossEvent) == parent.getMinibars().size() - 1;
            boolean isStartOrEnd = isStart || isEnd;
            int width = 182 / parent.getMinibars().size();
            int fillerWidth = width - (isStartOrEnd ? 5 : 3);
            
            BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, isStart ? 0.0F : 5.0F, 0.0F, isStart ? 4 : 2, 2, 5, 16, color);
            for (int i = 0; i <= fillerWidth; i++) {
                BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + i + (isStart ? 5 : 3)), parent.yPos + 5, 0, 8.0F, 0.0F, 1, 2, 5, 16, color);
            }
            BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + (isEnd ? fillerWidth + 2 : width)), parent.yPos + 5, 0, isEnd ? 12.0F : 10.0F, 0.0F, isEnd ? 4 : 1, 2, 5, 16, color);
            
            int progress = (int) (bossEvent.getProgress() * (fillerWidth + 1));
            if (progress > 0) {
                BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, isStart ? 0.0F : 5.0F, 3.0F, Math.min(progress, (isStart ? 4 : 2)), 2, 5, 16, color);
                for (int i = 0; i <= progress; i++) {
                    if (progress > (isStart ? 4 : 2))
                        BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + i + (isStart ? 5 : 3)), parent.yPos + 5, 0, 8.0F, 3.0F, 1, 2, 5, 16, color);
                }
                
                int endWidth = 0;
                if (progress >= fillerWidth) {
                    endWidth = fillerWidth - progress;
                }
                BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + (isEnd ? fillerWidth + 2 : width)), parent.yPos + 5, 0, isEnd ? 12.0F : 10.0F, 3.0F, endWidth, 2, 5, 16, color);
            }
        }
    }
}
