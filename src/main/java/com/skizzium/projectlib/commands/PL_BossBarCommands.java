package com.skizzium.projectlib.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PL_BossBarCommands {
    public static LiteralArgumentBuilder<CommandSourceStack> getNode() {
        return Commands.literal("bossbar").requires((p_136627_) -> p_136627_.hasPermission(2));
    }
}
