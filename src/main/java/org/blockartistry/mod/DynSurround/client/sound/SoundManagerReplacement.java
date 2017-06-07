/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher, Abastro
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

package org.blockartistry.mod.DynSurround.client.sound;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Map.Entry;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.data.SoundRegistry;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.StreamThread;

@SideOnly(Side.CLIENT)
public class SoundManagerReplacement extends SoundManager {

	private static int normalChannelCount = 0;
	private static int streamChannelCount = 0;

	private static Field soundLibrary = null;
	private static Field streamThread = null;

	static {

		try {
			soundLibrary = ReflectionHelper.findField(SoundSystem.class, "soundLibrary");
			streamThread = ReflectionHelper.findField(Library.class, "streamThread");
		} catch (final Throwable t) {
			ModLog.warn("Cannot find sound manager fields; auto-restart not enabled");
			soundLibrary = null;
			streamThread = null;
		}

	}

	private final static int CHECK_INTERVAL = 30 * 20; // 30 seconds
	private int nextCheck = 0;

	public SoundManagerReplacement(final SoundHandler handler, final GameSettings settings) {
		super(handler, settings);
		MinecraftForge.EVENT_BUS.register(this);

		configureSound();
	}

	private void keepAlive() {
		if (!this.loaded || streamThread == null)
			return;

		// Don't want to spam attempts
		if (this.playTime < this.nextCheck)
			return;

		this.nextCheck = this.playTime + CHECK_INTERVAL;

		try {
			final Library l = (Library) soundLibrary.get(this.sndSystem);
			final StreamThread t = (StreamThread) streamThread.get(l);
			if (t != null && !t.isAlive()) {
				if (ModLog.DEBUGGING) {
					EnvironState.getPlayer().addChatMessage(new ChatComponentText("Autorestart of sound system!"));
				}
				ModLog.warn("Autorestart of sound system!");
				this.reloadSoundSystem();
			}
		} catch (final Throwable t) {
			;
		}
	}

	@Override
	public boolean isSoundPlaying(final ISound sound) {
		return sound == null ? false : super.isSoundPlaying(sound);
	}

	@Override
	public void stopSound(final ISound sound) {
		if (sound != null)
			super.stopSound(sound);
	}

	@Override
	public void playSound(final ISound sound) {
		if (sound != null)
			super.playSound(sound);
	}

	@Override
	public void addDelayedSound(final ISound sound, final int delay) {
		if (sound != null)
			super.addDelayedSound(sound, delay);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateAllSounds() {

		keepAlive();

		final SoundSystem sndSystem = this.sndSystem;

		++this.playTime;
		Iterator<?> iterator = this.tickableSounds.iterator();
		String s;

		while (iterator.hasNext()) {
			ITickableSound itickablesound = (ITickableSound) iterator.next();
			itickablesound.update();

			if (itickablesound.isDonePlaying()) {
				this.stopSound(itickablesound);
			} else {

				s = (String) this.invPlayingSounds.get(itickablesound);
				final SoundPoolEntry poolEntry = (SoundPoolEntry) this.playingSoundPoolEntries.get(itickablesound);

				if (poolEntry == null)
					this.stopSound(itickablesound);
				else
					synchronized (SoundSystemConfig.THREAD_SYNC) {
						sndSystem.setVolume(s, this.getNormalizedVolume(itickablesound, poolEntry, this.sndHandler
								.getSound(itickablesound.getPositionedSoundLocation()).getSoundCategory()));
						sndSystem.setPitch(s, this.getNormalizedPitch(itickablesound, poolEntry));
						sndSystem.setPosition(s, itickablesound.getXPosF(), itickablesound.getYPosF(),
								itickablesound.getZPosF());
					}
			}
		}

		iterator = this.playingSounds.entrySet().iterator();
		ISound isound;

		while (iterator.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
			s = (String) entry.getKey();
			isound = (ISound) entry.getValue();

			if (!sndSystem.playing(s)) {
				int i = ((Integer) this.playingSoundsStopTime.get(s)).intValue();

				if (i <= this.playTime) {
					int j = isound.getRepeatDelay();

					if (isound.canRepeat() && j > 0) {
						this.delayedSounds.put(isound, Integer.valueOf(this.playTime + j));
					}

					sndSystem.removeSource(s);
					iterator.remove();
					this.playingSoundsStopTime.remove(s);
					this.playingSoundPoolEntries.remove(isound);

					try {
						this.categorySounds.remove(
								this.sndHandler.getSound(isound.getPositionedSoundLocation()).getSoundCategory(), s);
					} catch (RuntimeException runtimeexception) {
						;
					}

					if (isound instanceof ITickableSound) {
						this.tickableSounds.remove(isound);
					}
				}
			}
		}

		Iterator<?> iterator1 = this.delayedSounds.entrySet().iterator();

		while (iterator1.hasNext()) {
			Entry<?, ?> entry1 = (Entry<?, ?>) iterator1.next();

			if (this.playTime >= ((Integer) entry1.getValue()).intValue()) {
				isound = (ISound) entry1.getKey();

				if (isound instanceof ITickableSound) {
					((ITickableSound) isound).update();
				}

				this.playSound(isound);
				iterator1.remove();
			}
		}
	}

	/**
	 * Normalizes pitch from parameters and clamps to [0.5, 2.0]
	 */
	public float getNormalizedPitch(final ISound sound, final SoundPoolEntry poolEntry) {
		float result = 1.0F;
		if (sound == null) {
			ModLog.warn("getNormalizedPitch(): Null sound parameter");
		} else if (poolEntry == null) {
			ModLog.warn("getNormalizedPitch(): Null poolEntry parameter");
			result = sound.getPitch();
		} else {
			result = (float) (sound.getPitch() * poolEntry.getPitch());
		}

		return (float) MathHelper.clamp_double(result, 0.5D, 2.0D);
	}

	@Override
	public float getNormalizedVolume(final ISound sound, final SoundPoolEntry poolEntry, final SoundCategory category) {
		float result = 0.0F;
		if (sound == null) {
			ModLog.warn("getNormalizedVolume(): Null sound parameter");
		} else if (poolEntry == null) {
			ModLog.warn("getNormalizedVolume(): Null poolEntry parameter");
		} else if (category == null) {
			ModLog.warn("getNormalizedVolume(): Null category parameter");
		} else {
			final String soundName = sound.getPositionedSoundLocation().toString();
			try {
				final float volumeScale = SoundRegistry.getVolumeScale(soundName);
				result = (float) MathHelper.clamp_double((double) sound.getVolume() * poolEntry.getVolume()
						* (double) getSoundCategoryVolume(category) * volumeScale, 0.0D, 1.0D);
			} catch (final Throwable t) {
				ModLog.error("getNormalizedVolume(): Unable to calculate " + soundName, t);
			}
		}
		return result;
	}

	public static void configureSound() {
		int totalChannels = -1;

		try {
			final boolean create = !AL.isCreated();
			if (create)
				AL.create();
			final IntBuffer ib = BufferUtils.createIntBuffer(1);
			ALC10.alcGetInteger(AL.getDevice(), ALC11.ALC_MONO_SOURCES, ib);
			totalChannels = ib.get(0);
			if (create)
				AL.destroy();
		} catch (final Throwable e) {
			e.printStackTrace();
		}

		normalChannelCount = ModOptions.normalSoundChannelCount;
		streamChannelCount = ModOptions.streamingSoundChannelCount;

		if (ModOptions.autoConfigureChannels && totalChannels > 64) {
			totalChannels = ((totalChannels + 1) * 3) / 4;
			streamChannelCount = totalChannels / 5;
			normalChannelCount = totalChannels - streamChannelCount;
		}

		ModLog.info("Sound channels: %d normal, %d streaming (total avail: %s)", normalChannelCount, streamChannelCount,
				totalChannels == -1 ? "UNKNOWN" : Integer.toString(totalChannels));
		SoundSystemConfig.setNumberNormalChannels(normalChannelCount);
		SoundSystemConfig.setNumberStreamingChannels(streamChannelCount);

	}

}
