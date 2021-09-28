package com.skizzium.projectlib.gui;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.skizzium.projectlib.network.PL_BossEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class PL_ServerBossEvent extends PL_BossEvent {
    private final Set<ServerPlayer> players = Sets.newHashSet();
    private final Set<ServerPlayer> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
    private boolean visible = true;

    public PL_ServerBossEvent(Component displayName, PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay) {
        super(Mth.createInsecureUUID(), displayName, color, overlay);
    }

    public void setProgress(float newProgress) {
        if (newProgress != this.progress) {
            super.setProgress(newProgress);
            this.broadcast(PL_BossEventPacket::createUpdateProgressPacket);
        }
    }

    public void setColor(PL_BossBarColor newColor) {
        if (newColor != this.color) {
            super.setColor(newColor);
            this.broadcast(PL_BossEventPacket::createUpdateStylePacket);
        }
    }

    public void setOverlay(PL_BossEvent.PL_BossBarOverlay newOverlay) {
        if (newOverlay != this.overlay) {
            super.setOverlay(newOverlay);
            this.broadcast(PL_BossEventPacket::createUpdateStylePacket);
        }
    }

    public PL_BossEvent setDarkenScreen(boolean newValue) {
        if (newValue != this.darkenScreen) {
            super.setDarkenScreen(newValue);
            this.broadcast(PL_BossEventPacket::createUpdatePropertiesPacket);
        }
        return this;
    }

    public PL_BossEvent setCreateWorldFog(boolean newValue) {
        if (newValue != this.createWorldFog) {
            super.setCreateWorldFog(newValue);
            this.broadcast(PL_BossEventPacket::createUpdatePropertiesPacket);
        }
        return this;
    }

    public void setName(Component newName) {
        if (!Objects.equal(newName, this.name)) {
            super.setName(newName);
            this.broadcast(PL_BossEventPacket::createUpdateNamePacket);
        }
    }

    private void broadcast(Function<PL_BossEvent, PL_BossEventPacket> function) {
        if (this.visible) {
            PL_BossEventPacket bossEventPacket = function.apply(this);

            for(ServerPlayer serverplayer : this.players) {
                serverplayer.connection.send(bossEventPacket);
            }
        }
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            player.connection.send(PL_BossEventPacket.createAddPacket(this));
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
            player.connection.send(PL_BossEventPacket.createRemovePacket(this.getId()));
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

            for(ServerPlayer serverplayer : this.players) {
                serverplayer.connection.send(newValue ? PL_BossEventPacket.createAddPacket(this) : PL_BossEventPacket.createRemovePacket(this.getId()));
            }
        }
    }

    public Collection<ServerPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }
}