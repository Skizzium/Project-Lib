package com.skizzium.projectlib.network;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PL_UpdatePropertiesBossEventPacket {
    public final UUID id;
    public final boolean darkenScreen;
    public final boolean createWorldFog;
    
    public PL_UpdatePropertiesBossEventPacket(PL_BossEvent event) {
        this.id = event.getId();
        this.darkenScreen = event.shouldDarkenScreen();
        this.createWorldFog = event.shouldCreateWorldFog();
    }
    
    public PL_UpdatePropertiesBossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        int i = buffer.readUnsignedByte();
        this.darkenScreen = (i & 1) > 0;
        this.createWorldFog = (i & 1) > 0;
    }
    
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeByte(ProjectLib.encodeBossEventProperties(this.darkenScreen, this.createWorldFog));
    }

    public static PL_UpdatePropertiesBossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_UpdatePropertiesBossEventPacket(buffer);
    }

    public static void handle(PL_UpdatePropertiesBossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleUpdatePropertiesBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
