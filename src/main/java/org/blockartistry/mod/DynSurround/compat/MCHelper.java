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

package org.blockartistry.mod.DynSurround.compat;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class MCHelper {

	public static String nameOf(final Block block) {
		return Block.blockRegistry.getNameForObject(block);
	}
	
	public static Block getBlockNameRaw(final String blockName) {
		return GameData.getBlockRegistry().getRaw(blockName);
	}
	
	public static Block getBlock(final World world, final BlockPos pos) {
		return world.getBlock(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static int getBlockMetadata(final World world, final BlockPos pos) {
		return world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static boolean isAirBlock(final World world, final BlockPos pos) {
		return world.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static boolean isLeafBlock(final World world, final BlockPos pos) {
		return getBlock(world, pos).isLeaves(world, pos.getX(), pos.getY(), pos.getZ());
	}

}
