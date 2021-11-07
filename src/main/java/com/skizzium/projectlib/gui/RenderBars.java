package com.skizzium.projectlib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.gui.minibar.LerpingMinibar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderBars {
    private static final ResourceLocation VANILLA_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final ResourceLocation PL_BARS_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/pl_bars.png");
    private static final ResourceLocation TEMPLATE_BAR_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/template_bar.png");
    
    private static final ResourceLocation PL_MINIBARS_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/pl_minibars.png");
    private static final ResourceLocation TEMPLATE_MINIBAR_PARTS_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/template_minibar_parts.png");
    
    private static final Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public static void registerBossBarRendering(RenderGameOverlayEvent.BossInfo event) {
        event.setCanceled(true);
        int i = minecraft.getWindow().getGuiScaledWidth();
        int j = 12;

        for (LerpingBossEvent lerpingEvent : minecraft.gui.getBossOverlay().events.values()) {
            int k = i / 2 - 91;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            
            if (minecraft.gui.getBossOverlay().events.get(lerpingEvent.getId()) instanceof LerpingMinibar) {
                if (((LerpingMinibar) lerpingEvent).getCustomHexColor() != null) {
                    RenderSystem.setShaderTexture(0, TEMPLATE_MINIBAR_PARTS_LOCATION);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderBars.drawMinibar(event.getMatrixStack(), ((LerpingMinibar) lerpingEvent).getCustomHexColor(), (LerpingMinibar) lerpingEvent);
                }
                else {
                    if (((LerpingMinibar) lerpingEvent).getCustomColor() != null) {
                        RenderSystem.setShaderTexture(0, PL_MINIBARS_LOCATION);
                    }
                    else {
                        RenderSystem.setShaderTexture(0, TEMPLATE_MINIBAR_PARTS_LOCATION);
                    }
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderBars.drawMinibar(event.getMatrixStack(), ((LerpingMinibar) lerpingEvent).getCustomColor() != null ? null : 0xFFFFFF, (LerpingMinibar) lerpingEvent);
                }
                RenderSystem.disableBlend();
            }
            else if (minecraft.gui.getBossOverlay().events.get(lerpingEvent.getId()) instanceof PL_LerpingBossEvent) {
                if (((PL_LerpingBossEvent) lerpingEvent).getCustomHexColor() != null) {
                    RenderSystem.setShaderTexture(0, TEMPLATE_BAR_LOCATION);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    BarRendering.drawBar(event.getMatrixStack(), k, j, ((PL_LerpingBossEvent) lerpingEvent).getCustomHexColor(), (PL_LerpingBossEvent) lerpingEvent);
                }
                else {
                    if (((PL_LerpingBossEvent) lerpingEvent).getCustomColor() != null) {
                        RenderSystem.setShaderTexture(0, PL_BARS_LOCATION);
                    }
                    else {
                        RenderSystem.setShaderTexture(0, TEMPLATE_BAR_LOCATION);
                    }
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    BarRendering.drawBar(event.getMatrixStack(), k, j, null, (PL_LerpingBossEvent) lerpingEvent);
                }
                RenderSystem.disableBlend();
            }
            else {
                RenderSystem.setShaderTexture(0, VANILLA_BARS_LOCATION);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                BarRendering.drawBar(event.getMatrixStack(), k, j, lerpingEvent);
                RenderSystem.disableBlend();
            }

            if (!(lerpingEvent instanceof LerpingMinibar)) {
                Component component = lerpingEvent.getName();
                int l = minecraft.font.width(component);
                int i1 = i / 2 - l / 2;
                int j1 = j - 9;
                minecraft.font.drawShadow(event.getMatrixStack(), component, (float) i1, (float) j1, 16777215);

                j += event.getIncrement();
                if (j >= minecraft.getWindow().getGuiScaledHeight() / 3) {
                    break;
                }
            }
        } 
    }

    /*
     * Quick note on how I'm rendering the mini-bars:
     * I'm first rendering the beginning parts, then the fillers and then the ending parts.
     * The parts are ordered as follows: first bar beginning, default beginning, filler, default ending, last bar ending
     */
    public static void drawMinibar(PoseStack pose, Integer color, LerpingMinibar bossEvent) {
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
            int width = 182 / parent.getMinibars().size();
            int fillerWidth = width - (isStartOrEnd ? 5 : 3);

            BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, isStart ? 0.0F : 5.0F, color != null ? 0.0F : (float)(bossEvent.getCustomColor().ordinal() * 2 * 2), isStart ? 4 : 2, 2, color != null ? 5 : 56, 16, color);
            for (int i = 0; i <= fillerWidth; i++) {
                BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + i + (isStart ? 5 : 3)), parent.yPos + 5, 0, 8.0F, color != null ? 0.0F : (float)(bossEvent.getCustomColor().ordinal() * 2 * 2), 1, 2, color != null ? 5 : 56, 16, color);
            }
            BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + (isEnd ? fillerWidth + 2 : width)), parent.yPos + 5, 0, isEnd ? 12.0F : 10.0F, color != null ? 0.0F : (float)(bossEvent.getCustomColor().ordinal() * 2 * 2), isEnd ? 4 : 1, 2, color != null ? 5 : 56, 16, color);

            int progress = (int) (bossEvent.getProgress() * (fillerWidth + 1));
            if (progress > 0) {
                BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, isStart ? 0.0F : 5.0F, color != null ? 3.0F : (float)(bossEvent.getCustomColor().ordinal() * 2 * 2 + 2), Math.min(progress, (isStart ? 4 : 2)), 2, color != null ? 5 : 56, 16, color);
                for (int i = 0; i <= progress; i++) {
                    if (progress > (isStart ? 4 : 2))
                        BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + i + (isStart ? 5 : 3)), parent.yPos + 5, 0, 8.0F, color != null ? 3.0F : (float)(bossEvent.getCustomColor().ordinal() * 2 * 2 + 2), 1, 2, color != null ? 5 : 56, 16, color);
                }

                int endWidth = 0;
                if (progress >= fillerWidth) {
                    endWidth = fillerWidth - progress;
                }
                BarRendering.blit(pose, parent.xPos + (width * parent.getMinibars().indexOf(bossEvent) + (isEnd ? fillerWidth + 2 : width)), parent.yPos + 5, 0, isEnd ? 12.0F : 10.0F, color != null ? 3.0F : (float)(bossEvent.getCustomColor().ordinal() * 2 * 2 + 2), endWidth, 2, color != null ? 5 : 56, 16, color);
            }
        }
    }
}
