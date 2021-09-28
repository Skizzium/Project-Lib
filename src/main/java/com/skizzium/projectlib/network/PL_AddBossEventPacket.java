package com.skizzium.projectlib.network;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PL_AddBossEventPacket {
    public final UUID id;
    public final Component name;
    public final float progress;
    public final PL_BossEvent.PL_BossBarColor color;
    public final PL_BossEvent.PL_BossBarOverlay overlay;
    public final boolean darkenScreen;
    public final boolean createWorldFog;
    
    public PL_AddBossEventPacket(PL_BossEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.progress = event.getProgress();
        this.color = event.getColor();
        this.overlay = event.getOverlay();
        this.darkenScreen = event.shouldDarkenScreen();
        this.createWorldFog = event.shouldCreateWorldFog();
    }
    
    public PL_AddBossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.name = buffer.readComponent();
        this.progress = buffer.readFloat();
        this.color = buffer.readEnum(PL_BossEvent.PL_BossBarColor.class);
        this.overlay = buffer.readEnum(PL_BossEvent.PL_BossBarOverlay.class);
        int i = buffer.readUnsignedByte();
        this.darkenScreen = (i & 1) > 0;
        this.createWorldFog = (i & 2) > 0;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeComponent(this.name);
        buffer.writeFloat(this.progress);
        buffer.writeEnum(this.color);
        buffer.writeEnum(this.overlay);
        buffer.writeByte(ProjectLib.encodeBossEventProperties(this.darkenScreen, this.createWorldFog));
    }

    public static PL_AddBossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_AddBossEventPacket(buffer);
    }

    public static void handle(PL_AddBossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleAddBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
