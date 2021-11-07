package com.skizzium.projectlib.gui;

import com.skizzium.projectlib.gui.minibar.LerpingMinibar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PL_LerpingBossEvent extends LerpingBossEvent {
    private Entity entity;
    private List<LerpingMinibar> minibars = new ArrayList<>();
    public SoundInstance music;
    private Integer customHexColor;
    private PL_BossEvent.PL_BossBarColor customColor;
    private PL_BossEvent.PL_BossBarOverlay customOverlay;
    
    public int xPos;
    public int yPos;

    public PL_LerpingBossEvent(UUID uuid, Component displayName, float progressPercentage, Entity entity, List<UUID> minibars, @Nullable Integer customColor, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay, boolean darkenScreen, boolean fog) {
        super(uuid, displayName, progressPercentage, BossBarColor.WHITE, BossBarOverlay.PROGRESS, darkenScreen, false, fog);
        this.entity = entity;
        for (UUID id : minibars) {
            this.minibars.add((LerpingMinibar) Minecraft.getInstance().gui.getBossOverlay().events.get(id));
        }
        this.customHexColor = customColor;
        this.customColor = color;
        this.customOverlay = overlay;
    }

    public Entity getEntity() {
        return this.entity;
    }
    
    public List<LerpingMinibar> getMinibars() {
        return this.minibars;
    }

    public void setMinibars(List<UUID> minibars) {
        this.minibars.clear();
        for (UUID id : minibars) {
            this.minibars.add((LerpingMinibar) Minecraft.getInstance().gui.getBossOverlay().events.get(id));
        }
    }
    
    @Nullable
    public Integer getCustomHexColor() {
        return this.customHexColor;
    }

    public void setCustomHexColor(@Nullable Integer color) {
        this.customHexColor = color;
    }

    @Nullable
    public PL_BossEvent.PL_BossBarColor getCustomColor() {
        return this.customColor;
    }

    public void setCustomColor(@Nullable PL_BossEvent.PL_BossBarColor newColor) {
        this.customColor = newColor;
    }
    
    public PL_BossEvent.PL_BossBarOverlay getCustomOverlay() {
        return customOverlay;
    }
    
    public void setCustomOverlay(PL_BossEvent.PL_BossBarOverlay newOverlay) {
        this.customOverlay = newOverlay;
    }
}
