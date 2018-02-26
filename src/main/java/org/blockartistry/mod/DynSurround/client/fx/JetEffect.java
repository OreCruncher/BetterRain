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

import org.blockartistry.mod.DynSurround.client.fx.particle.EntityBubbleJetFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityDustJetFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityFireJetFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityFountainJetFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityJetFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntitySteamJetFX;
import org.blockartistry.mod.DynSurround.compat.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/*
 * Provides support for BlockLiquids.  It hooks the RANDOM display
 * routine in order to generate particle effects based on the
 * liquid block.
 */
public abstract class JetEffect extends BlockEffect {

	private static final int MAX_STRENGTH = 10;

	private static int countBlocks(final World world, final BlockPos pos, final Block block, final int dir) {
		int count = 0;
		int idx = pos.getY();
		while (count < MAX_STRENGTH) {
			if (world.getBlock(pos.getX(), idx, pos.getZ()) != block)
				return count;
			count++;
			idx += dir;
		}
		return count;
	}

	// Takes into account partial blocks because of flow
	private static double jetSpawnHeight(final World world, final BlockPos pos) {
		final int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
		return 1.1D - BlockLiquid.getLiquidHeightPercent(meta) + pos.getY();
	}

	public JetEffect(final int chance) {
		super(chance);
	}

	protected void addEffect(final EntityJetFX fx) {
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		fx.playSound();
	}

	public static class Fire extends JetEffect {
		public Fire(final int chance) {
			super(chance);
		}

		@Override
		public boolean trigger(final Block block, final World world, final BlockPos pos, final Random random) {
			return super.trigger(block, world, pos, random) && world.isAirBlock(pos.getX(), pos.getY() + 1, pos.getZ());
		}

		@Override
		public void doEffect(final Block block, final World world, final BlockPos pos, final Random random) {
			final int lavaBlocks = countBlocks(world, pos, block, -1);
			final double spawnHeight = jetSpawnHeight(world, pos);
			final EntityJetFX effect = new EntityFireJetFX(lavaBlocks, world, pos.getX() + 0.5D, spawnHeight,
					pos.getZ() + 0.5D);
			addEffect(effect);
		}
	}

	public static class Bubble extends JetEffect {
		public Bubble(final int chance) {
			super(chance);
		}

		@Override
		public boolean trigger(final Block block, final World world, final BlockPos pos, final Random random) {
			return super.trigger(block, world, pos, random)
					&& world.getBlock(pos.getX(), pos.getY() - 1, pos.getZ()).getMaterial().isSolid();
		}

		@Override
		public void doEffect(final Block block, final World world, final BlockPos pos, final Random random) {
			final int waterBlocks = countBlocks(world, pos, block, 1);
			final EntityJetFX effect = new EntityBubbleJetFX(waterBlocks, world, pos.getX() + 0.5D, pos.getY() + 0.1D,
					pos.getZ() + 0.5D);
			addEffect(effect);
		}
	}

	public static class Steam extends JetEffect {

		public Steam(final int chance) {
			super(chance);
		}

		protected int lavaCount(final World world, final BlockPos pos) {
			int blockCount = 0;
			for (int i = -1; i <= 1; i++)
				for (int j = -1; j <= 1; j++)
					for (int k = -1; k <= 1; k++)
						if (world.getBlock(pos.getX() + i, pos.getY() + j, pos.getZ() + k) == Blocks.lava)
							blockCount++;
			return blockCount;
		}

		@Override
		public boolean trigger(final Block block, final World world, final BlockPos pos, final Random random) {
			if (!super.trigger(block, world, pos, random) || !world.isAirBlock(pos.getX(), pos.getY() + 1, pos.getZ()))
				return false;

			return lavaCount(world, pos) != 0;
		}

		@Override
		public void doEffect(final Block block, final World world, final BlockPos pos, final Random random) {
			final int strength = lavaCount(world, pos);
			final double spawnHeight = jetSpawnHeight(world, pos);
			final EntityJetFX effect = new EntitySteamJetFX(strength, world, pos.getX() + 0.5D, spawnHeight,
					pos.getZ() + 0.5D);
			addEffect(effect);
		}
	}

	public static class Dust extends JetEffect {

		public Dust(final int chance) {
			super(chance);
		}

		@Override
		public boolean trigger(final Block block, final World world, final BlockPos pos, final Random random) {
			return super.trigger(block, world, pos, random) && world.isAirBlock(pos.getX(), pos.getY() - 1, pos.getZ());
		}

		@Override
		public void doEffect(final Block block, final World world, final BlockPos pos, final Random random) {
			final EntityJetFX effect = new EntityDustJetFX(2, world, pos.getX() + 0.5D, pos.getY() - 0.2D,
					pos.getZ() + 0.5D, block);
			addEffect(effect);
		}
	}

	public static class Fountain extends JetEffect {
		public Fountain(final int chance) {
			super(chance);
		}

		@Override
		public boolean trigger(final Block block, final World world, final BlockPos pos, final Random random) {
			return super.trigger(block, world, pos, random) && world.isAirBlock(pos.getX(), pos.getY() + 1, pos.getZ());
		}

		@Override
		public void doEffect(final Block block, final World world, final BlockPos pos, final Random random) {
			final EntityJetFX effect = new EntityFountainJetFX(5, world, pos.getX() + 0.5D, pos.getY() + 1.1D,
					pos.getZ() + 0.5D, block);
			addEffect(effect);
		}

	}
}
