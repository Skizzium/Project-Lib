package com.skizzium.projectlib.gui;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.entity.BossEntity;
import com.skizzium.projectlib.gui.minibar.ServerMinibar;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class PL_BossEvent {
    private final UUID id;
    protected Component name;
    protected float progress;
    protected Entity entity;
    protected List<ServerMinibar> minibars;
    protected SoundEvent bossMusic;
    @Nullable
    protected Integer customColor;
    @Nullable
    protected PL_BossBarColor color;
    protected PL_BossEvent.PL_BossBarOverlay overlay;
    protected boolean darkenScreen;
    protected boolean createWorldFog;
    
    protected boolean autoRender;
    protected boolean autoUpdate;
    protected boolean combinedHealth;

    public PL_BossEvent(UUID uuid, Component displayName, Entity entity, BossEventProperties properties) {
        this.id = uuid;
        this.name = displayName;
        this.progress = 1.0F;
        this.entity = entity;
        this.minibars = properties.minibars;
        this.bossMusic = properties.music;
        this.customColor = properties.customColor;
        this.color = properties.color;
        this.overlay = properties.overlay;
        this.darkenScreen = properties.darkenScreen;
        this.createWorldFog = properties.createWorldFog;
        this.autoRender = properties.autoRender;
        this.autoUpdate = properties.autoUpdate;
        this.combinedHealth = properties.combinedHealth;
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
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<ServerMinibar> getMinibars() {
        return this.minibars;
    }

    public boolean isMultibar() {
        return this.minibars.size() > 0;
    }

    public void addMinibar(ServerMinibar minibar) {
        this.minibars.add(minibar);
    }

    public void removeMinibar(ServerMinibar minibar) {
        this.minibars.remove(minibar);
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

    public void setDarkenScreen(boolean flag) {
        this.darkenScreen = flag;
    }

    public boolean shouldCreateWorldFog() {
        return this.createWorldFog;
    }

    public void setCreateWorldFog(boolean flag) {
        this.createWorldFog = flag;
    }

    public boolean shouldRenderAutomatically() {
        return this.autoRender;
    }

    public void setRenderAutomatically(boolean flag) {
        this.autoRender = flag;
    }

    public boolean shouldUpdateAutomatically() {
        return this.autoUpdate;
    }

    public void setUpdateAutomatically(boolean flag) {
        this.autoUpdate = flag;
    }

    public boolean hasCombinedHealth() {
        return this.combinedHealth;
    }

    public void setCombinedHealth(boolean flag) {
        this.combinedHealth = flag;
    }

    @SubscribeEvent
    public static void renderBars(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (entity instanceof BossEntity && ((BossEntity) entity).getBossBar().shouldRenderAutomatically() && event.getPlayer() instanceof ServerPlayer player) {
            ((BossEntity) entity).getBossBar().addPlayer(player);
        }
    }

    @SubscribeEvent
    public static void unrenderBars(PlayerEvent.StopTracking event) {
        Entity entity = event.getTarget();
        if (entity instanceof BossEntity && ((BossEntity) entity).getBossBar().shouldRenderAutomatically() && event.getPlayer() instanceof ServerPlayer player) {
            ((BossEntity) entity).getBossBar().removePlayer(player);
        }
    }

    @SubscribeEvent
    public static void updateBars(LivingEvent.LivingUpdateEvent event) {
        LivingEntity boss = event.getEntityLiving();
        if (boss instanceof BossEntity && ((BossEntity) boss).getBossBar().shouldUpdateAutomatically() && ((BossEntity) boss).getBossBar().getEntity() instanceof LivingEntity entity) {
            float progress = entity.getHealth() / entity.getMaxHealth();
            float miniProgress = 0;
            for (ServerMinibar minibar : ((BossEntity) boss).getBossBar().getMinibars()) {
                if (minibar.getEntity() instanceof LivingEntity minibarEntity && minibar.shouldUpdateAutomatically()) {
                    minibar.setProgress(minibarEntity.getHealth() / entity.getMaxHealth());
                }

                if (((BossEntity) boss).getBossBar().hasCombinedHealth()) {
                    miniProgress += minibar.getProgress();
                }
            }

            if (((BossEntity) boss).getBossBar().hasCombinedHealth()) {
                progress += miniProgress;
                progress /= ((BossEntity) boss).getBossBar().minibars.size();
            }
            
            ((BossEntity) boss).getBossBar().setProgress(progress);
        }
    }

    public static class BossEventProperties {
        boolean darkenScreen;
        boolean createWorldFog;
        
        boolean autoRender = true;
        boolean autoUpdate = true;
        boolean combinedHealth = false;
        
        List<ServerMinibar> minibars = new ArrayList<>();
        @Nullable
        SoundEvent music;
        @Nullable
        Integer customColor;
        @Nullable
        PL_BossBarColor color;
        PL_BossBarOverlay overlay = PL_BossBarOverlay.PROGRESS;

        public BossEventProperties addMinibar(ServerMinibar minibar) {
            this.minibars.add(minibar);
            return this;
        }
        
        public BossEventProperties darkenScreen(boolean flag) {
            this.darkenScreen = flag;
            return this;
        }

        public BossEventProperties createWorldFog(boolean flag) {
            this.createWorldFog = flag;
            return this;
        }
        
        public BossEventProperties renderAutomatically(boolean flag) {
            this.autoRender = flag;
            return this;
        }

        public BossEventProperties updateProgressAutomatically(boolean flag) {
            this.autoUpdate = flag;
            return this;
        }

        public BossEventProperties combinedHealth(boolean flag) {
            this.combinedHealth = flag;
            return this;
        }
        
        public BossEventProperties music(@Nullable SoundEvent music) {
            this.music = music;
            return this;
        }

        public BossEventProperties color(@Nullable Integer customColor) {
            this.customColor = customColor;
            return this;
        }

        public BossEventProperties color(@Nullable PL_BossBarColor color) {
            this.color = color;
            return this;
        }

        public BossEventProperties overlay(PL_BossBarOverlay overlay) {
            this.overlay = overlay;
            return this;
        }
    }
    
    public static enum PL_BossBarColor {
        RED("red", ChatFormatting.DARK_RED),
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
