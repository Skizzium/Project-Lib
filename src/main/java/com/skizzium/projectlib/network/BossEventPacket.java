package com.skizzium.projectlib.network;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.gui.Minibar;
import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.init.PL_PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class BossEventPacket {
    public final UUID id;
    public final Component name;
    public final float progress;
    public final int entityId;
    public ArrayList<UUID> minibars = new ArrayList<>();
    @Nullable
    public final Integer customColor;
    @Nullable
    public final PL_BossEvent.PL_BossBarColor color;
    public final PL_BossEvent.PL_BossBarOverlay overlay;
    public final boolean darkenScreen;
    public final boolean createWorldFog;
    public final OperationType opeartion;

    public BossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        this.name = buffer.readComponent();
        this.progress = buffer.readFloat();
        this.entityId = buffer.readInt();
        
        int i = buffer.readInt();
        for (int j = 0; j < i; j++) {
            this.minibars.add(buffer.readUUID());
        }
        
        int customColor = buffer.readInt();
        if (customColor != 0) {
            this.customColor = customColor;
        }
        else {
            this.customColor = null;
        }
        
        this.color = buffer.readEnum(PL_BossEvent.PL_BossBarColor.class);
        this.overlay = buffer.readEnum(PL_BossEvent.PL_BossBarOverlay.class);
        int i1 = buffer.readUnsignedByte();
        this.darkenScreen = (i1 & 1) > 0;
        this.createWorldFog = (i1 & 2) > 0;
        this.opeartion = buffer.readEnum(OperationType.class);
    }
    
    public BossEventPacket(PL_BossEvent event, OperationType operation) {
        this.id = event.getId();
        this.name = event.getName();
        this.progress = event.getProgress();
        this.entityId = event.getEntity().getId();
        for (Minibar minibar : event.getMinibars()) {
            this.minibars.add(minibar.getId());
        }
        this.customColor = event.getCustomColor();
        this.color = event.getColor();
        this.overlay = event.getOverlay();
        this.darkenScreen = event.shouldDarkenScreen();
        this.createWorldFog = event.shouldCreateWorldFog();
        this.opeartion = operation;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeComponent(this.name);
        buffer.writeFloat(this.progress);
        buffer.writeInt(this.entityId);
        
        buffer.writeInt(this.minibars.size());
        for (UUID minibar : this.minibars) {
            buffer.writeUUID(minibar);
        }
        
        buffer.writeInt(this.customColor == null ? 0 : this.customColor);
        buffer.writeEnum(this.color == null ? PL_BossEvent.PL_BossBarColor.WHITE : this.color);
        buffer.writeEnum(this.overlay);
        buffer.writeByte(ProjectLib.encodeBossEventProperties(this.darkenScreen, this.createWorldFog));
        buffer.writeEnum(this.opeartion);
    }

    public static BossEventPacket decode(FriendlyByteBuf buffer) {
        return new BossEventPacket(buffer);
    }

    public static void handle(BossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PL_PacketHandler.handleBossEventPacket(packet)));
        context.get().setPacketHandled(true);
    }

    public enum OperationType {
        ADD,
        REMOVE,
        UPDATE
    }
}
