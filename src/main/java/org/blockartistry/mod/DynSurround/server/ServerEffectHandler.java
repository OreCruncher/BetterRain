/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
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

package org.blockartistry.mod.DynSurround.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import java.util.List;
import java.util.Set;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.data.AuroraData;
import org.blockartistry.mod.DynSurround.data.AuroraPreset;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.ColorPair;
import org.blockartistry.mod.DynSurround.data.DimensionEffectData;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.network.Network;
import org.blockartistry.mod.DynSurround.util.DiurnalUtils;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;

public final class ServerEffectHandler {

	private static final float RESET = -10.0F;

	private static final boolean AURORA_ENABLE = ModOptions.getAuroraEnable();

	// Offset from the player location so they can see it
	// without looking straight up.
	private static final int Z_OFFSET = -ModOptions.getAuroraSpawnOffset();

	// Minimum distance between auroras, squared
	private static final long MIN_AURORA_DISTANCE_SQ = 400 * 400;

	public static void initialize() {
		FMLCommonHandler.instance().bus().register(new ServerEffectHandler());
	}

	@SubscribeEvent
	public void tickEvent(final TickEvent.WorldTickEvent event) {

		if (event.phase == Phase.END) {
			if (AURORA_ENABLE)
				processAuroras(event);
			return;
		}

		final World world = event.world;
		final int dimensionId = world.provider.dimensionId;
		final float sendIntensity = DimensionRegistry.hasWeather(world) ? DimensionEffectData.get(world).getRainIntensity()
				: RESET;

		// Set the rain intensity for all players in the current
		// dimension.
		Network.sendRainIntensity(sendIntensity, dimensionId);
	}

	private static boolean isAuroraInRange(final EntityPlayerMP player, final Set<AuroraData> data) {
		for (final AuroraData aurora : data) {
			final long deltaX = aurora.posX - (int) player.posX;
			final long deltaZ = aurora.posZ - (int) player.posZ + Z_OFFSET;
			final long distSq = deltaX * deltaX + deltaZ * deltaZ;
			if (distSq <= MIN_AURORA_DISTANCE_SQ)
				return true;
		}

		return false;
	}

	/*
	 * Only OK to spawn an aurora when it is night time and the moon brightness
	 * is less than half full.
	 */
	private static boolean okToSpawnAurora(final World world) {
		return DiurnalUtils.isNighttime(world);
	}

	private static final int CHECK_INTERVAL = 100; // Ticks
	private static TIntIntHashMap tickCounters = new TIntIntHashMap();

	protected void processAuroras(final TickEvent.WorldTickEvent event) {

		final World world = event.world;
		if (world == null || !DimensionRegistry.hasAuroras(world))
			return;

		final Set<AuroraData> data = DimensionEffectData.get(world).getAuroraList();

		// Daylight hours clear the aurora list. No auroras should be
		// showing at this time.
		if (DiurnalUtils.isDaytime(world)) {
			data.clear();
		} else {
			final int tickCount = tickCounters.get(world.provider.dimensionId) + 1;
			tickCounters.put(world.provider.dimensionId, tickCount);
			if (tickCount % CHECK_INTERVAL == 0) {
				if (okToSpawnAurora(world)) {

					@SuppressWarnings("unchecked")
					final List<EntityPlayerMP> players = MinecraftServer.getServer()
							.getConfigurationManager().playerEntityList;

					for (final EntityPlayerMP player : players) {
						if (!BiomeRegistry.hasAurora(PlayerUtils.getPlayerBiome(player, false)))
							continue;
						if (isAuroraInRange(player, data))
							continue;

						final int colorSet = ColorPair.randomId();
						final int preset = AuroraPreset.randomId();
						// final int colorSet = ColorPair.testId();
						// final int preset = AuroraPreset.testId();
						final AuroraData aurora = new AuroraData(player, Z_OFFSET, colorSet, preset);
						if (data.add(aurora)) {
							ModLog.info("Spawned new aurora: " + aurora.toString());
						}
					}
				}

				for (final AuroraData a : data) {
					Network.sendAurora(a, world.provider.dimensionId);
				}
			}
		}
	}
}
