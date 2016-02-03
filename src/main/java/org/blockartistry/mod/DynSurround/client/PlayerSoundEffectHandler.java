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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.fx.SoundEffect;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.event.DiagnosticEvent;
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
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;

@SideOnly(Side.CLIENT)
public class PlayerSoundEffectHandler implements IClientEffectHandler {

	private static final Random RANDOM = new XorShiftRandom();
	private static final float VOLUME_INCREMENT = 0.02F;
	private static final float VOLUME_DECREMENT = 0.015F;
	private static final float MASTER_SCALE_FACTOR = ModOptions.getMasterSoundScaleFactor();

	// TODO: Need jump sound
	private static SoundEffect JUMP_SOUND = null;

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

		@Override
		public float getVolume() {
			return super.getVolume() * MASTER_SCALE_FACTOR;
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
			this.volume = (repeat || sound.skipFade) ? 0.01F : sound.volume;
			this.field_147663_c = sound.pitch;
			this.player = new WeakReference<EntityPlayer>(player);
			this.repeat = repeat;
			this.fadeAway = false;

			// Repeat delay
			this.field_147665_h = sound.repeatDelay;

			// Initial position
			this.xPosF = (float) (player.posX);
			this.yPosF = (float) (player.posY + 1);
			this.zPosF = (float) (player.posZ);
		}

		public void fadeAway() {
			this.fadeAway = true;
			if (this.sound.skipFade)
				this.donePlaying = true;
		}

		public boolean sameSound(final SoundEffect snd) {
			return this.sound.equals(snd);
		}

		@Override
		public void update() {
			if (this.fadeAway) {
				this.volume -= VOLUME_DECREMENT;
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
		public float getVolume() {
			return super.getVolume() * MASTER_SCALE_FACTOR;
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

	// Active loop sounds
	private static final List<PlayerSound> activeSounds = new ArrayList<PlayerSound>();

	private static void clearSounds() {
		for (final PlayerSound sound : activeSounds)
			sound.fadeAway();
		activeSounds.clear();
	}

	private static boolean isPlaying(final SoundEffect sound) {
		for (final PlayerSound s : activeSounds)
			if (s.sameSound(sound))
				return true;
		return false;
	}

	private static void playSound(final EntityPlayer player, final SoundEffect sound) {
		final PlayerSound s = new PlayerSound(player, sound);
		activeSounds.add(s);
		Minecraft.getMinecraft().getSoundHandler().playSound(s);
	}

	public static void playSoundAtPlayer(EntityPlayer player, final SoundEffect sound, final int tickDelay) {
		if (player == null)
			player = EnvironState.getPlayer();

		final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		final ISound s = new SpotSound(player, sound);

		if (tickDelay == 0)
			handler.playSound(s);
		else
			handler.playDelayedSound(s, tickDelay);
	}

	public static void playSoundAt(final int x, final int y, final int z, final SoundEffect sound,
			final int tickDelay) {
		final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		final ISound s = new SpotSound(x, y, z, sound);

		if (tickDelay == 0)
			handler.playSound(s);
		else
			handler.playDelayedSound(s, tickDelay);
	}

	private static void processSounds(final EntityPlayer player, final List<SoundEffect> sounds) {
		// Need to remove sounds that are active but not
		// in the incoming list
		final Iterator<PlayerSound> itr = activeSounds.iterator();
		while (itr.hasNext()) {
			final PlayerSound sound = itr.next();
			if (!sounds.contains(sound.sound)) {
				sound.fadeAway();
				itr.remove();
			}
		}

		// Add sounds from the incoming list that are not
		// active.
		for (final SoundEffect sound : sounds)
			if (!isPlaying(sound))
				playSound(player, sound);
	}

	@Override
	public void process(final World world, final EntityPlayer player) {
		if (didReloadOccur() || player.isDead)
			clearSounds();

		// Dead players hear no sounds
		if (player.isDead)
			return;

		final BiomeGenBase playerBiome = EnvironState.getPlayerBiome();
		final String conditions = EnvironState.getConditions();

		final List<SoundEffect> sounds = new ArrayList<SoundEffect>();
		sounds.addAll(BiomeRegistry.getSounds(playerBiome, conditions));
		sounds.addAll(BiomeRegistry.getSounds(BiomeRegistry.PLAYER, conditions));
		processSounds(player, sounds);

		SoundEffect sound = BiomeRegistry.getSpotSound(playerBiome, conditions, RANDOM);
		if (sound != null)
			playSoundAtPlayer(player, sound, 0);
		
		sound = BiomeRegistry.getSpotSound(BiomeRegistry.PLAYER, conditions, RANDOM);
		if (sound != null)
			playSoundAtPlayer(player, sound, 0);
	}

	@Override
	public boolean hasEvents() {
		return true;
	}

	@SubscribeEvent
	public void diagnostics(final DiagnosticEvent.Gather event) {
		for (final PlayerSound sound : activeSounds) {
			final StringBuilder builder = new StringBuilder();
			builder.append("Active Sound: ").append(sound.toString());
			builder.append(" (effective volume:").append(sound.getVolume()).append(')');
			event.output.add(builder.toString());
		}
	}

	@SubscribeEvent
	public void onJump(final LivingJumpEvent event) {
		if (JUMP_SOUND != null && event.entity.worldObj.isRemote && EnvironState.isPlayer(event.entity)) {
			playSoundAtPlayer(EnvironState.getPlayer(), JUMP_SOUND, 0);
		}
	}
}
