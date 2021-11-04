package com.skizzium.projectlib.init;

import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.commands.CommandReplacement;
import com.skizzium.projectlib.commands.PL_BossBarCommands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PL_Comamnds {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandReplacement.replaceAndRegister(event.getDispatcher(), PL_BossBarCommands.getNode());
    }
}
