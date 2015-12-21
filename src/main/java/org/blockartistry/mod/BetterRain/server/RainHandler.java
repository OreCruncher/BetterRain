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

package org.blockartistry.mod.BetterRain.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import net.minecraft.world.World;

import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.RainData;
import org.blockartistry.mod.BetterRain.network.Network;
import org.blockartistry.mod.BetterRain.util.ElementRule;

public class RainHandler {

	private static final float RESET = -10.0F;

	public static void initialize() {
		FMLCommonHandler.instance().bus().register(new RainHandler());
	}

	private final ElementRule rule = ModOptions.getDimensionRule();

	@SubscribeEvent
	public void tickEvent(final TickEvent.WorldTickEvent event) {

		if (event.phase != Phase.START)
			return;

		float sendStrength = RESET;
		final World world = event.world;
		final int dimensionId = world.provider.dimensionId;

		// Have to be a surface world and match the dimension rule
		if (world.provider.isSurfaceWorld() && rule.isOk(dimensionId)) {
			final RainData data = RainData.get(world);
			if (world.isRaining()) {
				if (data.getRainIntensity() == 0.0F) {
					data.randomize();
					ModLog.info(String.format("dim %d rain strength set to %f", dimensionId, data.getRainIntensity()));
				}
			} else if (data.getRainIntensity() > 0.0F) {
				ModLog.info(String.format("dim %d rain is stopping", dimensionId));
				data.setRainIntensity(0.0F);
			}
			sendStrength = data.getRainIntensity();
		}

		// Set the rain strength for all players in the current
		// dimension.
		Network.sendRainStrength(sendStrength, dimensionId);
	}
}
