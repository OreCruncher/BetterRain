/* This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.DynSurround.client.sound;

import java.util.Random;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.util.MyUtils;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
class PlayerSound extends MovingSound {

	private static final float DONE_VOLUME_THRESHOLD = 0.001F;
	private static final float FADE_AMOUNT = 0.015F;
	private static final Random RANDOM = new XorShiftRandom();

	private final SoundEffect sound;
	private boolean isFading;
	private float maxVolume;
	private boolean isDonePlaying;
	private long lastTick;

	public PlayerSound(final SoundEffect sound) {
		super(new ResourceLocation(sound.sound));

		// Don't set volume to 0; MC will optimize out
		this.sound = sound;
		this.volume = sound.volume;
		this.maxVolume = sound.getVolume();
		this.volume = DONE_VOLUME_THRESHOLD * 2;
		this.field_147663_c = sound.getPitch(RANDOM);
		this.repeat = sound.repeatDelay == 0;

		// Repeat delay
		this.field_147665_h = 0;
		
		this.lastTick = EnvironState.getTickCounter() - 1;

		// No attenuation for sounds attached to the player
		this.field_147666_i = ISound.AttenuationType.NONE;

		updateLocation();
	}

	public void fadeAway() {
		this.isFading = true;
	}
	
	public boolean isFading() {
		return this.isFading;
	}

	public boolean isDonePlaying() {
		return this.isDonePlaying;
	}

	public boolean sameSound(final SoundEffect snd) {
		return this.sound.equals(snd);
	}

	public void updateLocation() {
		final AxisAlignedBB box = EnvironState.getPlayer().boundingBox;
		final Vec3 point = MyUtils.getCenter(box);
		this.xPosF = (float) point.xCoord;
		this.yPosF = (float) box.minY;
		this.zPosF = (float) point.zCoord;
	}

	@Override
	public void update() {
		if (this.isDonePlaying())
			return;

		if (!EnvironState.getPlayer().isEntityAlive()) {
			this.isDonePlaying = true;
			return;
		}

		final long tickDelta = EnvironState.getTickCounter() - this.lastTick;
		if(tickDelta == 0)
			return;
		
		this.lastTick = EnvironState.getTickCounter();
		
		if (this.isFading()) {
			this.volume -= FADE_AMOUNT * tickDelta;
		} else if (this.volume < this.maxVolume) {
			this.volume += FADE_AMOUNT * tickDelta;
		}
		
		if (this.volume > this.maxVolume) {
			this.volume = this.maxVolume;
		}

		if (this.volume <= DONE_VOLUME_THRESHOLD) {
			// Make sure the volume is 0 so a repeating
			// sound won't make a last gasp in the sound
			// engine.
			this.isDonePlaying = true;
			this.volume = 0.0F;
		} else {
			updateLocation();
		}
	}

	@Override
	public float getVolume() {
		return super.getVolume() * ModOptions.masterSoundScaleFactor;
	}

	public void setVolume(final float volume) {
		if (volume < this.maxVolume || !this.isFading)
			this.maxVolume = volume;
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
