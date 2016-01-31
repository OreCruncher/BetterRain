/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.DynSurround.client;

import java.lang.ref.WeakReference;
import java.util.Random;

import org.blockartistry.mod.DynSurround.client.fx.SoundEffect;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.event.DiagnosticEvent;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

@SideOnly(Side.CLIENT)
public class PlayerSoundEffectHandler implements IClientEffectHandler {

	private static final Random RANDOM = new XorShiftRandom();
	private static final float VOLUME_INCREMENT = 0.02F;

	private static int reloadTracker = 0;

	private static class SpotSound extends PositionedSound {

		public SpotSound(final int x, final int y, final int z, final SoundEffect sound) {
			super(new ResourceLocation(sound.sound));

			this.volume = sound.volume;
			this.field_147663_c = sound.pitch;
			this.repeat = false;
			this.field_147665_h = 0;

			this.xPosF = (float) x + 0.5F;
			this.yPosF = (float) y + 0.5F;
			this.zPosF = (float) z + 0.5F;
		}

		public SpotSound(final EntityPlayer player, final SoundEffect sound) {
			super(new ResourceLocation(sound.sound));

			this.volume = sound.volume;
			this.field_147663_c = sound.pitch;
			this.repeat = false;
			this.field_147665_h = 0;

			this.xPosF = (float) player.posX;
			this.yPosF = (float) player.posY + 1;
			this.zPosF = (float) player.posZ;
		}

	}

	private static class PlayerSound extends MovingSound {
		private boolean fadeAway;
		private final WeakReference<EntityPlayer> player;
		private final SoundEffect sound;

		public PlayerSound(final EntityPlayer player, final SoundEffect sound) {
			this(player, sound, true);
		}

		public PlayerSound(final EntityPlayer player, final SoundEffect sound, final boolean repeat) {
			super(new ResourceLocation(sound.sound));
			// Don't set volume to 0; MC will optimize out
			this.sound = sound;
			this.volume = repeat ? 0.01F : sound.volume;
			this.field_147663_c = sound.pitch;
			this.player = new WeakReference<EntityPlayer>(player);
			this.repeat = repeat;
			this.fadeAway = false;

			// Repeat delay
			this.field_147665_h = 0;

			// Initial position
			this.xPosF = (float) (player.posX);
			this.yPosF = (float) (player.posY + 1);
			this.zPosF = (float) (player.posZ);
		}

		public void fadeAway() {
			this.fadeAway = true;
		}

		public boolean sameSound(final SoundEffect snd) {
			return this.sound.equals(snd);
		}

		@Override
		public void update() {
			if (this.fadeAway) {
				this.volume -= VOLUME_INCREMENT;
				if (this.volume < 0.0F) {
					this.volume = 0.0F;
				}
			} else if (this.volume < this.sound.volume) {
				this.volume += VOLUME_INCREMENT;
				if (this.volume > this.sound.volume)
					this.volume = this.sound.volume;
			}

			if (this.volume == 0.0F)
				this.donePlaying = true;
		}

		@Override
		public float getXPosF() {
			final EntityPlayer player = this.player.get();
			if (player == null) {
				this.donePlaying = true;
				return 0.0F;
			}
			return (float) player.posX;
		}

		@Override
		public float getYPosF() {
			final EntityPlayer player = this.player.get();
			if (player == null) {
				this.donePlaying = true;
				return 0.0F;
			}
			return (float) player.posY + 1;
		}

		@Override
		public float getZPosF() {
			final EntityPlayer player = this.player.get();
			if (player == null) {
				this.donePlaying = true;
				return 0.0F;
			}
			return (float) player.posZ;
		}
		
		@Override
		public String toString() {
			return this.sound.toString();
		}
	}

	private static boolean didReloadOccur() {
		final int count = BiomeRegistry.getReloadCount();
		if (count != reloadTracker) {
			reloadTracker = count;
			return true;
		}
		return false;
	}

	// Current active background sound
	private static PlayerSound currentSound = null;

	public static void playSoundAtPlayer(EntityPlayer player, final SoundEffect sound, final int tickDelay) {
		if(player == null)
			player = Minecraft.getMinecraft().thePlayer;
		
		final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		final ISound s = new SpotSound(player, sound);

		if (tickDelay == 0)
			handler.playSound(s);
		else
			handler.playDelayedSound(s, tickDelay);
	}
	
	public static void playSoundAt(final int x, final int y, final int z, final SoundEffect sound, final int tickDelay) {
		final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		final ISound s = new SpotSound(x, y, z, sound);

		if (tickDelay == 0)
			handler.playSound(s);
		else
			handler.playDelayedSound(s, tickDelay);
	}


	
	@Override
	public void process(final World world, final EntityPlayer player) {
		// Dead player or they are covered with blocks
		if (player.isDead) {
			if (currentSound != null) {
				currentSound.fadeAway();
				currentSound = null;
			}
			return;
		}

		final String conditions = DimensionRegistry.getConditions(world);
		final BiomeGenBase playerBiome = PlayerUtils.getPlayerBiome(player, false);
		SoundEffect sound = BiomeRegistry.getSound(playerBiome, conditions);

		if (currentSound != null) {
			if (didReloadOccur() || sound == null || !currentSound.sameSound(sound)) {
				currentSound.fadeAway();
				currentSound = null;
			}
		}

		if (currentSound == null && sound != null) {
			currentSound = new PlayerSound(player, sound);
			Minecraft.getMinecraft().getSoundHandler().playSound(currentSound);
		}

		sound = BiomeRegistry.getSpotSound(playerBiome, conditions, RANDOM);
		if (sound != null) {
			playSoundAtPlayer(player, sound, 0);
		}
	}
	
	@Override
	public boolean hasEvents() {
		return true;
	}

	@SubscribeEvent
	public void diagnostics(final DiagnosticEvent.Gather event) {
		if(currentSound != null) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Active Sound: ").append(currentSound.toString());
			event.output.add(builder.toString());
		}
	}
}
