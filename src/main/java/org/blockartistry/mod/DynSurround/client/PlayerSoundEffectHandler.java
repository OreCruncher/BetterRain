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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.sound.SoundEffect;
import org.blockartistry.mod.DynSurround.client.sound.SoundManager;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.event.DiagnosticEvent;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import paulscode.sound.SoundSystemConfig;

@SideOnly(Side.CLIENT)
public class PlayerSoundEffectHandler implements IClientEffectHandler {

	private static final Random RANDOM = new XorShiftRandom();
	private static final float MASTER_SCALE_FACTOR = ModOptions.getMasterSoundScaleFactor();
	private static final int SPOT_SOUND_RANGE = 6;
	private static final int SOUND_QUEUE_SLACK = 12;

	private static int playerDimension = 0;
	private static int reloadTracker = 0;

	private static class SpotSound extends PositionedSound {

		public SpotSound(final int x, final int y, final int z, final SoundEffect sound) {
			super(new ResourceLocation(sound.sound));

			this.volume = sound.volume;
			this.pitch = sound.getPitch(RANDOM);
			this.repeat = false;
			this.repeatDelay = 0;

			this.xPosF = (float) x + 0.5F;
			this.yPosF = (float) y + 0.5F;
			this.zPosF = (float) z + 0.5F;
		}

		public SpotSound(final EntityPlayer player, final SoundEffect sound) {
			super(new ResourceLocation(sound.sound));

			this.volume = sound.volume;
			this.pitch = sound.getPitch(RANDOM);
			this.repeat = false;
			this.repeatDelay = 0;

			this.xPosF = MathHelper
					.floor_double(player.posX + RANDOM.nextInt(SPOT_SOUND_RANGE) - RANDOM.nextInt(SPOT_SOUND_RANGE));
			this.yPosF = MathHelper.floor_double(
					player.posY + 1 + RANDOM.nextInt(SPOT_SOUND_RANGE) - RANDOM.nextInt(SPOT_SOUND_RANGE));
			this.zPosF = MathHelper
					.floor_double(player.posZ + RANDOM.nextInt(SPOT_SOUND_RANGE) - RANDOM.nextInt(SPOT_SOUND_RANGE));
		}

		@Override
		public float getVolume() {
			return super.getVolume() * MASTER_SCALE_FACTOR;
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

	private int currentSoundCount() {
		return Minecraft.getMinecraft().getSoundHandler().sndManager.playingSounds.size();
	}

	private int maxSoundCount() {
		return SoundSystemConfig.getNumberNormalChannels() + SoundSystemConfig.getNumberStreamingChannels();
	}

	private boolean canFitSound() {
		return currentSoundCount() < (maxSoundCount() - SOUND_QUEUE_SLACK);
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

	public static void playSoundAt(final BlockPos pos, final SoundEffect sound, final int tickDelay) {
		final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		final ISound s = new SpotSound(pos.getX(), pos.getY(), pos.getZ(), sound);

		if (tickDelay == 0)
			handler.playSound(s);
		else
			handler.playDelayedSound(s, tickDelay);
	}

	@Override
	public void process(final World world, final EntityPlayer player) {
		if (didReloadOccur() || player.isDead || playerDimension != EnvironState.getDimensionId())
			SoundManager.clearSounds();

		// Dead players hear no sounds
		if (player.isDead)
			return;

		playerDimension = EnvironState.getDimensionId();
		
		final BiomeGenBase playerBiome = EnvironState.getPlayerBiome();
		final String conditions = EnvironState.getConditions();

		final List<SoundEffect> sounds = new ArrayList<SoundEffect>();
		sounds.addAll(BiomeRegistry.getSounds(playerBiome, conditions));
		sounds.addAll(BiomeRegistry.getSounds(BiomeRegistry.PLAYER, conditions));

		SoundManager.update();
		SoundManager.queueSounds(sounds);

		SoundEffect sound = null;
		if (canFitSound()) {
			sound = BiomeRegistry.getSpotSound(playerBiome, conditions, RANDOM);
			if (sound != null)
				playSoundAtPlayer(player, sound, 0);
		}

		if (canFitSound()) {
			sound = BiomeRegistry.getSpotSound(BiomeRegistry.PLAYER, conditions, RANDOM);
			if (sound != null)
				playSoundAtPlayer(player, sound, 0);
		}
	}

	@Override
	public boolean hasEvents() {
		return true;
	}

	@SubscribeEvent
	public void diagnostics(final DiagnosticEvent.Gather event) {
		final StringBuilder builder = new StringBuilder();
		builder.append("SoundSystem: ").append(currentSoundCount()).append('/').append(maxSoundCount());
		event.output.add(builder.toString());
		for (final SoundEffect sound : SoundManager.activeSounds()) {
			builder.setLength(0);
			builder.append("Active Sound: ").append(sound.toString());
			builder.append(" (effective volume:").append(sound.getVolume() * MASTER_SCALE_FACTOR).append(')');
			event.output.add(builder.toString());
		}
	}

}
