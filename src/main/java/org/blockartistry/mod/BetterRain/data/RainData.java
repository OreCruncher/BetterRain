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

import org.blockartistry.mod.BetterRain.BetterRain;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/**
 * Per world save data for rain status
 */
public final class RainData extends WorldSavedData {

	public final static float MIN_STRENGTH = 0.0F;
	public final static float MAX_STRENGTH = 1.0F;

	private final static String IDENTIFIER = BetterRain.MOD_ID;

	private final class NBT {
		public final static String STRENGTH = "s";
	};

	public RainData() {
		this(IDENTIFIER);
	}

	public RainData(final String id) {
		super(id);
	}

	private float strength = 0.0F;

	public static RainData get(final World world) {
		RainData data = (RainData) world.loadItemData(RainData.class, IDENTIFIER);
		if (data == null) {
			data = new RainData();
			world.setItemData(IDENTIFIER, data);
		}
		return data;
	}

	public float getRainStrength() {
		return this.strength;
	}

	public void setRainStrength(float strength) {
		strength = MathHelper.clamp_float(strength, MIN_STRENGTH, MAX_STRENGTH);
		if (this.strength != strength) {
			this.strength = strength;
			this.markDirty();
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		this.strength = MathHelper.clamp_float(nbt.getFloat(NBT.STRENGTH), MIN_STRENGTH, MAX_STRENGTH);
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		nbt.setFloat(NBT.STRENGTH, this.strength);
	}
}
