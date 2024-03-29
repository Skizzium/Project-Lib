package com.skizzium.projectlib.gui.minibar;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketRegistry;
import com.skizzium.projectlib.network.BossEventPacket;
import com.skizzium.projectlib.network.MinibarPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ServerMinibar extends Minibar {
    private final Set<ServerPlayer> players = Sets.newHashSet();
    private final Set<ServerPlayer> unmodifiablePlayers;
    private boolean visible;

    public ServerMinibar(Entity entity, MinibarProperties properties) {
        super(Mth.createInsecureUUID(), entity, properties);
        this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
        this.visible = true;
    }

    public void setEntity(Entity entity) {
        if (entity != this.entity) {
            super.setEntity(entity);
            this.broadcastUpdatePacket();
        }
    }
    
    public void setProgress(float progress) {
        if (progress != this.progress) {
            super.setProgress(progress);
            this.broadcastUpdatePacket();
        }
    }

    public void setCustomColor(@Nullable Integer color) {
        if (!java.util.Objects.equals(color, this.customColor)) {
            super.setCustomColor(color);
            this.broadcastUpdatePacket();
        }
    }

    public void setColor(PL_BossEvent.PL_BossBarColor color) {
        if (color != this.color) {
            super.setColor(color);
            this.broadcastUpdatePacket();
        }
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            this.broadcastAddRemovePacket(BossEventPacket.OperationType.ADD, player);
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
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
                this.broadcastAddRemovePacket(newValue ? BossEventPacket.OperationType.ADD : BossEventPacket.OperationType.REMOVE, player);
            }
        }
    }

    public Collection<ServerPlayer> getPlayers() {
        return this.unmodifiablePlayers;
    }

    private void broadcastAddRemovePacket(BossEventPacket.OperationType operation, ServerPlayer player) {
        PL_PacketRegistry.INSTANCE.sendTo(new MinibarPacket(this, operation), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    private void broadcastUpdatePacket() {
        if (this.visible) {
            for (ServerPlayer player : this.players) {
                PL_PacketRegistry.INSTANCE.sendTo(new MinibarPacket(this, BossEventPacket.OperationType.UPDATE), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        }
    }
}
