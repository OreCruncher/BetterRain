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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.fx.SoundEffect;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class SoundManager {

	private static final Map<SoundEffect, Emitter> emitters = new HashMap<SoundEffect, Emitter>();

	private static final Random RANDOM = new XorShiftRandom();
	private static final float VOLUME_INCREMENT = 0.02F;
	private static final float VOLUME_DECREMENT = 0.015F;
	private static final float MASTER_SCALE_FACTOR = ModOptions.getMasterSoundScaleFactor();

	private static class PlayerSound extends MovingSound {
		private boolean fadeAway;
		private final SoundEffect sound;

		public PlayerSound(final SoundEffect sound) {
			super(new ResourceLocation(sound.sound));

			// Don't set volume to 0; MC will optimize out
			this.sound = sound;
			this.volume = !sound.skipFade ? 0.01F : sound.volume;
			this.pitch = sound.getPitch(RANDOM);
			this.repeat = sound.repeatDelay == 0;
			this.fadeAway = false;

			// Repeat delay
			this.repeatDelay = 0;

			final EntityPlayer player = EnvironState.getPlayer();
			// Initial position
			this.xPosF = (float) (player.posX);
			this.yPosF = (float) (player.posY + 1);
			this.zPosF = (float) (player.posZ);
		}

		public void fadeAway() {
			this.fadeAway = true;
			if (this.sound.skipFade) {
				this.volume = 0.0F;
				this.donePlaying = true;
			}
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
			return MathHelper.floor_double(EnvironState.getPlayer().posX);
		}

		@Override
		public float getYPosF() {
			return MathHelper.floor_double(EnvironState.getPlayer().posY + 1);
		}

		@Override
		public float getZPosF() {
			return MathHelper.floor_double(EnvironState.getPlayer().posZ);
		}

		@Override
		public String toString() {
			return this.sound.toString();
		}

		@Override
		public boolean equals(final Object anObj) {
			if (this == anObj)
				return true;
			if (anObj instanceof PlayerSound)
				return this.sameSound(((PlayerSound) anObj).sound);
			if (anObj instanceof SoundEffect)
				return this.sameSound((SoundEffect) anObj);
			return false;
		}
	}

	private static class Emitter {

		protected final SoundEffect effect;
		protected PlayerSound activeSound;
		protected final SoundHandler handler;

		protected int repeatDelay = 0;

		public Emitter(final SoundEffect sound) {
			this.effect = sound;
			this.handler = Minecraft.getMinecraft().getSoundHandler();
		}

		public void update() {
			if (activeSound != null) {
				if (handler.isSoundPlaying(activeSound))
					return;
				activeSound.fadeAway();
				activeSound = null;
				if (this.effect.repeatDelay > 0) {
					this.repeatDelay = this.effect.repeatDelay;
					return;
				}
			} else if (this.repeatDelay > 0) {
				if (--this.repeatDelay > 0)
					return;
			}

			this.activeSound = new PlayerSound(effect);
			try {
				handler.playSound(this.activeSound);
			} catch (final Throwable t) {
				;
			}
		}

		public void fade() {
			if (this.activeSound != null)
				this.activeSound.fadeAway();
		}
	}

	public static void clearSounds() {
		for (final Emitter emit : emitters.values())
			emit.fade();
		emitters.clear();
	}

	public static void queueSounds(final List<SoundEffect> sounds) {
		// Need to remove sounds that are active but not
		// in the incoming list
		final List<SoundEffect> active = new ArrayList<SoundEffect>(emitters.keySet());
		for (final SoundEffect effect : active) {
			if (!sounds.remove(effect))
				emitters.remove(effect).fade();
		}

		// Add sounds from the incoming list that are not
		// active.
		for (final SoundEffect sound : sounds)
			emitters.put(sound, new Emitter(sound));
	}

	public static void update() {
		final Iterator<Entry<SoundEffect, Emitter>> itr = emitters.entrySet().iterator();
		while (itr.hasNext()) {
			final Entry<SoundEffect, Emitter> e = itr.next();
			e.getValue().update();
		}
	}

	public static List<SoundEffect> activeSounds() {
		return new ArrayList<SoundEffect>(emitters.keySet());
	}

}
