package com.skizzium.projectlib.init;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.gui.LerpingMinibar;
import com.skizzium.projectlib.gui.PL_LerpingBossEvent;
import com.skizzium.projectlib.network.*;
import com.skizzium.projectlib.sound.BossMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PL_PacketHandler {
    public static void handleMinibarPacket(MinibarPacket packet) {
        if (packet.opeartion.equals(BossEventPacket.OperationType.ADD)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            vanillaEvents.put(packet.id, new LerpingMinibar(packet.id, packet.progress, Minecraft.getInstance().level.getEntity(packet.entityId), packet.customColor, packet.color));
        }
        else if (packet.opeartion.equals(BossEventPacket.OperationType.REMOVE)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            vanillaEvents.remove(packet.id);
        }
        else if (packet.opeartion.equals(BossEventPacket.OperationType.UPDATE)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            LerpingMinibar lerpingBossEvent = (LerpingMinibar)vanillaEvents.get(packet.id);

            lerpingBossEvent.setProgress(packet.progress);
            lerpingBossEvent.setCustomHexColor(packet.customColor);
            lerpingBossEvent.setCustomColor(packet.color);
        }
    }
    
    public static void handleBossMusicPacket(BossMusicPacket packet) {
        if (packet.opeartion.equals(BossMusicPacket.OperationType.START)) {
            LerpingBossEvent bossEvent = Minecraft.getInstance().gui.getBossOverlay().events.get(packet.eventID);
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();

            if (bossEvent instanceof PL_LerpingBossEvent) {
                Minecraft.getInstance().getMusicManager().stopPlaying();
    
                if (((PL_LerpingBossEvent) bossEvent).music == null) {
                    ((PL_LerpingBossEvent) bossEvent).music = new BossMusic(((PL_LerpingBossEvent) bossEvent).getEntity(), packet.bossMusic, Minecraft.getInstance());
                }
    
                if (packet.update || !soundManager.isActive(((PL_LerpingBossEvent) bossEvent).music)) {
                    soundManager.play(((PL_LerpingBossEvent) bossEvent).music);
                }
            }
        }
        else if (packet.opeartion.equals(BossMusicPacket.OperationType.STOP)) {
            LerpingBossEvent bossEvent = Minecraft.getInstance().gui.getBossOverlay().events.get(packet.eventID);

            if (bossEvent instanceof PL_LerpingBossEvent) {
                Minecraft.getInstance().getSoundManager().stop(((PL_LerpingBossEvent) bossEvent).music);

                // Recalculate the next song delay similarly to how vanilla does it
                Music music = Minecraft.getInstance().getSituationalMusic();
                MusicManager musicManager = Minecraft.getInstance().getMusicManager();
                musicManager.nextSongDelay = Math.min(100, Mth.nextInt(new Random(), music.getMinDelay(), music.getMaxDelay()));
            }
        }
    }
    
    public static void handleBossEventPacket(BossEventPacket packet) {
        if (packet.opeartion.equals(BossEventPacket.OperationType.ADD)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            vanillaEvents.put(packet.id, new PL_LerpingBossEvent(packet.id, packet.name, packet.progress, Minecraft.getInstance().level.getEntity(packet.entityId), packet.minibars, packet.customColor, packet.color, packet.overlay, packet.darkenScreen, packet.createWorldFog));
        }
        else if (packet.opeartion.equals(BossEventPacket.OperationType.REMOVE)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            vanillaEvents.remove(packet.id);
        }
        else if (packet.opeartion.equals(BossEventPacket.OperationType.UPDATE)) {
            Map<UUID, LerpingBossEvent> vanillaEvents = Minecraft.getInstance().gui.getBossOverlay().events;
            PL_LerpingBossEvent lerpingBossEvent = (PL_LerpingBossEvent)vanillaEvents.get(packet.id);

            lerpingBossEvent.setName(packet.name);
            lerpingBossEvent.setProgress(packet.progress);
            lerpingBossEvent.setMinibars(packet.minibars);
            
            lerpingBossEvent.setCustomHexColor(packet.customColor);
            lerpingBossEvent.setCustomColor(packet.color);
            lerpingBossEvent.setCustomOverlay(packet.overlay);

            lerpingBossEvent.setDarkenScreen(packet.darkenScreen);
            lerpingBossEvent.setCreateWorldFog(packet.createWorldFog);
        }
    }
}
