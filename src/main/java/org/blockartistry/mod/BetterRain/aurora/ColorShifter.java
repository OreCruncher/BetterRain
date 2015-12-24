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

import java.util.HashMap;
import java.util.Map;

public final class ColorShifter {
	public static final ColorShifter instance = new ColorShifter();
	public Map<int[], EnumColors> ColorMap = new HashMap<int[], EnumColors>();

	private ColorShifter() {
		for (final EnumColors color : EnumColors.values()) {
			this.ColorMap.put(color.rgbValues(), color);
		}
	}

	public static boolean shiftColor(final Color c, final EnumColors end) {
		if (c.red != end.red) {
			if (c.red < end.red) {
				c.incRed(1);
			} else {
				c.incRed(-1);
			}
		}
		if (c.green != end.green) {
			if (c.green < end.green) {
				c.incGreen(1);
			} else {
				c.incGreen(-1);
			}
		}
		if (c.blue != end.blue) {
			if (c.blue < end.blue) {
				c.incBlue(1);
			} else {
				c.incBlue(-1);
			}
		}
		if (c.equals(end)) {
			return true;
		}
		return false;
	}

	public static boolean shiftColorsRange(final Color c, final EnumColors end, final boolean direction) {
		if (c.lastColor == end) {
			return true;
		}
		final EnumColors targetColor = findNextColor(c, direction);
		if (targetColor != end) {
			return shiftColor(c, targetColor);
		}

		return shiftColor(c, end);
	}

	public static EnumColors findNextColor(final Color c, final boolean direction) {
		if (direction) {
			if (c.lastColor == EnumColors.RASPBERRY) {
				return EnumColors.RED;
			}
			return EnumColors.get(c.lastColor.ordinal() + 1);
		}

		if (c.lastColor == EnumColors.RED) {
			return EnumColors.RASPBERRY;
		}
		return EnumColors.get(c.lastColor.ordinal() - 1);
	}
}