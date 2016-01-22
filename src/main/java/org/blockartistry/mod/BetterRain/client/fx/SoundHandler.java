/*
 * This file is part of BetterRain, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.BetterRain.client.fx;

import java.util.Random;

import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public abstract class SoundHandler {

	protected static final Random random = new XorShiftRandom();

	protected final String sound;
	protected int chance;
	protected float scale;
	protected float volume;
	protected float pitch;

	public SoundHandler(final String sound) {
		this(100, sound);
	}

	public SoundHandler(final int chance, final String sound) {
		this(chance, sound, 1.0F, 1.0F, 1.0F);
	}

	public SoundHandler(final int chance, final String sound, final float scale, final float volume,
			final float pitch) {
		this.chance = chance;
		this.sound = sound;
		this.scale = scale;
		this.volume = volume;
		this.pitch = pitch;
	}

	public SoundHandler setChance(final int chance) {
		this.chance = chance;
		return this;
	}

	public SoundHandler setVolume(final float volume) {
		this.volume = volume;
		return this;
	}

	public SoundHandler setPitch(final float pitch) {
		this.pitch = pitch;
		return this;
	}

	public SoundHandler setScale(final float scale) {
		this.scale = scale;
		return this;
	}

	public boolean trigger() {
		return random.nextInt(chance) == 0;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getScale() {
		return this.scale;
	}

	public void doSound(final World world, final int x, final int y, final int z) {
		world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, this.sound, getVolume() * getScale(), getPitch(), false);
	}
};
