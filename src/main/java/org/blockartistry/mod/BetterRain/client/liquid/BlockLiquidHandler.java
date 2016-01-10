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

package org.blockartistry.mod.BetterRain.client.liquid;

import java.util.Random;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/*
 * Provides support for BlockLiquids.  It hooks the random display
 * routine in order to generate particle effects based on the
 * liquid block.
 */
public class BlockLiquidHandler {

	private static final Random RANDOM = XorShiftRandom.shared;
	private static final boolean ENABLE_FIREJETS = ModOptions.getEnableFireJets();
	private static final boolean ENABLE_WATER_BUBBLES = ModOptions.getEnableBubbleJets();
	private static final int FIREJET_SPAWN_CHANCE = ModOptions.getFireJetsSpawnChance();
	private static final int WATERBUBBLE_SPAWN_CHANCE = ModOptions.getBubbleJetSpawnChance();
	private static final int MAX_STRENGTH = 10;

	private static int countBlocks(final World world, final BlockPos start, final Block block, final int dir) {
		int count = 0;
		BlockPos loc = start;
		while (count < MAX_STRENGTH) {
			if (world.getBlockState(loc).getBlock() != block)
				return count;
			count++;
			loc = loc.add(0, dir, 0);
		}
		return count;
	}

	/*
	 * Hooked into BlockLiquid.randomDisplayTick(). Goal is to spawn EntityJetFX
	 * particles as a client side effect.
	 */
	public static void randomDisplayTick(final World world, final BlockPos pos, final IBlockState state, final Random rand) {
		final Block theThis = state.getBlock();
		EntityFX effect = null;
		if (ENABLE_FIREJETS && theThis == Blocks.lava) {
			if (RANDOM.nextInt(FIREJET_SPAWN_CHANCE) == 0 && world.getBlockState(pos.up()).getBlock() == Blocks.air) {
				// The number of lava blocks beneath determines the jet
				// strength. Strength affects life span, size of flame
				// particle, and the sound volume.
				final int lavaBlocks = countBlocks(world, pos, theThis, -1);
				effect = new EntityFireJetFX(lavaBlocks, world, pos.getX() + 0.5D, pos.getY() + 1.1D,
						pos.getZ() + 0.5D);
			}
		} else if (ENABLE_WATER_BUBBLES && theThis == Blocks.water) {
			if (RANDOM.nextInt(WATERBUBBLE_SPAWN_CHANCE) == 0
					&& world.getBlockState(pos.down()).getBlock().getMaterial().isSolid()) {
				// The number of water blocks in the water column determines
				// the jet strength. Strength affects life span of the jet
				// as well as the speed at which the bubbles rise.
				final int waterBlocks = countBlocks(world, pos, theThis, 1);
				effect = new EntityBubbleJetFX(waterBlocks, world, pos.getX() + 0.5D, pos.getY() + 0.1D,
						pos.getZ() + 0.5D);
			}
		}

		if (effect != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

}
