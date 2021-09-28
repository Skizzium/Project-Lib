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

public class PL_UpdateNameBossEventPacket {
    public final UUID id;
    public final Component name;
    
    public PL_UpdateNameBossEventPacket(PL_BossEvent event) {
        this.id = event.getId();
        this.name = event.getName();
    }
    
    public PL_UpdateNameBossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.name = buffer.readComponent();
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeComponent(this.name);
    }

    public static PL_UpdateNameBossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_UpdateNameBossEventPacket(buffer);
    }

    public static void handle(PL_UpdateNameBossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleUpdateNameBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
