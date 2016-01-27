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

package org.blockartistry.mod.DynSurround.client.fx;

import java.util.Random;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.fx.particle.ParticleFactory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.blockartistry.mod.DynSurround.client.fx.BlockEffectHandler.IBlockEffect;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityJetFX;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/*
 * Provides support for BlockLiquids.  It hooks the random display
 * routine in order to generate particle effects based on the
 * liquid block.
 */
@SideOnly(Side.CLIENT)
public class BlockLiquidHandler implements IBlockEffect {

	private static final int FIREJET_SPAWN_CHANCE = ModOptions.getFireJetsSpawnChance();
	private static final int WATERBUBBLE_SPAWN_CHANCE = ModOptions.getBubbleJetSpawnChance();
	private static final int MAX_STRENGTH = 10;

	private static int countBlocks(final World world, final int x, final int y, final int z, final Block block,
			final int dir) {
		int count = 0;
		int idx = y;
		while (count < MAX_STRENGTH) {
			if (world.getBlock(x, idx, z) != block)
				return count;
			count++;
			idx += dir;
		}
		return count;
	}

	public boolean trigger(final Block block, final World world, final int x, final int y, final int z,
			final Random random) {
		if (block == Blocks.lava) {
			if (random.nextInt(FIREJET_SPAWN_CHANCE) == 0 && world.isAirBlock(x, y + 1, z)) {
				return true;
			}
		} else if (block == Blocks.water) {
			if (random.nextInt(WATERBUBBLE_SPAWN_CHANCE) == 0 && world.getBlock(x, y - 1, z).getMaterial().isSolid()) {
				return true;
			}
		}
		return false;
	}

	public void doEffect(final Block theThis, final World world, final int x, final int y, final int z,
			final Random random) {
		EntityFX effect = null;
		if (theThis == Blocks.lava) {
			// The number of lava blocks beneath determines the jet
			// strength. Strength affects life span, size of flame
			// particle, and the sound volume.
			final int lavaBlocks = countBlocks(world, x, y, z, theThis, -1);
			final int jetType = random.nextInt(3) == 0 ? EntityJetFX.LAVA : EntityJetFX.FIRE;
			effect = ParticleFactory.jet.getEntityFX(lavaBlocks, world, x + 0.5D, y + 1.1D, z + 0.5D, 0, 0, 0, jetType);
		} else if (theThis == Blocks.water) {
			// The number of water blocks in the water column determines
			// the jet strength. Strength affects life span of the jet
			// as well as the speed at which the bubbles rise.
			final int waterBlocks = countBlocks(world, x, y, z, theThis, 1);
			effect = ParticleFactory.jet.getEntityFX(waterBlocks, world, x + 0.5D, y + 1.1D, z + 0.5D, 0, 0, 0,
					EntityJetFX.BUBBLE);
		}

		if (effect != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

}
