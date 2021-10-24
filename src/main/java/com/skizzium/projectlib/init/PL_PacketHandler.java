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
    public static void handleBossEventPacket(BossEventPacket packet) {
        if (packet.opeartion.equals(BossEventPacket.OperationType.ADD)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            vanillaEvents.put(packet.id, new PL_LerpingBossEvent(packet.id, packet.name, packet.progress, packet.color, packet.overlay, packet.darkenScreen, packet.createWorldFog));
        }
        else if (packet.opeartion.equals(BossEventPacket.OperationType.REMOVE)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            vanillaEvents.remove(packet.id);
        }
        else if (packet.opeartion.equals(BossEventPacket.OperationType.UPDATE)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            PL_LerpingBossEvent lerpingBossEvent = (PL_LerpingBossEvent)vanillaEvents.get(packet.id);

            vanillaEvents.get(packet.id).setName(packet.name);
            vanillaEvents.get(packet.id).setProgress(packet.progress);

            lerpingBossEvent.setCustomColor(packet.color);
            lerpingBossEvent.setCustomOverlay(packet.overlay);

            lerpingBossEvent.setDarkenScreen(packet.darkenScreen);
            lerpingBossEvent.setCreateWorldFog(packet.createWorldFog);
        }
    }
}
