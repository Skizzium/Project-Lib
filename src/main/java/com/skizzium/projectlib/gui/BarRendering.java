package com.skizzium.projectlib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.skizzium.projectlib.ProjectLib;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.BossEvent;

public class BarRendering {
    private static final ResourceLocation PL_BARS_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/pl_bars.png");
    private static final ResourceLocation TEMPLATE_BAR_LOCATION = new ResourceLocation(ProjectLib.MOD_ID, "textures/gui/template_bar.png");
    
    public static void drawBar(PoseStack pose, int xPos, int yPos, BossEvent bossEvent) {
        GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getColor().ordinal() * 5 * 2), 182, 5, 256, 256);
        if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2), 182, 5, 256, 256);
        }

        int i = (int)(bossEvent.getProgress() * 183.0F);
        if (i > 0) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getColor().ordinal() * 5 * 2 + 5), i, 5, 256, 256);
            if (bossEvent.getOverlay() != BossEvent.BossBarOverlay.PROGRESS) {
                GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(80 + (bossEvent.getOverlay().ordinal() - 1) * 5 * 2 + 5), i, 5, 256, 256);
            }
        }
    }

    public static void drawBar(PoseStack pose, int xPos, int yPos, PL_LerpingBossEvent bossEvent) {
        bossEvent.xPos = xPos;
        bossEvent.yPos = yPos;
        
        GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getCustomColor().ordinal() * 5 * 2), 182, 5, 256, 300);
        if (bossEvent.getCustomOverlay() != PL_BossEvent.PL_BossBarOverlay.PROGRESS) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2), 182, 5, 256, 300);
        }

        int i = (int)(bossEvent.getProgress() * 183.0F);
        if (i > 0) {
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(bossEvent.getCustomColor().ordinal() * 5 * 2 + 5), i, 5, 256, 300);
            if (bossEvent.getCustomOverlay() != PL_BossEvent.PL_BossBarOverlay.PROGRESS) {
                GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2 + 5), i, 5, 256, 300);
            }
        }
    }

    public static void blit(PoseStack pMatrixStack, int pX, int pY, int pBlitOffset, float pUOffset, float pVOffset, int pUWidth, int pVHeight, int pTextureHeight, int pTextureWidth, int color) {
        innerBlit(pMatrixStack, pX, pX + pUWidth, pY, pY + pVHeight, pBlitOffset, pUWidth, pVHeight, pUOffset, pVOffset, pTextureWidth, pTextureHeight, color);
    }

    private static void innerBlit(PoseStack pMatrixStack, int pX1, int pX2, int pY1, int pY2, int pBlitOffset, int pUWidth, int pVHeight, float pUOffset, float pVOffset, int pTextureWidth, int pTextureHeight, int color) {
        innerBlit(pMatrixStack.last().pose(), pX1, pX2, pY1, pY2, pBlitOffset, (pUOffset + 0.0F) / (float)pTextureWidth, (pUOffset + (float)pUWidth) / (float)pTextureWidth, (pVOffset + 0.0F) / (float)pTextureHeight, (pVOffset + (float)pVHeight) / (float)pTextureHeight, color);
    }

    private static void innerBlit(Matrix4f pMatrix, int pX1, int pX2, int pY1, int pY2, int pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV, int color) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(pMatrix, (float)pX1, (float)pY2, (float)pBlitOffset).color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color), 255).uv(pMinU, pMaxV).endVertex();
        bufferbuilder.vertex(pMatrix, (float)pX2, (float)pY2, (float)pBlitOffset).color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color), 255).uv(pMaxU, pMaxV).endVertex();
        bufferbuilder.vertex(pMatrix, (float)pX2, (float)pY1, (float)pBlitOffset).color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color), 255).uv(pMaxU, pMinV).endVertex();
        bufferbuilder.vertex(pMatrix, (float)pX1, (float)pY1, (float)pBlitOffset).color(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color), 255).uv(pMinU, pMinV).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }

    public static void drawCustomColoredBar(PoseStack pose, int xPos, int yPos, int color, PL_LerpingBossEvent bossEvent) {
        bossEvent.xPos = xPos;
        bossEvent.yPos = yPos;
        
        blit(pose, xPos, yPos, 0, 0.0F, 0.0F, 182, 5, 10, 182, color);
        if (bossEvent.getCustomOverlay() != PL_BossEvent.PL_BossBarOverlay.PROGRESS) {
            RenderSystem.setShaderTexture(0, PL_BARS_LOCATION);
            GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2), 182, 5, 256, 300);
            RenderSystem.setShaderTexture(0, TEMPLATE_BAR_LOCATION);
        }

        int i = (int)(bossEvent.getProgress() * 183.0F);
        if (i > 0) {
            blit(pose, xPos, yPos, 0, 0.0F, 5.0F, i, 5, 10, 182, color);
            if (bossEvent.getCustomOverlay() != PL_BossEvent.PL_BossBarOverlay.PROGRESS) {
                RenderSystem.setShaderTexture(0, PL_BARS_LOCATION);
                GuiComponent.blit(pose, xPos, yPos, 0, 0.0F, (float)(150 + (bossEvent.getCustomOverlay().ordinal() - 1) * 5 * 2 + 5), i, 5, 256, 300);
            }
        }
    }
}
