package com.skizzium.projectlib.gui;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.skizzium.projectlib.init.PL_PacketRegistry;
import com.skizzium.projectlib.network.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class PL_ServerBossEvent extends PL_BossEvent {
    private final Set<ServerPlayer> players = Sets.newHashSet();
    private final Set<ServerPlayer> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
    private boolean visible = true;

    public PL_ServerBossEvent(Component displayName, PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay) {
        super(Mth.createInsecureUUID(), displayName, color, overlay);
    }

    private void broadcast(Object packet) {
        if (this.visible) {
            for(ServerPlayer player : this.players) {
                PL_PacketRegistry.INSTANCE.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
    
    public void setName(Component newName) {
        if (!Objects.equal(newName, this.name)) {
            super.setName(newName);
            this.broadcast(new PL_UpdateNameBossEventPacket(this));
        }
    }

    public void setColor(PL_BossBarColor color) {
        if (color != this.color) {
            super.setColor(color);
            this.broadcast(new PL_UpdateStyleBossEventPacket(this));
        }
    }

    public void setOverlay(PL_BossEvent.PL_BossBarOverlay overlay) {
        if (overlay != this.overlay) {
            super.setOverlay(overlay);
            this.broadcast(new PL_UpdateStyleBossEventPacket(this));
        }
    }

    public PL_BossEvent setDarkenScreen(boolean flag) {
        if (flag != this.darkenScreen) {
            super.setDarkenScreen(flag);
            this.broadcast(new PL_UpdatePropertiesBossEventPacket(this));
        }
        return this;
    }

    public PL_BossEvent setCreateWorldFog(boolean flag) {
        if (flag != this.createWorldFog) {
            super.setCreateWorldFog(flag);
            this.broadcast(new PL_UpdatePropertiesBossEventPacket(this));
        }
        return this;
    }
    
    public void setProgress(float f) {
        if (f != this.progress) {
            super.setProgress(f);
            this.broadcast(new PL_UpdateProgressBossEventPacket(this));
        }
    }
    
    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            PL_PacketRegistry.INSTANCE.sendTo(new PL_AddBossEventPacket(this), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
            PL_PacketRegistry.INSTANCE.sendTo(new PL_RemoveBossEventPacket(this), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public void removeAllPlayers() {
        if (!this.players.isEmpty()) {
            for(ServerPlayer serverplayer : Lists.newArrayList(this.players)) {
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

            for(ServerPlayer player : this.players) {
                if (newValue) 
                    PL_PacketRegistry.INSTANCE.sendTo(new PL_AddBossEventPacket(this), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                else
                    PL_PacketRegistry.INSTANCE.sendTo(new PL_RemoveBossEventPacket(this), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }

    public Collection<ServerPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }
}