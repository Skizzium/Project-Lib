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
        INSTANCE.registerMessage(ID++, BossEventPacket.class, BossEventPacket::encode, BossEventPacket::decode, BossEventPacket::handle);
    }
}
