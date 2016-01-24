/* This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.DynSurround.util;

import org.blockartistry.mod.DynSurround.ModOptions;

import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.world.World;

public final class WorldUtils {

	private static boolean useCelestial = true;

	private static interface IDiurnalProvider {
		boolean isDaytime(final World world);

		boolean isNighttime(final World world);

		boolean isSunrise(final World world);

		boolean isSunset(final World world);
	}

	private static class CelestialProvider implements IDiurnalProvider {

		@Override
		public boolean isDaytime(final World world) {
			final float celestialAngle = getCelestialAngle(world, 0.0F);
			// 0.785 0.260
			return celestialAngle >= 0.785F || celestialAngle < 0.285F;
		}

		@Override
		public boolean isNighttime(final World world) {
			final float celestialAngle = getCelestialAngle(world, 0.0F);
			// 0.260 0.705
			return celestialAngle >= 0.285F && celestialAngle < 0.701F;
		}

		@Override
		public boolean isSunrise(final World world) {
			final float celestialAngle = getCelestialAngle(world, 0.0F);
			// 0.705
			return celestialAngle >= 0.701F && celestialAngle < 0.785F;
		}

		@Override
		public boolean isSunset(final World world) {
			final float celestialAngle = getCelestialAngle(world, 0.0F);
			return celestialAngle > 0.215 && celestialAngle <= 0.306F;
		}
	}

	private static class WallClockProvider implements IDiurnalProvider {
		@Override
		public boolean isDaytime(final World world) {
			final long time = getClockTime(world);
			return time < 13000;
		}

		@Override
		public boolean isNighttime(final World world) {
			final long time = getClockTime(world);
			return time >= 13000 && time < 22220L;
		}

		@Override
		public boolean isSunrise(final World world) {
			final long time = getClockTime(world);
			return time >= 22220L;
		}

		@Override
		public boolean isSunset(final World world) {
			final long time = getClockTime(world);
			return time >= 12000 && time < 14000;
		}
	}

	private static final TIntIntHashMap seaLevelOverride = new TIntIntHashMap();
	private static final TIntIntHashMap skyHeightOverride = new TIntIntHashMap();
	private static final IDiurnalProvider diurnalProvider;

	static {
		for (final String entry : ModOptions.getElevationOverrides()) {
			final int[] values = MyUtils.splitToInts(entry, ',');
			if (values.length == 3) {
				seaLevelOverride.put(values[0], values[1]);
				skyHeightOverride.put(values[0], values[2]);
			}
		}

		diurnalProvider = useCelestial ? new CelestialProvider() : new WallClockProvider();
	}

	private WorldUtils() {
	}

	public static float getCelestialAngle(final World world, final float partialTickTime) {
		final float angle = world.getCelestialAngle(partialTickTime);
		return angle >= 1.0F ? angle - 1.0F : angle;
	}

	public static float getMoonPhaseFactor(final World world) {
		return world.getCurrentMoonPhaseFactor();
	}

	public static long getClockTime(final World world) {
		return world.getWorldTime() % 24000L;
	}

	public static boolean hasSky(final World world) {
		return !world.provider.getHasNoSky();
	}

	public static int getSeaLevel(final World world) {
		final int dimId = world.provider.getDimensionId();
		if (seaLevelOverride.contains(dimId))
			return seaLevelOverride.get(dimId);
		return world.provider.getAverageGroundLevel();
	}

	public static int getSkyHeight(final World world) {
		final int dimId = world.provider.getDimensionId();
		if (skyHeightOverride.contains(dimId))
			return skyHeightOverride.get(dimId);
		return world.provider.getHeight();
	}

	public static int getCloudHeight(final World world) {
		final int sky = getSkyHeight(world);
		return hasSky(world) ? sky / 2 : sky;
	}

	public static boolean isDaytime(final World world) {
		return diurnalProvider.isDaytime(world);
	}

	public static boolean isNighttime(final World world) {
		return diurnalProvider.isNighttime(world);
	}

	public static boolean isSunrise(final World world) {
		return diurnalProvider.isSunrise(world);
	}
}