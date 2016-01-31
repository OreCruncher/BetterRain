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

package org.blockartistry.mod.DynSurround.data;

import java.io.File;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.data.config.DimensionConfig;
import org.blockartistry.mod.DynSurround.util.DiurnalUtils;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;

public final class DimensionRegistry {

	private static final TIntObjectHashMap<DimensionRegistry> dimensionData = new TIntObjectHashMap<DimensionRegistry>();
	private static boolean isFlatWorld = false;

	protected final int dimensionId;
	protected boolean initialized;
	protected String name = "<NOT SET>";
	protected Integer seaLevel;
	protected Integer skyHeight;
	protected Integer cloudHeight;
	protected Boolean hasHaze;
	protected Boolean hasAuroras;
	protected Boolean hasWeather;

	public static void initialize() {
		try {
			process(DimensionConfig.load("dimensions"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (final String file : ModOptions.getDimensionConfigFiles()) {
			final File theFile = new File(Module.dataDirectory(), file);
			if (theFile.exists()) {
				try {
					final DimensionConfig config = DimensionConfig.load(theFile);
					if (config != null)
						process(config);
					else
						ModLog.warn("Unable to process dimension config file " + file);
				} catch (final Exception ex) {
					ModLog.error("Unable to process dimension config file " + file, ex);
				}
			} else {
				ModLog.warn("Could not locate dimension config file [%s]", file);
			}
		}

		ModLog.info("*** DIMENSION REGISTRY (delay init) ***");
		for (final DimensionRegistry reg : dimensionData.valueCollection())
			ModLog.info(reg.toString());
	}

	public static void loading(final World world) {
		getData(world).initialize(world.provider);
		if (world.provider.getDimensionId() == 0) {
			isFlatWorld = world.getWorldInfo().getTerrainType() == WorldType.FLAT;
		}
	}

	private static void process(final DimensionConfig config) {
		for (final DimensionConfig.Entry entry : config.entries) {
			if (entry.dimensionId != null) {
				final DimensionRegistry data = getData(entry.dimensionId);
				if (entry.hasAurora != null)
					data.hasAuroras = entry.hasAurora;
				if (entry.hasHaze != null)
					data.hasHaze = entry.hasHaze;
				if (entry.hasWeather != null)
					data.hasWeather = entry.hasWeather;
				if (entry.cloudHeight != null)
					data.cloudHeight = entry.cloudHeight;
				if (entry.seaLevel != null)
					data.seaLevel = entry.seaLevel;
				if (entry.skyHeight != null)
					data.skyHeight = entry.skyHeight;
			}
		}
	}

	protected DimensionRegistry(final int dimensionId) {
		this.dimensionId = dimensionId;
	}

	protected DimensionRegistry(final World world) {
		this.dimensionId = world.provider.getDimensionId();
		initialize(world.provider);
	}

	protected DimensionRegistry initialize(final WorldProvider provider) {
		if (!this.initialized) {
			this.name = provider.getDimensionName();
			if (this.seaLevel == null)
				this.seaLevel = provider.getAverageGroundLevel();
			if (this.skyHeight == null)
				this.skyHeight = provider.getHeight();
			if (this.hasHaze == null)
				this.hasHaze = !provider.getHasNoSky();
			if (this.hasAuroras == null)
				this.hasAuroras = !provider.getHasNoSky();
			if (this.hasWeather == null)
				this.hasWeather = !provider.getHasNoSky();
			if (this.cloudHeight == null)
				this.cloudHeight = this.hasHaze ? this.skyHeight / 2 : this.skyHeight;
			this.initialized = true;
			ModLog.info("Dimension initialized " + this.toString());
		}
		return this;
	}

	public int getDimensionId() {
		return this.dimensionId;
	}

	public String getName() {
		return this.name;
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

	public boolean getHasHaze() {
		return this.hasHaze.booleanValue();
	}

	public boolean getHasAuroras() {
		return this.hasAuroras.booleanValue();
	}

	public boolean getHasWeather() {
		return this.hasWeather.booleanValue();
	}

	protected static DimensionRegistry getData(final int dimensionId) {
		DimensionRegistry data = dimensionData.get(dimensionId);
		if (data == null) {
			data = new DimensionRegistry(dimensionId);
			dimensionData.put(dimensionId, data);
		}
		return data;
	}

	public static DimensionRegistry getData(final World world) {
		DimensionRegistry data = dimensionData.get(world.provider.getDimensionId());
		if (data == null) {
			data = new DimensionRegistry(world);
			dimensionData.put(world.provider.getDimensionId(), data);
		} else {
			data.initialize(world.provider);
		}
		return data;
	}

	public static boolean hasHaze(final World world) {
		return getData(world).getHasHaze();
	}

	public static int getSeaLevel(final World world) {
		if (world.provider.getDimensionId() == 0 && isFlatWorld)
			return 0;
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

	public static boolean hasWeather(final World world) {
		return getData(world).getHasWeather();
	}

	private static final String CONDITION_TOKEN_RAINING = "raining";
	private static final String CONDITION_TOKEN_DAY = "day";
	private static final String CONDITION_TOKEN_NIGHT = "night";
	private static final String CONDITION_TOKEN_NETHER = "nether";
	private static final String CONDITION_TOKEN_END = "end";
	private static final String CONDITION_TOKEN_SKY = "sky";

	public static String getConditions(final World world) {
		final StringBuilder builder = new StringBuilder();
		if (DiurnalUtils.isDaytime(world))
			builder.append(CONDITION_TOKEN_DAY);
		else
			builder.append(CONDITION_TOKEN_NIGHT);
		if (world.getRainStrength(1.0F) > 0.0F)
			builder.append(CONDITION_TOKEN_RAINING);
		if (world.provider.getDimensionId() == -1)
			builder.append(CONDITION_TOKEN_NETHER);
		if (world.provider.getDimensionId() == 1)
			builder.append(CONDITION_TOKEN_END);
		if (DimensionRegistry.hasHaze(world))
			builder.append(CONDITION_TOKEN_SKY);
		return builder.toString();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(this.dimensionId).append('/').append(this.name).append(':');
		builder.append(" seaLevel:").append(this.seaLevel);
		builder.append(" cloudH:").append(this.cloudHeight);
		builder.append(" skyH:").append(this.skyHeight);
		builder.append(" haze:").append(Boolean.toString(this.hasHaze));
		builder.append(" aurora:").append(Boolean.toString(this.hasAuroras));
		builder.append(" weather:").append(Boolean.toString(this.hasWeather));
		return builder.toString();
	}
}
