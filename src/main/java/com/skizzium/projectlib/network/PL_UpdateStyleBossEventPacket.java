package com.skizzium.projectlib.network;

import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PL_UpdateStyleBossEventPacket {
    public final UUID id;
    public final PL_BossEvent.PL_BossBarColor color;
    public final PL_BossEvent.PL_BossBarOverlay overlay;
    
    public PL_UpdateStyleBossEventPacket(PL_BossEvent event) {
        this.id = event.getId();
        this.color = event.getColor();
        this.overlay = event.getOverlay();
    }
    
    public PL_UpdateStyleBossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.color = buffer.readEnum(PL_BossEvent.PL_BossBarColor.class);
        this.overlay = buffer.readEnum(PL_BossEvent.PL_BossBarOverlay.class);
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeEnum(this.color);
        buffer.writeEnum(this.overlay);
    }

    public static PL_UpdateStyleBossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_UpdateStyleBossEventPacket(buffer);
    }

    public static void handle(PL_UpdateStyleBossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleUpdateStyleBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
