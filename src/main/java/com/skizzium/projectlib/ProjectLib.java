package com.skizzium.projectlib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ProjectLib.MOD_ID)
public class ProjectLib {
    public static final String MOD_ID = "bossutils";
    private static final Logger LOGGER = LogManager.getLogger();

    public ProjectLib() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static int encodeBossEventProperties(boolean darkenFlag, boolean fogFlag) {
        int i = 0;
        if (darkenFlag) {
            i |= 1;
        }

        if (fogFlag) {
            i |= 2;
        }

        return i;
    }
}
