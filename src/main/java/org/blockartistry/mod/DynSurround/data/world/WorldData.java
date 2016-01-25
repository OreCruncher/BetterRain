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

package org.blockartistry.mod.DynSurround.data.world;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.util.MyUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public final class WorldData {

	private static boolean useCelestial = true;
	private static final TIntObjectHashMap<WorldData> dimensionData = new TIntObjectHashMap<WorldData>();
	private static final IDiurnalProvider DIURNAL = useCelestial ? new CelestialProvider() : new WallClockProvider();

	protected final int dimensionId;
	protected boolean initialized;
	protected Integer seaLevel;
	protected Integer skyHeight;
	protected Integer cloudHeight;
	protected Boolean hasSky;
	protected Boolean hasAuroras;

	public static void initialize() {

		// Initialize data from the config file
		for (final String entry : ModOptions.getElevationOverrides()) {
			final int[] values = MyUtils.splitToInts(entry, ',');
			if (values.length == 3) {
				final WorldData data = getData(values[0]);
				data.seaLevel = values[1];
				data.skyHeight = values[2];
				data.cloudHeight = data.skyHeight / 2;
			}
		}
	}

	protected WorldData(final int dimensionId) {
		this.dimensionId = dimensionId;
	}

	protected WorldData(final World world) {
		this.dimensionId = world.provider.dimensionId;
		initialize(world.provider);
	}

	protected WorldData initialize(final WorldProvider provider) {
		if (!this.initialized) {
			if (this.seaLevel == null)
				this.seaLevel = provider.getAverageGroundLevel();
			if (this.skyHeight == null)
				this.skyHeight = provider.getHeight();
			if (this.hasSky == null)
				this.hasSky = !provider.hasNoSky;
			if (this.hasAuroras == null)
				this.hasAuroras = !provider.hasNoSky;
			if (this.cloudHeight == null)
				this.cloudHeight = this.hasSky ? this.skyHeight / 2 : this.skyHeight;
			this.initialized = true;
		}
		return this;
	}

	public int getDimensionId() {
		return this.dimensionId;
	}

	public int getSeaLevel() {
		return this.seaLevel.intValue();
	}

	public int getSkyHeight() {
		return this.skyHeight.intValue();
	}

	public int getCloudHeight() {
		return this.cloudHeight.intValue();
	}

	public boolean getHasSky() {
		return this.hasSky.booleanValue();
	}

	public boolean getHasAuroras() {
		return this.hasAuroras.booleanValue();
	}

	protected static WorldData getData(final int dimensionId) {
		WorldData data = dimensionData.get(dimensionId);
		if (data == null) {
			data = new WorldData(dimensionId);
			dimensionData.put(dimensionId, data);
		}
		return data;
	}

	public static WorldData getData(final World world) {
		WorldData data = dimensionData.get(world.provider.dimensionId);
		if (data == null) {
			data = new WorldData(world);
			dimensionData.put(world.provider.dimensionId, data);
		} else {
			data.initialize(world.provider);
		}
		return data;
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
		return getData(world).getHasSky();
	}

	public static int getSeaLevel(final World world) {
		return getData(world).getSeaLevel();
	}

	public static int getSkyHeight(final World world) {
		return getData(world).getSkyHeight();
	}

	public static int getCloudHeight(final World world) {
		return getData(world).getCloudHeight();
	}

	public static boolean hasAuroras(final World world) {
		return getData(world).getHasAuroras();
	}

	public static boolean isDaytime(final World world) {
		return DIURNAL.isDaytime(world);
	}

	public static boolean isNighttime(final World world) {
		return DIURNAL.isNighttime(world);
	}

	public static boolean isSunrise(final World world) {
		return DIURNAL.isSunrise(world);
	}
}
