package com.skizzium.projectlib.gui;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PL_LerpingBossEvent extends LerpingBossEvent {
    private PL_BossEvent.PL_BossBarColor customColor;
    private PL_BossEvent.PL_BossBarOverlay customOverlay;

    public PL_LerpingBossEvent(UUID uuid, Component displayName, float progressPercentage, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay, boolean darkenScreen, boolean fog) {
        super(uuid, displayName, progressPercentage, BossBarColor.WHITE, BossBarOverlay.PROGRESS, darkenScreen, false, fog);
        this.customColor = color;
        this.customOverlay = overlay;
    }

    public PL_BossEvent.PL_BossBarColor getCustomColor() {
        return this.customColor;
    }

    public void setCustomColor(PL_BossEvent.PL_BossBarColor newColor) {
        this.customColor = newColor;
    }
    
    public PL_BossEvent.PL_BossBarOverlay getCustomOverlay() {
        return customOverlay;
    }
    
    public void setCustomOverlay(PL_BossEvent.PL_BossBarOverlay newOverlay) {
        this.customOverlay = newOverlay;
    }
}
