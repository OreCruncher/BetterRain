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

import java.text.DecimalFormat;
import java.util.Random;

import org.blockartistry.mod.BetterRain.ModOptions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Per world save data for rain status
 */
public final class RainData {

	private static final Random random = new Random();
	private static final DecimalFormat FORMATTER = new DecimalFormat("0");

	public final static float MIN_INTENSITY = 0.0F;
	public final static float MAX_INTENSITY = 1.0F;

	private final class NBT {
		public final static String DIMENSION = "d";
		public final static String INTENSITY = "s";
		public final static String MIN_INTENSITY = "min";
		public final static String MAX_INTENSITY = "max";
	};

	private int dimensionId = 0;
	private float intensity = 0.0F;
	private float minIntensity = ModOptions.getDefaultMinRainIntensity();
	private float maxIntensity = ModOptions.getDefaultMaxRainIntensity();

	public RainData() {
	}

	public RainData(final int dimensionId) {
		this.dimensionId = dimensionId;
	}

	public int getDimensionId() {
		return dimensionId;
	}

	public float getRainIntensity() {
		return this.intensity;
	}

	public void setRainIntensity(final float intensity) {
		this.intensity = MathHelper.clamp_float(intensity, MIN_INTENSITY, MAX_INTENSITY);
	}

	public float getMinRainIntensity() {
		return this.minIntensity;
	}
	
	public void setMinRainIntensity(final float intensity) {
		this.minIntensity = MathHelper.clamp_float(intensity, MIN_INTENSITY, this.maxIntensity);
	}

	public float getMaxRainIntensity() {
		return this.maxIntensity;
	}
	
	public void setMaxRainIntensity(final float intensity) {
		this.maxIntensity = MathHelper.clamp_float(intensity, this.minIntensity, MAX_INTENSITY);
	}

	public void randomize() {
		setRainIntensity(MathHelper.randomFloatClamp(random, this.minIntensity, this.maxIntensity));
	}

	public void readFromNBT(final NBTTagCompound nbt) {
		this.dimensionId = nbt.getInteger(NBT.DIMENSION);
		this.intensity = MathHelper.clamp_float(nbt.getFloat(NBT.INTENSITY), MIN_INTENSITY, MAX_INTENSITY);
		if (nbt.hasKey(NBT.MIN_INTENSITY))
			this.minIntensity = MathHelper.clamp_float(nbt.getFloat(NBT.MIN_INTENSITY), MIN_INTENSITY, MAX_INTENSITY);
		if (nbt.hasKey(NBT.MAX_INTENSITY))
			this.maxIntensity = MathHelper.clamp_float(nbt.getFloat(NBT.MAX_INTENSITY), this.minIntensity,
					MAX_INTENSITY);
	}

	public void writeToNBT(final NBTTagCompound nbt) {
		nbt.setInteger(NBT.DIMENSION, this.dimensionId);
		nbt.setFloat(NBT.INTENSITY, this.intensity);
		nbt.setFloat(NBT.MIN_INTENSITY, this.minIntensity);
		nbt.setFloat(NBT.MAX_INTENSITY, this.maxIntensity);
	}

	public static RainData get(final World world) {
		return RainDataFile.get(world);
	}

	@Override
	public String toString() {
		// Dump out some diagnostics for the current dimension
		final StringBuilder builder = new StringBuilder();
		builder.append("dim ").append(this.dimensionId).append(": ");
		builder.append("intensity: ").append(FORMATTER.format(this.intensity * 100));
		builder.append(" [").append(FORMATTER.format(this.minIntensity * 100));
		builder.append(",").append(FORMATTER.format(this.maxIntensity * 100));
		builder.append("]");
		return builder.toString();
	}
}
