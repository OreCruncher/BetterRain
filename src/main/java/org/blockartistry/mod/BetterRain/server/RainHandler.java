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
import net.minecraft.world.World;

import java.util.Random;

import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.RainData;
import org.blockartistry.mod.BetterRain.network.Network;
import org.blockartistry.mod.BetterRain.util.ElementRule;
import org.blockartistry.mod.BetterRain.util.ElementRule.Rule;

public class RainHandler {

	private static final Random random = new Random();
	private static final float RESET = -10.0F;

	private static final ElementRule rule = new ElementRule(
			ModOptions.getDimensionListAsBlacklist() ? Rule.MUST_NOT_BE_IN : Rule.MUST_BE_IN,
			ModOptions.getDimensionList());

	public static void initialize() {
		FMLCommonHandler.instance().bus().register(new RainHandler());
	}

	@SubscribeEvent
	public void tickEvent(final TickEvent.WorldTickEvent event) {

		float sendStrength = RESET;
		final World world = event.world;

		// Have to be a surface world and match the dimension rule
		if (world.provider.isSurfaceWorld() && rule.isOk(world.provider.dimensionId)) {
			final RainData data = RainData.get(world);
			if (world.getRainStrength(1.0F) > 0.0F) {
				if (data.getRainStrength() == 0.0F) {
					final float strength = 0.05F + (0.90F * random.nextFloat());
					data.setRainStrength(strength);
					ModLog.info(String.format("Rain strength set to %f", data.getRainStrength()));
				}
			} else if (data.getRainStrength() > 0.0F) {
				ModLog.info("Rain is stopping");
				data.setRainStrength(0.0F);
			}
			sendStrength = data.getRainStrength();
		}

		// Set the rain strength for all players in the current
		// dimension.
		Network.sendRainStrength(sendStrength, world.provider.dimensionId);
	}
}
