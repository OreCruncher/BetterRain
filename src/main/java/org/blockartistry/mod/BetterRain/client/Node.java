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

package org.blockartistry.mod.BetterRain.client;

import org.blockartistry.mod.BetterRain.util.MathStuff;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Node {

	private static final float COS_DEG90_FACTOR = MathStuff.cos(MathStuff.PI_F / 2.0F);
	private static final float COS_DEG270_FACTOR = MathStuff.cos(MathStuff.PI_F / 2.0F + MathStuff.PI_F);
	private static final float SIN_DEG90_FACTOR = MathStuff.sin(MathStuff.PI_F / 2.0F);
	private static final float SIN_DEG270_FACTOR = MathStuff.sin(MathStuff.PI_F / 2.0F + MathStuff.PI_F);

	private float modZ = 0.0F;
	private float modY = 0.0F;

	private float cosDeg90 = 0.0F;
	private float cosDeg270 = 0.0F;
	private float sinDeg90 = 0.0F;
	private float sinDeg270 = 0.0F;

	public float angle;
	public float posX = 1.0F;
	public float posZ = 1.0F;
	public float posY = 7.0F;
	public float tetX = 0.0F;
	public float tetX2 = 0.0F;
	public float tetZ = 0.0F;
	public float tetZ2 = 0.0F;

	public Node(final float x, final float y, final float z, final float theta) {
		this.posX = x;
		this.posZ = z;
		this.angle = theta;
		this.posY = y;
	}

	public void setModZ(final float f) {
		this.modZ = f;
	}

	public void setModY(final float f) {
		this.modY = f;
	}

	public float getModdedZ() {
		return this.posZ + this.modZ;
	}

	public float getModdedY() {
		final float y = this.posY + this.modY;
		return y < 0.0F ? 0.0F : y;
	}

	public void setWidth(final float w) {
		this.cosDeg270 = COS_DEG270_FACTOR * w;
		this.cosDeg90 = COS_DEG90_FACTOR * w;
		this.sinDeg270 = SIN_DEG270_FACTOR * w;
		this.sinDeg90 = SIN_DEG90_FACTOR * w;
	}
	
	public void findAngles(final Node next) {
		this.tetX = this.tetX2 = this.posX;
		this.tetZ = this.tetZ2 = this.getModdedZ();
		this.angle = 0.0F;
		if (next != null) {
			this.angle = MathStuff.atan2(this.getModdedZ() - next.getModdedZ(), this.posX - next.posX);
			this.tetX += this.cosDeg90;
			this.tetX2 += this.cosDeg270;
			this.tetZ += this.sinDeg90;
			this.tetZ2 += this.sinDeg270;
		}
	}
}
