package com.skizzium.projectlib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.skizzium.projectlib.ProjectLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderCustomBossBar {
    private static final ResourceLocation VANILLA_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
    private static final ResourceLocation PL_BARS_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/pl_bars.png");
    private static final ResourceLocation TEMPLATE_BAR_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/template_bar.png");
    private static final ResourceLocation TEMPLATE_MINIBAR_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/template_minibar.png");
    
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
                    RenderSystem.setShaderTexture(0, TEMPLATE_MINIBAR_LOCATION);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    drawCustomColoredMinibar(event.getMatrixStack(), ((LerpingMinibar) lerpingEvent).getCustomHexColor(), (LerpingMinibar) lerpingEvent);
                }
                else {
                    if (((LerpingMinibar) lerpingEvent).getCustomColor() != null) {
                        RenderSystem.setShaderTexture(0, PL_BARS_LOCATION);
                    }
                    else {
                        RenderSystem.setShaderTexture(0, TEMPLATE_MINIBAR_LOCATION);
                    }
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    drawMinibar(event.getMatrixStack(), (LerpingMinibar) lerpingEvent);
                }
                RenderSystem.disableBlend();
            }
            else if (minecraft.gui.getBossOverlay().events.get(lerpingEvent.getId()) instanceof PL_LerpingBossEvent) {
                if (((PL_LerpingBossEvent) lerpingEvent).getCustomHexColor() != null) {
                    RenderSystem.setShaderTexture(0, TEMPLATE_BAR_LOCATION);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    BarRendering.drawCustomColoredBar(event.getMatrixStack(), k, j, ((PL_LerpingBossEvent) lerpingEvent).getCustomHexColor(), (PL_LerpingBossEvent) lerpingEvent);
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
                    BarRendering.drawBar(event.getMatrixStack(), k, j, (PL_LerpingBossEvent) lerpingEvent);
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

    public static void drawMinibar(PoseStack pose, LerpingMinibar bossEvent) {
        PL_LerpingBossEvent parent = null;

        for (LerpingBossEvent event : minecraft.gui.getBossOverlay().events.values()) {
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

    public static void drawCustomColoredMinibar(PoseStack pose, int color, LerpingMinibar bossEvent) {
        PL_LerpingBossEvent parent = null;
        
        for (LerpingBossEvent event : minecraft.gui.getBossOverlay().events.values()) {
            if (event instanceof PL_LerpingBossEvent && ((PL_LerpingBossEvent) event).getMinibars().contains(bossEvent)) {
                parent = (PL_LerpingBossEvent) event;
                break;
            }
        }
        
        if (parent != null) {
            float offset;
            if (parent.getMinibars().indexOf(bossEvent) == 0) {
                offset = 0.0F;
            }
            else if (parent.getMinibars().indexOf(bossEvent) == parent.getMinibars().size() - 1) {
                offset = 74.0F;
            }
            else {
                offset = 37.0F;
            }
            BarRendering.blit(pose, parent.xPos + (36 * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, offset, 0.0F, 36, 2, 5, 110, color);

            int i = (int) (bossEvent.getProgress() * 37.0F);
            if (i > 0) {
                BarRendering.blit(pose, parent.xPos + (36 * parent.getMinibars().indexOf(bossEvent) + 1), parent.yPos + 5, 0, offset, 3.0F, i, 2, 5, 111, color);
            }
        }
    }
}
