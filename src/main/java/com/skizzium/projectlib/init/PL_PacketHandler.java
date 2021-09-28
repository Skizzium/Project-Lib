package com.skizzium.projectlib.init;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.gui.PL_LerpingBossEvent;
import com.skizzium.projectlib.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PL_PacketHandler {
    public static void handleAddBossEventPacket(PL_AddBossEventPacket packet) {
        final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
        vanillaEvents.put(packet.id, new PL_LerpingBossEvent(packet.id, packet.name, packet.progress, packet.color, packet.overlay, packet.darkenScreen, packet.createWorldFog));
    }

    public static void handleRemoveBossEventPacket(PL_RemoveBossEventPacket packet) {
        final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
        vanillaEvents.remove(packet.id);
    }

    public static void handleUpdateNameBossEventPacket(PL_UpdateNameBossEventPacket packet) {
        final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
        vanillaEvents.get(packet.id).setName(packet.name);
    }

    public static void handleUpdateStyleBossEventPacket(PL_UpdateStyleBossEventPacket packet) {
        final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
        PL_LerpingBossEvent lerpingBossEvent = (PL_LerpingBossEvent) vanillaEvents.get(packet.id);
        lerpingBossEvent.setCustomColor(packet.color);
        lerpingBossEvent.setCustomOverlay(packet.overlay);
    }

    public static void handleUpdatePropertiesBossEventPacket(PL_UpdatePropertiesBossEventPacket packet) {
        final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
        PL_LerpingBossEvent lerpingBossEvent = (PL_LerpingBossEvent) vanillaEvents.get(packet.id);
        lerpingBossEvent.setDarkenScreen(packet.darkenScreen);
        lerpingBossEvent.setCreateWorldFog(packet.createWorldFog);
    }

    public static void handleUpdateProgressBossEventPacket(PL_UpdateProgressBossEventPacket packet) {
        final Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
        vanillaEvents.get(packet.id).setProgress(packet.progress);
    }
}
