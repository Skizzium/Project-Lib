package com.skizzium.projectlib.init;

import com.mojang.brigadier.CommandDispatcher;
import com.skizzium.projectlib.ProjectLib;
import com.skizzium.projectlib.commands.CommandReplacement;
import com.skizzium.projectlib.commands.PL_BossBarCommands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PL_Comamnds {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        if (event.getEnvironment() != Commands.CommandSelection.INTEGRATED) {
            CommandReplacement.replaceAndRegister(dispatcher, PL_BossBarCommands.getNode());
        }
    }
}
