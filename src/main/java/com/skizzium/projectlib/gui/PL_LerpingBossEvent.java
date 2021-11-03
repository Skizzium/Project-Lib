package com.skizzium.projectlib.gui;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PL_LerpingBossEvent extends LerpingBossEvent {
    public SoundInstance music;
    private PL_BossEvent.PL_BossBarColor customColor;
    private PL_BossEvent.PL_BossBarOverlay customOverlay;
    private Entity entity;

    public PL_LerpingBossEvent(UUID uuid, Component displayName, float progressPercentage, Entity entity, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay, boolean darkenScreen, boolean fog) {
        super(uuid, displayName, progressPercentage, BossBarColor.WHITE, BossBarOverlay.PROGRESS, darkenScreen, false, fog);
        this.customColor = color;
        this.customOverlay = overlay;
        this.entity = entity;
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

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
