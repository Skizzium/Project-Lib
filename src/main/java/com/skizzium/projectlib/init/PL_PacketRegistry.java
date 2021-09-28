package com.skizzium.projectlib.init;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.network.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PL_PacketRegistry {
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ProjectLib.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SubscribeEvent
    public static void registerMessages(FMLCommonSetupEvent event) {
        INSTANCE.registerMessage(ID++, PL_AddBossEventPacket.class, PL_AddBossEventPacket::encode, PL_AddBossEventPacket::decode, PL_AddBossEventPacket::handle);
        INSTANCE.registerMessage(ID++, PL_RemoveBossEventPacket.class, PL_RemoveBossEventPacket::encode, PL_RemoveBossEventPacket::decode, PL_RemoveBossEventPacket::handle);
        INSTANCE.registerMessage(ID++, PL_UpdateNameBossEventPacket.class, PL_UpdateNameBossEventPacket::encode, PL_UpdateNameBossEventPacket::decode, PL_UpdateNameBossEventPacket::handle);
        INSTANCE.registerMessage(ID++, PL_UpdateStyleBossEventPacket.class, PL_UpdateStyleBossEventPacket::encode, PL_UpdateStyleBossEventPacket::decode, PL_UpdateStyleBossEventPacket::handle);
        INSTANCE.registerMessage(ID++, PL_UpdatePropertiesBossEventPacket.class, PL_UpdatePropertiesBossEventPacket::encode, PL_UpdatePropertiesBossEventPacket::decode, PL_UpdatePropertiesBossEventPacket::handle);
        INSTANCE.registerMessage(ID++, PL_UpdateProgressBossEventPacket.class, PL_UpdateProgressBossEventPacket::encode, PL_UpdateProgressBossEventPacket::decode, PL_UpdateProgressBossEventPacket::handle);
    }
}
