package com.skizzium.projectlib.network;

import com.skizzium.projectlib.gui.Minibar;
import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class MinibarPacket {
    public final UUID id;
    public final float progress;
    public final int entityId;
    @Nullable
    public final Integer customColor;
    @Nullable
    public final PL_BossEvent.PL_BossBarColor color;
    public final BossEventPacket.OperationType opeartion;

    public MinibarPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.progress = buffer.readFloat();
        this.entityId = buffer.readInt();
        int customColor = buffer.readInt();
        if (customColor != 0) {
            this.customColor = customColor;
        }
        else {
            this.customColor = null;
        }
        this.color = buffer.readEnum(PL_BossEvent.PL_BossBarColor.class);
        this.opeartion = buffer.readEnum(BossEventPacket.OperationType.class);
    }
    
    public MinibarPacket(Minibar event, BossEventPacket.OperationType operation) {
        this.id = event.getId();
        this.progress = event.getProgress();
        this.entityId = event.getEntity().getId();
        this.customColor = event.getCustomColor();
        this.color = event.getColor();
        this.opeartion = operation;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeFloat(this.progress);
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.customColor == null ? 0 : this.customColor);
        buffer.writeEnum(this.color == null ? PL_BossEvent.PL_BossBarColor.WHITE : this.color);
        buffer.writeEnum(this.opeartion);
    }

    public static MinibarPacket decode(FriendlyByteBuf buffer) {
        return new MinibarPacket(buffer);
    }

    public static void handle(MinibarPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleMinibarPacket(packet)));
        context.get().setPacketHandled(true);
    }
}
