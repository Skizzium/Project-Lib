package com.skizzium.projectlib.gui.minibar;

import com.skizzium.projectlib.gui.PL_BossEvent;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class Minibar {
    private final UUID id;
    protected float progress;
    protected Entity entity;
    @Nullable
    protected Integer customColor;
    @Nullable
    protected PL_BossEvent.PL_BossBarColor color;
    protected boolean autoUpdate;

    public Minibar(UUID uuid, Entity entity, MinibarProperties properties) {
        this.id = uuid;
        this.progress = 1.0F;
        this.entity = entity;
        this.customColor = properties.customColor;
        this.color = properties.color;
        this.autoUpdate = properties.autoUpdate;
    }

    public UUID getId() {
        return this.id;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float newProgress) {
        this.progress = newProgress;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Nullable
    public Integer getCustomColor() {
        return this.customColor;
    }

    public void setCustomColor(@Nullable Integer color) {
        this.customColor = color;
    }

    @Nullable
    public PL_BossEvent.PL_BossBarColor getColor() {
        return this.color;
    }

    public void setColor(@Nullable PL_BossEvent.PL_BossBarColor newColor) {
        this.color = newColor;
    }

    public boolean shouldUpdateAutomatically() {
        return this.autoUpdate;
    }

    public void setUpdateAutomatically(boolean flag) {
        this.autoUpdate = flag;
    }

    public static class MinibarProperties {
        boolean autoUpdate = true;
        @Nullable
        Integer customColor;
        @Nullable
        PL_BossEvent.PL_BossBarColor color;

        public MinibarProperties updateProgressAutomatically(boolean flag) {
            this.autoUpdate = flag;
            return this;
        }
        
        public MinibarProperties color(@Nullable Integer customColor) {
            this.customColor = customColor;
            return this;
        }

        public MinibarProperties color(@Nullable PL_BossEvent.PL_BossBarColor color) {
            this.color = color;
            return this;
        }
    }
}
