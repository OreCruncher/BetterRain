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
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
class PlayerSound extends MovingSound {
	
	private static final Random RANDOM = new XorShiftRandom();
	private static final float MASTER_SCALE_FACTOR = ModOptions.masterSoundScaleFactor;

	private final SoundEffect sound;

	public PlayerSound(final SoundEffect sound) {
		super(new ResourceLocation(sound.sound));

		// Don't set volume to 0; MC will optimize out
		this.sound = sound;
		this.volume = sound.volume;
		this.field_147663_c = sound.getPitch(RANDOM);
		this.repeat = sound.repeatDelay == 0;

		// Repeat delay
		this.field_147665_h = 0;

		final EntityPlayer player = EnvironState.getPlayer();
		// Initial position
		this.xPosF = (float) (player.posX);
		this.yPosF = (float) (player.posY + 1);
		this.zPosF = (float) (player.posZ);
	}

	public void fadeAway() {
		this.volume = 0.0F;
		this.donePlaying = true;
	}

	public boolean sameSound(final SoundEffect snd) {
		return this.sound.equals(snd);
	}

	@Override
	public void update() {
		if (this.volume == 0.0F)
			this.donePlaying = true;
	}

	@Override
	public float getVolume() {
		return super.getVolume() * MASTER_SCALE_FACTOR;
	}
	
	public void setVolume(final float volume) {
		this.volume = volume;
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

