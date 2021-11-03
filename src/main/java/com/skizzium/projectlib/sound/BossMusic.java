package com.skizzium.projectlib.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossMusic extends AbstractTickableSoundInstance {
    private final Minecraft minecraft;
    private final Entity entity;

    public BossMusic(Entity entity, SoundEvent musicEvent, Minecraft minecraftInstance) {
        super(musicEvent, SoundSource.MUSIC);
        this.entity = entity;
        this.looping = true;
        this.delay = 0;
        this.volume = 100.0F;
        this.minecraft = minecraftInstance;
    }

    @Override
    public boolean canStartSilent() {
        return false;
    }

    @Override
    public boolean canPlaySound() {
        return true;
    }

    @Override
    public void tick() {
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        
        MusicManager musicManager = this.minecraft.getMusicManager();
        musicManager.stopPlaying();
        musicManager.nextSongDelay = Integer.MAX_VALUE;
    }
}
