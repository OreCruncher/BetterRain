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

package org.blockartistry.mod.BetterRain.util;

/**
 * Holds an RGB triple. See: http://www.rapidtables.com/web/color/RGB_Color.htm
 */
public final class Color {

	public static final Color RED = new Color(255, 0, 0);
	public static final Color ORANGE = new Color(255, 127, 0);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color LGREEN = new Color(127, 255, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color TURQOISE = new Color(0, 255, 127);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color AUQUAMARINE = new Color(0, 127, 255);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color VIOLET = new Color(127, 0, 255);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color RASPBERRY = new Color(255, 0, 127);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color PURPLE = new Color(80, 0, 80);
	public static final Color INDIGO = new Color(75, 0, 130);
	public static final Color NAVY = new Color(0, 0, 128);

	public final int red;
	public final int green;
	public final int blue;

	public Color(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
}
