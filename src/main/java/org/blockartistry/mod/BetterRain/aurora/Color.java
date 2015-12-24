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

public final class Color {

	public static final Color RED = new Color(EnumColors.RED);
	public static final Color ORANGE = new Color(EnumColors.ORANGE);
	public static final Color YELLOW = new Color(EnumColors.YELLOW);
	public static final Color LGREEN = new Color(EnumColors.LGREEN);
	public static final Color GREEN = new Color(EnumColors.GREEN);
	public static final Color TURQOISE = new Color(EnumColors.TURQOISE);
	public static final Color CYAN = new Color(EnumColors.CYAN);
	public static final Color AUQUAMARINE = new Color(EnumColors.AUQUAMARINE);
	public static final Color BLUE = new Color(EnumColors.BLUE);
	public static final Color VIOLET = new Color(EnumColors.VIOLET);
	public static final Color MAGENTA = new Color(EnumColors.MAGENTA);
	public static final Color RASPBERRY = new Color(EnumColors.RASPBERRY);
	
	// http://www.rapidtables.com/web/color/RGB_Color.htm
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color PURPLE = new Color(80, 0, 80);
	public static final Color INDIGO = new Color(75, 0, 130);
	public static final Color NAVY = new Color(0, 0, 128);

	public int red;
	public int green;
	public int blue;
	public EnumColors lastColor = EnumColors.RED;

	public Color(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.lastColor = closestColor();
	}

	public Color(final EnumColors s) {
		this.red = s.red;
		this.green = s.green;
		this.blue = s.blue;
		this.lastColor = s;
	}

	public void setColor(final EnumColors c) {
		this.lastColor = c;
		this.red = c.red;
		this.green = c.green;
		this.blue = c.blue;
	}

	public void incRed(int i) {
		this.red += i;
	}

	public void incGreen(int i) {
		this.green += i;
	}

	public void incBlue(int i) {
		this.blue += i;
	}

	public boolean equals(final EnumColors c) {
		if ((c.red == this.red) && (c.green == this.green) && (this.blue == c.blue)) {
			this.lastColor = c;
			return true;
		}
		return false;
	}

	private int closestTo(final int i) {
		if ((i >= 0) && (i <= 63)) {
			return 0;
		}
		if ((i > 63) && (i <= 127)) {
			return 127;
		}

		return 255;
	}

	public EnumColors closestColor() {
		int red = closestTo(this.red);
		int green = closestTo(this.green);
		int blue = closestTo(this.blue);

		return (EnumColors) ColorShifter.instance.ColorMap.get(new int[] { red, green, blue });
	}
}
