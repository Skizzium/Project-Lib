package com.skizzium.projectlib.network;

import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BossMusicPacket {
    public final UUID eventID;
    public SoundEvent bossMusic = null;
    public final OperationType opeartion;
    
    /**
     * If true, the handler will ignore if the music is already playing.
     * This is needed because the music doesn't stop in time for the next song to start, thus the handler
     * doesn't start the next song to prevent overlapping which in this case is only fractions of a second.
     */
    public final boolean update;

    public BossMusicPacket(FriendlyByteBuf buffer) {
        this.eventID = buffer.readUUID();
        this.opeartion = buffer.readEnum(OperationType.class);
        this.update = buffer.readBoolean();
        if (!opeartion.equals(OperationType.STOP)) {
            this.bossMusic = buffer.readRegistryId();
        }
    }

    public BossMusicPacket(PL_BossEvent event, OperationType operation, boolean update) {
        this.eventID = event.getId();
        this.opeartion = operation;
        this.update = update;
        if (!opeartion.equals(OperationType.STOP)) {
            this.bossMusic = event.getMusic();
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(eventID);
        buffer.writeEnum(this.opeartion);
        buffer.writeBoolean(this.update);
        if (!opeartion.equals(OperationType.STOP)) {
            buffer.writeRegistryId(this.bossMusic);
        }
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
        STOP
    }
}
