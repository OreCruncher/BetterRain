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

package org.blockartistry.mod.BetterRain.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Per world save data for rain status
 */
public final class RainData {
	
	public final static float MIN_STRENGTH = 0.0F;
	public final static float MAX_STRENGTH = 1.0F;

	private final class NBT {
		public final static String DIMENSION = "d";
		public final static String STRENGTH = "s";
	};

	private int dimensionId = 0;
	private float strength = 0.0F;

	public RainData() {
	}
	
	public RainData(final int dimensionId) {
		this.dimensionId = dimensionId;
		this.strength = 0.0F;
	}

	public int getDimensionId() {
		return dimensionId;
	}

	public float getRainStrength() {
		return this.strength;
	}

	public void setRainStrength(float strength) {
		this.strength = MathHelper.clamp_float(strength, MIN_STRENGTH, MAX_STRENGTH);
	}

	public void readFromNBT(final NBTTagCompound nbt) {
		this.dimensionId = nbt.getInteger(NBT.DIMENSION);
		setRainStrength(nbt.getFloat(NBT.STRENGTH));
	}

	public void writeToNBT(final NBTTagCompound nbt) {
		nbt.setInteger(NBT.DIMENSION, this.dimensionId);
		nbt.setFloat(NBT.STRENGTH, this.strength);
	}
	
	public static RainData get(final World world) {
		return RainDataFile.getRainData(world);
	}
}
