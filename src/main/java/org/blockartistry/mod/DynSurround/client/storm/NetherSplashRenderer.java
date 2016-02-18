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

package org.blockartistry.mod.DynSurround.client.storm;

import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.fx.particle.ParticleFactory;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class NetherSplashRenderer extends StormSplashRenderer {

	@Override
	protected String getBlockSoundFX(final Block block, final boolean hasDust, final World world) {
		return hasDust ? StormProperties.getIntensity().getDustSound() : null;
	}

	@Override
	protected EntityFX getBlockParticleFX(final Block block, final boolean dust, final World world, final double x,
			final double y, final double z) {
		if (dust)
			return ParticleFactory.smoke.getEntityFX(0, world, x, y, z, 0, 0, 0);
		return null;
	}

	@Override
	protected BlockPos getPrecipitationHeight(final World world, final int range, final BlockPos pos) {
		final int y = MathHelper.floor_double(EnvironState.getPlayer().posY);
		boolean airBlockFound = false;
		for (int i = range; i >= -range; i--) {
			final BlockPos p = new BlockPos(pos.getX(), y + i, pos.getZ());
			final Block block = world.getBlockState(p).getBlock();
			if (airBlockFound && block != Blocks.air && block.getMaterial().isSolid())
				return p.up();
			if (block == Blocks.air)
				airBlockFound = true;
		}

		return new BlockPos(pos.getX(), 128, pos.getZ());
	}
}
