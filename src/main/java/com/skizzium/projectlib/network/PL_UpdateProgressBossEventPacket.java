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

public class PL_UpdateProgressBossEventPacket {
    public final UUID id;
    public final float progress;
    
    public PL_UpdateProgressBossEventPacket(PL_BossEvent event) {
        this.id = event.getId();
        this.progress = event.getProgress();
    }
    
    public PL_UpdateProgressBossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.progress = buffer.readFloat();
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeFloat(this.progress);
    }

    public static PL_UpdateProgressBossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_UpdateProgressBossEventPacket(buffer);
    }

    public static void handle(PL_UpdateProgressBossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleUpdateProgressBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
