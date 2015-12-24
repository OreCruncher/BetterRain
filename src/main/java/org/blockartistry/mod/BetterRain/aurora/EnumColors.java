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

import gnu.trove.map.hash.TIntObjectHashMap;

public enum EnumColors {
	RED(255, 0, 0), ORANGE(255, 127, 0), YELLOW(255, 255, 0), LGREEN(127, 255, 0), GREEN(0, 255, 0), TURQOISE(0, 255,
			127), CYAN(0, 255, 255), AUQUAMARINE(0, 127, 255), BLUE(0, 0, 255), VIOLET(127, 0, 255), MAGENTA(255, 0,
					255), RASPBERRY(255, 0, 127);

	public final int red;
	public final int green;
	public final int blue;
	public final Color color;

	private static final TIntObjectHashMap<EnumColors> lookup = new TIntObjectHashMap<EnumColors>();

	static {
		for (final EnumColors c : EnumColors.values())
			lookup.put(c.ordinal(), c);
	}

	private EnumColors(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.color = new Color(this);
	}

	public int[] rgbValues() {
		return new int[] { this.red, this.green, this.blue };
	}

	public int getRGB() {
		return this.red << 16 | this.blue << 8 | this.green;
	}

	public Color asColor() {
		return this.color;
	}

	public static EnumColors get(final int i) {
		return lookup.get(i);
	}
}
