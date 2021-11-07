package com.skizzium.projectlib.gui.minibar;

import com.skizzium.projectlib.gui.PL_BossEvent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class LerpingMinibar extends LerpingBossEvent {
    private Entity entity;
    private Integer customHexColor;
    private PL_BossEvent.PL_BossBarColor customColor;

    public LerpingMinibar(UUID uuid, float progressPercentage, Entity entity, @Nullable Integer customColor, PL_BossEvent.PL_BossBarColor color) {
        super(uuid, new TextComponent("Minibar"), progressPercentage, BossBarColor.WHITE, BossBarOverlay.PROGRESS, false, false, false);
        this.customHexColor = customColor;
        this.customColor = color;
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
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
}
