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

import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.Map.Entry;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.data.SoundRegistry;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

@SideOnly(Side.CLIENT)
public class SoundManagerReplacement extends SoundManager {

	private static int normalChannelCount = 0;
	private static int streamChannelCount = 0;

	// Guard for concurrency challenges
	private final Object mutext = new Object();

	public SoundManagerReplacement(final SoundHandler handler, final GameSettings settings) {
		super(handler, settings);
		MinecraftForge.EVENT_BUS.register(this);

		configureSound();
	}

	@Override
	public void reloadSoundSystem() {
		synchronized (this.mutext) {
			super.reloadSoundSystem();
		}
	}

	@Override
	public void setSoundCategoryVolume(SoundCategory p_148601_1_, float p_148601_2_) {
		synchronized (this.mutext) {
			super.setSoundCategoryVolume(p_148601_1_, p_148601_2_);
		}
	}

	@Override
	public void unloadSoundSystem() {
		synchronized (this.mutext) {
			super.unloadSoundSystem();
		}
	}

	@Override
	public void stopAllSounds() {
		synchronized (this.mutext) {
			super.stopAllSounds();
		}
	}

	@Override
	public boolean isSoundPlaying(final ISound sound) {
		if (sound == null)
			return false;

		try {
			synchronized (this.mutext) {
				return super.isSoundPlaying(sound);
			}
		} catch (final Throwable t) {
			// Bad mod behavior
			;
		}
		return false;
	}

	@Override
	public void stopSound(final ISound sound) {
		if (sound != null)
			try {
				synchronized (this.mutext) {
					super.stopSound(sound);
				}
			} catch (final Throwable t) {
				// Bad mod behavior
				;
			}
	}

	@Override
	public void playSound(final ISound sound) {
		if (sound != null)
			try {
				synchronized (this.mutext) {
					super.playSound(sound);
					final SoundSystem sndSystem = this.sndSystem;
					// Flush the sounds
					sndSystem.CommandQueue(null);
				}
			} catch (final Throwable t) {
				// Bad mod behavior
				;
			}
	}

	@Override
	public void addDelayedSound(final ISound sound, final int delay) {
		if (sound != null)
			try {
				synchronized (this.mutext) {
					super.addDelayedSound(sound, delay);
				}
			} catch (final Throwable t) {
				// Bad mod behavior
				;
			}
	}

	@Override
	public void setListener(EntityPlayer p_148615_1_, float p_148615_2_) {
		synchronized (this.mutext) {
			super.setListener(p_148615_1_, p_148615_2_);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateAllSounds() {

		synchronized (this.mutext) {
			final SoundSystem sndSystem = this.sndSystem;

			++this.playTime;
			Iterator<?> iterator = this.tickableSounds.iterator();
			String s;

			while (iterator.hasNext()) {
				final ITickableSound itickablesound = (ITickableSound) iterator.next();
				itickablesound.update();

				if (itickablesound.isDonePlaying()) {
					stopSound(itickablesound);
				} else {

					s = (String) this.invPlayingSounds.get(itickablesound);
					final SoundPoolEntry poolEntry = (SoundPoolEntry) this.playingSoundPoolEntries.get(itickablesound);

					if (poolEntry == null)
						stopSound(itickablesound);
					else
						synchronized (SoundSystemConfig.THREAD_SYNC) {
							sndSystem.setVolume(s, getNormalizedVolume(itickablesound, poolEntry, this.sndHandler
									.getSound(itickablesound.getPositionedSoundLocation()).getSoundCategory()));
							sndSystem.setPitch(s, getNormalizedPitch(itickablesound, poolEntry));
							sndSystem.setPosition(s, itickablesound.getXPosF(), itickablesound.getYPosF(),
									itickablesound.getZPosF());
						}
				}
			}

			iterator = this.playingSounds.entrySet().iterator();
			ISound isound;

			while (iterator.hasNext()) {
				final Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
				s = (String) entry.getKey();
				isound = (ISound) entry.getValue();

				if (!sndSystem.playing(s)) {
					final int i = ((Integer) this.playingSoundsStopTime.get(s)).intValue();

					if (i <= this.playTime) {
						final int j = isound.getRepeatDelay();

						if (isound.canRepeat() && j > 0) {
							this.delayedSounds.put(isound, Integer.valueOf(this.playTime + j));
						}

						sndSystem.removeSource(s);
						iterator.remove();
						this.playingSoundsStopTime.remove(s);
						this.playingSoundPoolEntries.remove(isound);

						try {
							this.categorySounds.remove(
									this.sndHandler.getSound(isound.getPositionedSoundLocation()).getSoundCategory(),
									s);
						} catch (final RuntimeException runtimeexception) {
							;
						}

						if (isound instanceof ITickableSound) {
							this.tickableSounds.remove(isound);
						}
					}
				}
			}

			final Iterator<?> iterator1 = this.delayedSounds.entrySet().iterator();

			while (iterator1.hasNext()) {
				final Entry<?, ?> entry1 = (Entry<?, ?>) iterator1.next();

				if (this.playTime >= ((Integer) entry1.getValue()).intValue()) {
					isound = (ISound) entry1.getKey();

					if (isound instanceof ITickableSound) {
						((ITickableSound) isound).update();
					}

					playSound(isound);
					iterator1.remove();
				}
			}
		}
	}

	@Override
	public void pauseAllSounds() {
		synchronized (this.mutext) {
			super.pauseAllSounds();
		}
	}

	@Override
	public void resumeAllSounds() {
		synchronized (this.mutext) {
			super.resumeAllSounds();
		}
	}

	/**
	 * Normalizes pitch from parameters and clamps to [0.5, 2.0]
	 */
	@Override
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
				result = (float) MathHelper.clamp_double(
						sound.getVolume() * poolEntry.getVolume() * getSoundCategoryVolume(category) * volumeScale,
						0.0D, 1.0D);
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
