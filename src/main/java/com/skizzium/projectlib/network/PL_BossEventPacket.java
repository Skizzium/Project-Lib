package com.skizzium.projectlib.network;

import com.google.common.collect.Maps;
import com.skizzium.projectlib.gui.PL_BossEvent;
import com.skizzium.projectlib.gui.PL_LerpingBossEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class PL_BossEventPacket implements Packet<ClientGamePacketListener> {
    private final UUID id;
    private final Operation operation;
    private static final Map<UUID, PL_LerpingBossEvent> events = Maps.newLinkedHashMap();
    static final Operation REMOVE_OPERATION = new Operation() {
        public OperationType getType() {
            return OperationType.REMOVE;
        }

        public void dispatch(UUID uuid, Handler handler) {
            handler.remove(uuid);
        }

        public void write(FriendlyByteBuf buffer) {
        }
    };

    private PL_BossEventPacket(UUID uuid, Operation performOperation) {
        this.id = uuid;
        this.operation = performOperation;
    }

    public PL_BossEventPacket(FriendlyByteBuf buffer) {
        this.id = buffer.readUUID();
        OperationType opearationType = buffer.readEnum(OperationType.class);
        this.operation = opearationType.reader.apply(buffer);
    }

    public static Map<UUID, PL_LerpingBossEvent> getEvents() {
        return events;
    }

    public static PL_BossEventPacket createAddPacket(PL_BossEvent event) {
        return new PL_BossEventPacket(event.getId(), new AddOperation(event));
    }

    public static PL_BossEventPacket createRemovePacket(UUID uuid) {
        return new PL_BossEventPacket(uuid, REMOVE_OPERATION);
    }

    public static PL_BossEventPacket createUpdateProgressPacket(PL_BossEvent event) {
        return new PL_BossEventPacket(event.getId(), new UpdateProgressOperation(event.getProgress()));
    }

    public static PL_BossEventPacket createUpdateNamePacket(PL_BossEvent event) {
        return new PL_BossEventPacket(event.getId(), new UpdateNameOperation(event.getName()));
    }

    public static PL_BossEventPacket createUpdateStylePacket(PL_BossEvent event) {
        return new PL_BossEventPacket(event.getId(), new UpdateStyleOperation(event.getColor(), event.getOverlay()));
    }

    public static PL_BossEventPacket createUpdatePropertiesPacket(PL_BossEvent event) {
        return new PL_BossEventPacket(event.getId(), new UpdatePropertiesOperation(event.shouldDarkenScreen(), event.shouldCreateWorldFog()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.id);
        buffer.writeEnum(this.operation.getType());
        this.operation.write(buffer);
    }

    static int encodeProperties(boolean darkenFlag, boolean fogFlag) {
        int i = 0;
        if (darkenFlag) {
            i |= 1;
        }

        if (fogFlag) {
            i |= 2;
        }

        return i;
    }

    public static PL_BossEventPacket decode(FriendlyByteBuf buffer) {
        return new PL_BossEventPacket(buffer);
    }

    public void handle(ClientGamePacketListener listener) {
        handlePacket(this);
    }

    public static void handle(PL_BossEventPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> packet.handlePacket(packet)));
        context.get().setPacketHandled(true);
    }

    public void handlePacket(PL_BossEventPacket packet) {
        packet.dispatch(new Handler() {
            final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            public void add(UUID uuid, Component displayName, float progress, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay, boolean darkenScreen, boolean fog) {
                vanillaEvents.put(uuid, new PL_LerpingBossEvent(uuid, displayName, progress, color, overlay, darkenScreen, fog));
            }

            public void remove(UUID uuid) {
                vanillaEvents.remove(uuid);
            }

            public void updateProgress(UUID uuid, float progress) {
                vanillaEvents.get(uuid).setProgress(progress);
            }

            public void updateName(UUID uuid, Component displayName) {
                vanillaEvents.get(uuid).setName(displayName);
            }

            public void updateStyle(UUID uuid, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay) {
                PL_LerpingBossEvent lerpingBossEvent = (PL_LerpingBossEvent) vanillaEvents.get(uuid);
                lerpingBossEvent.setCustomColor(color);
                lerpingBossEvent.setCustomOverlay(overlay);
            }

            public void updateProperties(UUID uuid, boolean darkenScreen, boolean fog) {
                PL_LerpingBossEvent lerpingBossEvent = (PL_LerpingBossEvent) vanillaEvents.get(uuid);
                lerpingBossEvent.setDarkenScreen(darkenScreen);
                lerpingBossEvent.setCreateWorldFog(fog);
            }
        });
    }

    public void dispatch(Handler handler) {
        this.operation.dispatch(this.id, handler);
    }

    static class AddOperation implements Operation {
        private final Component name;
        private final float progress;
        private final PL_BossEvent.PL_BossBarColor color;
        private final PL_BossEvent.PL_BossBarOverlay overlay;
        private final boolean darkenScreen;
        private final boolean createWorldFog;

        AddOperation(PL_BossEvent event) {
            this.name = event.getName();
            this.progress = event.getProgress();
            this.color = event.getColor();
            this.overlay = event.getOverlay();
            this.darkenScreen = event.shouldDarkenScreen();
            this.createWorldFog = event.shouldCreateWorldFog();
        }

        private AddOperation(FriendlyByteBuf buffer) {
            this.name = buffer.readComponent();
            this.progress = buffer.readFloat();
            this.color = buffer.readEnum(PL_BossEvent.PL_BossBarColor.class);
            this.overlay = buffer.readEnum(PL_BossEvent.PL_BossBarOverlay.class);
            int i = buffer.readUnsignedByte();
            this.darkenScreen = (i & 1) > 0;
            this.createWorldFog = (i & 4) > 0;
        }

        public OperationType getType() {
            return OperationType.ADD;
        }

        public void dispatch(UUID uuid, Handler handler) {
            handler.add(uuid, this.name, this.progress, this.color, this.overlay, this.darkenScreen, this.createWorldFog);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeComponent(this.name);
            buffer.writeFloat(this.progress);
            buffer.writeEnum(this.color);
            buffer.writeEnum(this.overlay);
            buffer.writeByte(PL_BossEventPacket.encodeProperties(this.darkenScreen, this.createWorldFog));
        }
    }

    public interface Handler {
        default void add(UUID uuid, Component name, float progress, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay, boolean darkenScreen, boolean fog) {
        }

        default void remove(UUID uuid) {
        }

        default void updateProgress(UUID uuid, float progress) {
        }

        default void updateName(UUID uuid, Component name) {
        }

        default void updateStyle(UUID uuid, PL_BossEvent.PL_BossBarColor color, PL_BossEvent.PL_BossBarOverlay overlay) {
        }

        default void updateProperties(UUID uuid, boolean darkenScreen, boolean fog) {
        }
    }

    interface Operation {
        OperationType getType();

        void dispatch(UUID uuid, Handler handler);

        void write(FriendlyByteBuf buffer);
    }

    static enum OperationType {
        ADD(AddOperation::new),
        REMOVE((p_178719_) -> PL_BossEventPacket.REMOVE_OPERATION),
        UPDATE_PROGRESS(UpdateProgressOperation::new),
        UPDATE_NAME(UpdateNameOperation::new),
        UPDATE_STYLE(UpdateStyleOperation::new),
        UPDATE_PROPERTIES(UpdatePropertiesOperation::new);

        final Function<FriendlyByteBuf, Operation> reader;

        private OperationType(Function<FriendlyByteBuf, Operation> reader) {
            this.reader = reader;
        }
    }

    static class UpdateNameOperation implements Operation {
        private final Component name;

        UpdateNameOperation(Component newName) {
            this.name = newName;
        }

        private UpdateNameOperation(FriendlyByteBuf buffer) {
            this.name = buffer.readComponent();
        }

        public OperationType getType() {
            return OperationType.UPDATE_NAME;
        }

        public void dispatch(UUID uuid, Handler handler) {
            handler.updateName(uuid, this.name);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeComponent(this.name);
        }
    }

    static class UpdateProgressOperation implements Operation {
        private final float progress;

        UpdateProgressOperation(float newProgress) {
            this.progress = newProgress;
        }

        private UpdateProgressOperation(FriendlyByteBuf buffer) {
            this.progress = buffer.readFloat();
        }

        public OperationType getType() {
            return OperationType.UPDATE_PROGRESS;
        }

        public void dispatch(UUID uuid, Handler handler) {
            handler.updateProgress(uuid, this.progress);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeFloat(this.progress);
        }
    }

    static class UpdatePropertiesOperation implements Operation {
        private final boolean darkenScreen;
        private final boolean createWorldFog;

        UpdatePropertiesOperation(boolean darkenScreen, boolean fog) {
            this.darkenScreen = darkenScreen;
            this.createWorldFog = fog;
        }

        private UpdatePropertiesOperation(FriendlyByteBuf buffer) {
            int i = buffer.readUnsignedByte();
            this.darkenScreen = (i & 1) > 0;
            this.createWorldFog = (i & 2) > 0;
        }

        public OperationType getType() {
            return OperationType.UPDATE_PROPERTIES;
        }

        public void dispatch(UUID uuid, Handler handler) {
            handler.updateProperties(uuid, this.darkenScreen, this.createWorldFog);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(PL_BossEventPacket.encodeProperties(this.darkenScreen, this.createWorldFog));
        }
    }

    static class UpdateStyleOperation implements Operation {
        private final PL_BossEvent.PL_BossBarColor color;
        private final PL_BossEvent.PL_BossBarOverlay overlay;

        UpdateStyleOperation(PL_BossEvent.PL_BossBarColor newColor, PL_BossEvent.PL_BossBarOverlay newOverlay) {
            this.color = newColor;
            this.overlay = newOverlay;
        }

        private UpdateStyleOperation(FriendlyByteBuf buffer) {
            this.color = buffer.readEnum(PL_BossEvent.PL_BossBarColor.class);
            this.overlay = buffer.readEnum(PL_BossEvent.PL_BossBarOverlay.class);
        }

        public OperationType getType() {
            return OperationType.UPDATE_STYLE;
        }

        public void dispatch(UUID uuid, Handler handler) {
            handler.updateStyle(uuid, this.color, this.overlay);
        }

        public void write(FriendlyByteBuf buffer) {
            buffer.writeEnum(this.color);
            buffer.writeEnum(this.overlay);
        }
    }
}