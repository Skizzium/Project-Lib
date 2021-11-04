package com.skizzium.projectlib.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class PL_BossEvent {
    private final UUID id;
    protected Component name;
    protected float progress;
    protected Entity entity;
    protected SoundEvent bossMusic;
    @Nullable
    protected Integer customColor;
    @Nullable
    protected PL_BossBarColor color;
    protected PL_BossEvent.PL_BossBarOverlay overlay;
    protected boolean darkenScreen;
    protected boolean createWorldFog;

    public PL_BossEvent(UUID uuid, Component displayName, Entity entity, SoundEvent music, int color, PL_BossEvent.PL_BossBarOverlay choosenOverlay) {
        this.id = uuid;
        this.name = displayName;
        this.progress = 1.0F;
        this.entity = entity;
        this.bossMusic = music;
        this.customColor = color;
        this.overlay = choosenOverlay;
    }
    
    public PL_BossEvent(UUID uuid, Component displayName, Entity entity, @Nullable SoundEvent music, PL_BossBarColor assignedColor, PL_BossEvent.PL_BossBarOverlay choosenOverlay) {
        this.id = uuid;
        this.name = displayName;
        this.progress = 1.0F;
        this.entity = entity;
        this.bossMusic = music;
        this.color = assignedColor;
        this.overlay = choosenOverlay;
    }

    public UUID getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public void setName(Component newName) {
        this.name = newName;
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

    @Nullable
    public SoundEvent getMusic() {
        return this.bossMusic;
    }

    public void setMusic(@Nullable SoundEvent music) {
        this.bossMusic = music;
    }

    @Nullable
    public Integer getCustomColor() {
        return this.customColor;
    }

    public void setCustomColor(@Nullable Integer color) {
        this.customColor = color;
    }

    @Nullable
    public PL_BossBarColor getColor() {
        return this.color;
    }

    public void setColor(@Nullable PL_BossBarColor newColor) {
        this.color = newColor;
    }

    public PL_BossEvent.PL_BossBarOverlay getOverlay() {
        return this.overlay;
    }

    public void setOverlay(PL_BossEvent.PL_BossBarOverlay newOverlay) {
        this.overlay = newOverlay;
    }

    public boolean shouldDarkenScreen() {
        return this.darkenScreen;
    }

    public PL_BossEvent setDarkenScreen(boolean newValue) {
        this.darkenScreen = newValue;
        return this;
    }

    public PL_BossEvent setCreateWorldFog(boolean newValue) {
        this.createWorldFog = newValue;
        return this;
    }

    public boolean shouldCreateWorldFog() {
        return this.createWorldFog;
    }

    public static enum PL_BossBarColor {
        RED("red",ChatFormatting.DARK_RED),
        ORANGE("orange", ChatFormatting.GOLD),
        GOLD("gold", ChatFormatting.GOLD),
        YELLOW("yellow", ChatFormatting.YELLOW),
        LIME("lime", ChatFormatting.GREEN),
        GREEN("green", ChatFormatting.DARK_GREEN),
        CYAN("cyan",ChatFormatting.DARK_AQUA),
        AQUA("aqua", ChatFormatting.AQUA),
        BLUE("blue", ChatFormatting.BLUE),
        DARK_BLUE("dark_blue", ChatFormatting.DARK_BLUE),
        PURPLE("purple", ChatFormatting.DARK_PURPLE),
        PINK("pink", ChatFormatting.LIGHT_PURPLE),
        WHITE("white", ChatFormatting.WHITE),
        BLACK("black", ChatFormatting.BLACK);

        private final String name;
        private final ChatFormatting formatting;

        private PL_BossBarColor(String givenName, ChatFormatting color) {
            this.name = givenName;
            this.formatting = color;
        }

        public ChatFormatting getFormatting() {
            return this.formatting;
        }

        public String getName() {
            return this.name;
        }

        public static PL_BossBarColor byName(String nameToSearch) {
            for(PL_BossBarColor colorEnum : values()) {
                if (colorEnum.name.equals(nameToSearch)) {
                    return colorEnum;
                }
            }
            return WHITE;
        }
    }

    public static enum PL_BossBarOverlay {
        PROGRESS("progress"),
        NOTCHED_5("notched_5"),
        NOTCHED_6("notched_6"),
        NOTCHED_10("notched_10"),
        NOTCHED_12("notched_12"),
        NOTCHED_20("notched_20");

        private final String name;

        private PL_BossBarOverlay(String givenName) {
            this.name = givenName;
        }

        public String getName() {
            return this.name;
        }

        public static PL_BossEvent.PL_BossBarOverlay byName(String nameToSearch) {
            for(PL_BossEvent.PL_BossBarOverlay overlayEnum : values()) {
                if (overlayEnum.name.equals(nameToSearch)) {
                    return overlayEnum;
                }
            }

            return PROGRESS;
        }
    }
}
