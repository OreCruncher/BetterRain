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

import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry.BiomeSound;
import org.blockartistry.mod.DynSurround.util.DiurnalUtils;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

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

	private static final String CONDITION_TOKEN_RAINING = "raining";
	private static final String CONDITION_TOKEN_DAY = "day";
	private static final String CONDITION_TOKEN_NIGHT = "night";
	private static final String CONDITION_TOKEN_NETHER = "nether";
	private static final String CONDITION_TOKEN_END = "end";
	private static final String CONDITION_TOKEN_SKY = "sky";

	private static int reloadTracker = 0;

	private static class SpotSound extends PositionedSound {

		protected SpotSound(final EntityPlayer player, final BiomeSound sound) {
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
		private final BiomeSound sound;

		public PlayerSound(final EntityPlayer player, final BiomeSound sound) {
			this(player, sound, true);
		}

		public PlayerSound(final EntityPlayer player, final BiomeSound sound, final boolean repeat) {
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

		public boolean sameSound(final BiomeSound snd) {
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
	}

	private static String getConditions(final World world) {
		final StringBuilder builder = new StringBuilder();
		if (DiurnalUtils.isDaytime(world))
			builder.append(CONDITION_TOKEN_DAY);
		else
			builder.append(CONDITION_TOKEN_NIGHT);
		if (world.getRainStrength(1.0F) > 0.0F)
			builder.append(CONDITION_TOKEN_RAINING);
		if (world.provider.dimensionId == -1)
			builder.append(CONDITION_TOKEN_NETHER);
		if (world.provider.dimensionId == 1)
			builder.append(CONDITION_TOKEN_END);
		if (DimensionRegistry.hasHaze(world))
			builder.append(CONDITION_TOKEN_SKY);
		return builder.toString();
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

	@Override
	public boolean hasEvents() {
		return false;
	}

	public static void playSoundAtPlayer(EntityPlayer player, final BiomeSound sound, final int tickDelay) {
		if(player == null)
			player = Minecraft.getMinecraft().thePlayer;
		
		final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		final ISound s = new SpotSound(player, sound);

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

		final String conditions = getConditions(world);
		final BiomeGenBase playerBiome = PlayerUtils.getPlayerBiome(player, false);
		BiomeSound sound = BiomeRegistry.getSound(playerBiome, conditions);

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
}
