package com.skizzium.projectlib.gui;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.skizzium.projectlib.gui.minibar.ServerMinibar;
import com.skizzium.projectlib.init.PL_PacketRegistry;
import com.skizzium.projectlib.network.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.*;

public class PL_ServerBossEvent extends PL_BossEvent {
    private final Set<ServerPlayer> players = Sets.newHashSet();
    private final Set<ServerPlayer> unmodifiablePlayers;
    private boolean visible;

    public PL_ServerBossEvent(Entity entity, Component displayName, BossEventProperties properties) {
        super(Mth.createInsecureUUID(), displayName, entity, properties);
        this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
        this.visible = true;
    }

    public void setName(Component newName) {
        if (!Objects.equal(newName, this.name)) {
            super.setName(newName);
            this.broadcastUpdatePacket();
        }
    }

    public void setProgress(float progress) {
        if (progress != this.progress) {
            super.setProgress(progress);
            this.broadcastUpdatePacket();
        }
    }

    public void setEntity(Entity entity) {
        if (entity != this.entity) {
            super.setEntity(entity);
            this.broadcastUpdatePacket();
        }
    }

    public void addMinibar(ServerMinibar minibar) {
        if (!this.minibars.contains(minibar) && minibars.size() < 12) {
            super.addMinibar(minibar);
            this.broadcastMinibarPacket(minibar, BossEventPacket.OperationType.ADD);
        }
    }

    public void addMinibars(List<ServerMinibar> minibars) {
        for (ServerMinibar minibar : minibars) {
            this.addMinibar(minibar);
        }
    }
    
//    public void setMinibars(List<ServerMinibar> minibars) {
//        if (minibars != this.minibars) {
//            super.setMinibars(minibars);
//            this.broadcastUpdatePacket();
//        }
//    }

    public void setMinibars(List<ServerMinibar> minibars) {
        if (minibars != this.minibars) {
            this.clearMinibars();
            for (ServerMinibar minibar : minibars) {
                super.addMinibar(minibar);
            }
        }
    }
    
    public void clearMinibars() {
        for (ServerMinibar minibar : minibars) {
            this.removeMinibar(minibar);
        }
    }

    public void removeMinibar(ServerMinibar minibar) {
        if (this.minibars.contains(minibar)) {
            super.removeMinibar(minibar);
            this.broadcastMinibarPacket(minibar, BossEventPacket.OperationType.REMOVE);
        }
    }

    public void removeMinibars(List<ServerMinibar> minibars) {
        for (ServerMinibar minibar : minibars) {
            this.removeMinibar(minibar);
        }
    }
    
    public void setMusic(@Nullable SoundEvent music) {
        if (music == null) {
            super.setMusic(null);
            for (ServerPlayer player : this.players) {
                this.broadcastMusicPacket(false, BossMusicPacket.OperationType.STOP, player);
            }
        }
        else if (this.bossMusic == null) {
            super.setMusic(music);
            for (ServerPlayer player : this.players) {
                this.broadcastMusicPacket(false, BossMusicPacket.OperationType.START, player);
            }
        }
        else if (music != this.bossMusic) {
            super.setMusic(music);
            for (ServerPlayer player : this.players) {
                this.broadcastMusicPacket(true, BossMusicPacket.OperationType.STOP, player);
                this.broadcastMusicPacket(true, BossMusicPacket.OperationType.START, player);
            }
        }
    }

    public void setCustomColor(@Nullable Integer color) {
        if (!java.util.Objects.equals(color, this.customColor)) {
            super.setCustomColor(color);
            this.broadcastUpdatePacket();
        }
    }
    
    public void setColor(PL_BossBarColor color) {
        if (color != this.color) {
            super.setColor(color);
            this.broadcastUpdatePacket();
        }
    }

    public void setOverlay(PL_BossBarOverlay overlay) {
        if (overlay != this.overlay) {
            super.setOverlay(overlay);
            this.broadcastUpdatePacket();
        }
    }

    public void setDarkenScreen(boolean flag) {
        if (flag != this.darkenScreen) {
            super.setDarkenScreen(flag);
            this.broadcastUpdatePacket();
        }
    }

    public void setCreateWorldFog(boolean flag) {
        if (flag != this.createWorldFog) {
            super.setCreateWorldFog(flag);
            this.broadcastUpdatePacket();
        }
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            for (ServerMinibar minibar : this.minibars) {
                minibar.addPlayer(player);
            }
            
            this.broadcastAddRemovePacket(BossEventPacket.OperationType.ADD, player);
            if (this.bossMusic != null) {
                this.broadcastMusicPacket(false, BossMusicPacket.OperationType.START, player);
            }
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
            for (ServerMinibar minibar : this.minibars) {
                minibar.removePlayer(player);
            }
            
            this.broadcastMusicPacket(false, BossMusicPacket.OperationType.STOP, player);
            this.broadcastAddRemovePacket(BossEventPacket.OperationType.REMOVE, player);
        }
    }

    public void removeAllPlayers() {
        if (!this.players.isEmpty()) {
            for (ServerPlayer serverplayer : Lists.newArrayList(this.players)) {
                this.removePlayer(serverplayer);
            }
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean newValue) {
        if (newValue != this.visible) {
            this.visible = newValue;
            for (ServerPlayer player : this.players) {
                for (ServerMinibar minibar : this.minibars) {
                    minibar.setVisible(visible);
                }
                this.broadcastAddRemovePacket(newValue ? BossEventPacket.OperationType.ADD : BossEventPacket.OperationType.REMOVE, player);
            }
        }
    }

    public Collection<ServerPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }

    private void broadcastMusicPacket(boolean update, BossMusicPacket.OperationType operation, ServerPlayer player) {
        PL_PacketRegistry.INSTANCE.sendTo(new BossMusicPacket(this, operation, update), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }
    
    private void broadcastAddRemovePacket(BossEventPacket.OperationType operation, ServerPlayer player) {
        PL_PacketRegistry.INSTANCE.sendTo(new BossEventPacket(this, operation), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    private void broadcastUpdatePacket() {
        if (this.visible) {
            for (ServerPlayer player : this.players) {
                PL_PacketRegistry.INSTANCE.sendTo(new BossEventPacket(this, BossEventPacket.OperationType.UPDATE), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    private void broadcastMinibarPacket(ServerMinibar minibar, BossEventPacket.OperationType operation) {
        if (this.visible) {
            for (ServerPlayer player : this.players) {
                if (operation == BossEventPacket.OperationType.ADD) {
                    minibar.addPlayer(player);
                }
                else {
                    minibar.removePlayer(player);
                }
                PL_PacketRegistry.INSTANCE.sendTo(new MinibarPacket(minibar, operation), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                PL_PacketRegistry.INSTANCE.sendTo(new BossEventPacket(this, BossEventPacket.OperationType.UPDATE), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}