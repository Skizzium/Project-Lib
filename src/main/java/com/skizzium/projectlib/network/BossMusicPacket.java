package com.skizzium.projectlib.network;

import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BossMusicPacket {
    public final UUID eventID;
    public final SoundEvent bossMusic;
    public final OperationType opeartion;

    public BossMusicPacket(FriendlyByteBuf buffer) {
        this.eventID = buffer.readUUID();
        this.bossMusic = buffer.readRegistryId();
        this.opeartion = buffer.readEnum(OperationType.class);
    }

    public BossMusicPacket(PL_BossEvent event, OperationType operation) {
        this.eventID = event.getId();
        this.bossMusic = event.getMusic();
        this.opeartion = operation;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(eventID);
        buffer.writeRegistryId(this.bossMusic);
        buffer.writeEnum(this.opeartion);
    }

    public static BossMusicPacket decode(FriendlyByteBuf buffer) {
        return new BossMusicPacket(buffer);
    }

    public static void handle(BossMusicPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleBossMusicPacket(packet)));
        context.get().setPacketHandled(true);
    }

    public enum OperationType {
        START,
        STOP,
        UPDATE
    }
}
