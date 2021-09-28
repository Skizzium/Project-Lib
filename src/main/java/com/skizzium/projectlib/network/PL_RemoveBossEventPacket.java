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

public class PL_RemoveBossEventPacket {
    public final UUID id;
    
    public PL_RemoveBossEventPacket(PL_BossEvent event) {
        this.id = event.getId();
    }
    
    public PL_RemoveBossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
    }

    public static PL_RemoveBossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_RemoveBossEventPacket(buffer);
    }

    public static void handle(PL_RemoveBossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleRemoveBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
