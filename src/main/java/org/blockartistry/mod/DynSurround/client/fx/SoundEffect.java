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

package org.blockartistry.mod.DynSurround.client.fx;

import java.util.Random;

import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class SoundEffect extends BlockEffect {

	private static final float[] pitchDelta = { -0.2F, 0.0F, 0.0F, 0.2F, 0.2F, 0.2F };

	protected final String sound;
	protected float scale;
	protected float volume;
	protected float pitch;
	protected boolean variablePitch;

	public SoundEffect(final String sound) {
		this(100, sound);
	}

	public SoundEffect(final int chance, final String sound) {
		this(chance, sound, 1.0F, 1.0F, 1.0F, false);
	}

	public SoundEffect(final int chance, final String sound, final float scale, final float volume, final float pitch,
			final boolean variable) {
		super(chance);
		this.sound = sound;
		this.scale = scale;
		this.volume = volume;
		this.pitch = pitch;
		this.variablePitch = variable;
	}

	public void setVolume(final float volume) {
		this.volume = volume;
	}

	public void setPitch(final float pitch) {
		this.pitch = pitch;
	}

	public void setVariablePitch(final boolean flag) {
		this.variablePitch = flag;
	}

	public void setScale(final float scale) {
		this.scale = scale;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPitch(final Random rand) {
		if (rand != null && this.variablePitch)
			return this.pitch + pitchDelta[rand.nextInt(pitchDelta.length)];
		return this.pitch;
	}

	public float getScale() {
		return this.scale;
	}

	public void doEffect(final Block block, final World world, final int x, final int y, final int z,
			final Random random) {
		world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, this.sound, getVolume() * getScale(), getPitch(random), false);
	}
}
