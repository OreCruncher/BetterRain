/* This file is part of BetterRain, licensed under the MIT License (MIT).
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

import javax.annotation.Nonnull;

import org.blockartistry.mod.BetterRain.ModOptions;

import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.world.World;

public final class WorldUtils {

	private static final TIntIntHashMap seaLevelOverride = new TIntIntHashMap();
	private static final TIntIntHashMap skyHeightOverride = new TIntIntHashMap();

	static {
		for (final String entry : ModOptions.getElevationOverrides()) {
			final int[] values = MyUtils.splitToInts(entry, ',');
			if (values.length == 3) {
				seaLevelOverride.put(values[0], values[1]);
				skyHeightOverride.put(values[0], values[2]);
			}
		}
	}

	private WorldUtils() {
	}

	public static long getWorldTime(@Nonnull final World world) {
		long time = world.getWorldTime();
		for (; time > 24000L; time -= 24000L)
			;
		return time;
	}

	public static boolean isDaytime(@Nonnull final World world) {
		return !isNighttime(world);
	}

	public static boolean isDaytime(final long time) {
		return !isNighttime(time);
	}

	public static boolean isNighttime(@Nonnull final World world) {
		return isNighttime(getWorldTime(world));
	}

	public static boolean isNighttime(final long time) {
		return time >= 13000L && time <= 23500L;
	}

	public static boolean hasSky(@Nonnull final World world) {
		return !world.provider.getHasNoSky();
	}

	public static boolean isDusk(@Nonnull final World world) {
		return isSunset(world);
	}

	public static boolean isSunset(@Nonnull final World world) {
		final long time = getWorldTime(world);
		return time >= 12000 && time < 14000;
	}

	public static boolean isDawn(@Nonnull final World world) {
		return isSunrise(world);
	}

	public static boolean isSunrise(@Nonnull final World world) {
		final long time = getWorldTime(world);
		return time >= 0 && time < 1000;
	}

	public static int getSeaLevel(@Nonnull final World world) {
		final int dimId = world.provider.getDimensionId();
		if (seaLevelOverride.contains(dimId))
			return seaLevelOverride.get(dimId);
		return world.provider.getAverageGroundLevel();
	}

	public static int getSkyHeight(@Nonnull final World world) {
		final int dimId = world.provider.getDimensionId();
		if (skyHeightOverride.contains(dimId))
			return skyHeightOverride.get(dimId);
		return world.provider.getHeight();
	}

	public static int getCloudHeight(@Nonnull final World world) {
		int sky = getSkyHeight(world);
		if (hasSky(world))
			sky /= 2;
		return sky;
	}
}
