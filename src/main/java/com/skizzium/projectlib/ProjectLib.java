package com.skizzium.projectlib;

import com.skizzium.projectlib.gui.*;
import com.skizzium.projectlib.gui.minibar.Minibar;
import com.skizzium.projectlib.gui.minibar.ServerMinibar;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ProjectLib.MOD_ID)
public class ProjectLib {
    public static final String MOD_ID = "bossutils";
    private static final Logger LOGGER = LogManager.getLogger();

    public ProjectLib() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
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
    
    @SubscribeEvent
    public static void renderBars() {
        
    }

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ProjectLib.MOD_ID);
    private static final RegistryObject<EntityType<TestBoss>> ENTITY = ENTITIES.register("test_boss", () -> EntityType.Builder.of(TestBoss::new, MobCategory.CREATURE).sized(1, 1).build("test_boss"));

    @SubscribeEvent
    public void onRegisterAttributes(final EntityAttributeCreationEvent event) {
        event.put(ENTITY.get(), Pig.createAttributes().build());
    }

    @Mod.EventBusSubscriber(modid = ProjectLib.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientEvents {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ENTITY.get(), PigRenderer::new);
        }
    }

    private static class TestBoss extends Pig {
        private final ServerMinibar[] minibars = {new ServerMinibar(this, new Minibar.MinibarProperties().color(0x0FF1CE)),
                                            new ServerMinibar(this, new Minibar.MinibarProperties().color(PL_BossEvent.PL_BossBarColor.LIME)),
                                            new ServerMinibar(this, new Minibar.MinibarProperties().color(0xFF0000)),
                                            new ServerMinibar(this, new Minibar.MinibarProperties().color(0xE42AAA)),
                                            new ServerMinibar(this, new Minibar.MinibarProperties().color(0xFFFFFF)),
                                            new ServerMinibar(this, new Minibar.MinibarProperties()),
                                            new ServerMinibar(this, new Minibar.MinibarProperties().color(PL_BossEvent.PL_BossBarColor.GOLD))};
        private final PL_ServerBossEvent bossBar = new PL_ServerBossEvent(this, this.getDisplayName(), new PL_BossEvent.BossEventProperties().music(SoundEvents.MUSIC_DISC_PIGSTEP).color(0xFFC0CB).overlay(PL_BossEvent.PL_BossBarOverlay.NOTCHED_5));

        public TestBoss(EntityType<? extends Pig> type, Level world) {
            super(type, world);
            for (ServerMinibar minibar : minibars) {
                bossBar.addMinibar(minibar);
            }
        }

        @Override
        public void startSeenByPlayer(ServerPlayer serverPlayer) {
            super.startSeenByPlayer(serverPlayer);
            this.bossBar.addPlayer(serverPlayer);
        }

        @Override
        public void stopSeenByPlayer(ServerPlayer serverPlayer) {
            super.stopSeenByPlayer(serverPlayer);
            this.bossBar.removePlayer(serverPlayer);
        }

        @Override
        protected void customServerAiStep() {
            super.customServerAiStep();
            this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
            this.bossBar.getMinibars().forEach((bar) -> bar.setProgress(this.getHealth() / this.getMaxHealth()));
        }
    }
}
