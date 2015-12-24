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

package org.blockartistry.mod.BetterRain.aurora;

public final class Node {
	private float angle;
	public float posX = 1.0F;
	public float posZ = 1.0F;
	public float posY = 7.0F;
	private float modZ = 0.0F;
	private float modX = 0.0F;
	private float modY = 0.0F;
	private float tetX = 0.0F;
	private float tetX2 = 0.0F;
	private float tetZ = 0.0F;
	private float tetZ2 = 0.0F;
	public float width = 30.0F;
	public int alpha = 1;

	public Node(float x, float z, float theta) {
		this.posX = x;
		this.posZ = z;
		this.angle = theta;
	}

	public Node(float x, float y, float z, float theta) {
		this(x, z, theta);
		this.posY = y;
	}

	public void setTet(float x, float x2, float z, float z2) {
		this.tetX = x;
		this.tetX2 = x2;
		this.tetZ = z;
		this.tetZ2 = z2;
	}

	public float[] getTet() {
		return new float[] { this.tetX, this.tetX2, this.tetZ, this.tetZ2 };
	}
	
	public float[] getTet2() {
		return new float[] { this.posX, this.posX, getModdedZ(), getModdedZ() };
	}

	public void setModZ(float f) {
		this.modZ = f;
	}

	public void setModX(float f) {
		this.modX = f;
	}

	public void setModY(float f) {
		this.modY = f;
	}

	public float getModdedZ() {
		return this.posZ + this.modZ;
	}

	public float getModdedX() {
		return this.posX + this.modX;
	}

	public float getModdedY() {
		float y = this.posY + this.modY;
		if (y < 0.0F) {
			y = 0.0F;
		}
		return y;
	}

	public void setAngle(float f) {
		this.angle = f;
	}

	public float getAngle() {
		return this.angle;
	}

	public void addA(int a) {
		if (this.alpha >= 1) {
			this.alpha += a;
		}
	}
}
