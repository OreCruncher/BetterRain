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

package org.blockartistry.mod.BetterRain.client.fx.blocks;

import java.util.Random;

import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public abstract class SoundHandler {

	protected static final Random random = new XorShiftRandom();

	protected final int chance;
	protected final String sound;
	protected final float scale;
	protected final float volume;
	protected final float pitch;

	public SoundHandler(final int chance, final String sound, final float scale, final float volume, final float pitch) {
		this.chance = chance;
		this.sound = sound;
		this.scale = scale;
		this.volume = volume;
		this.pitch = pitch;
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

	public void doSound(final World world, final int x, final int y, final int z) {
		world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, this.sound, getVolume() * this.scale, getPitch(), false);
	}
};

