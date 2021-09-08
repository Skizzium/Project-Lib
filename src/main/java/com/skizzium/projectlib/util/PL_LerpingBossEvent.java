package com.skizzium.projectlib.util;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PL_LerpingBossEvent extends LerpingBossEvent {
    private PL_BossEvent.PL_BossBarColor customColor;

    public PL_LerpingBossEvent(UUID uuid, Component displayName, float progressPercentage, PL_BossEvent.PL_BossBarColor color, BossBarOverlay overlay, boolean darkenScreen, boolean fog) {
        super(uuid, displayName, progressPercentage, BossBarColor.WHITE, overlay, darkenScreen, false, fog);
        this.customColor = color;
    }

    public PL_BossEvent.PL_BossBarColor getCustomColor() {
        return this.customColor;
    }

    public void setCustomColor(PL_BossEvent.PL_BossBarColor newColor) {
        this.customColor = newColor;
    }
}
