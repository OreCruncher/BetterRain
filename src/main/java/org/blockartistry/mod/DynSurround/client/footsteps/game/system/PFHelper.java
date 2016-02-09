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

package org.blockartistry.mod.DynSurround.client.footsteps.game.system;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PFHelper {
	/**
	 * Gets the block at a certain location in the current world. This method is
	 * not safe against locations in undefined space.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static Block getBlockAt(int x, int y, int z) {
		return getBlockAt(Minecraft.getMinecraft().theWorld, x, y, z);
	}

	/**
	 * Gets the name of the block at a certain location in the current world. If
	 * the location is in an undefined space (lower than zero or higher than the
	 * current world getHeight(), or throws any exception during evaluation), it
	 * will return a default string.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param defaultIfFail
	 * @return
	 */
	public static String getNameAt(int x, int y, int z, String defaultIfFail) {
		if (y <= 0 || y >= Minecraft.getMinecraft().theWorld.getHeight())
			return defaultIfFail;

		try {
			return getNameAt(Minecraft.getMinecraft().theWorld, x, y, z);
		} catch (Exception e) {
			return defaultIfFail;
		}
	}

	/**
	 * Gets the block at a certain location in the given world. This method is
	 * not safe against locations in undefined space.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private static Block getBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		return block;
	}

	/**
	 * Gets the name of the block at a certain location in the given world. This
	 * method is not safe against locations in undefined space.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private static String getNameAt(World world, int x, int y, int z) {
		return nameOf(getBlockAt(world, x, y, z));
	}

	//

	/**
	 * Gets the unique name of a given block, defined by its interoperability
	 * identifier.
	 * 
	 * @param block
	 * @return
	 */
	public static String nameOf(Block block) {
		// RegistryNamespaced
		return Block.blockRegistry.getNameForObject(block).toString();
	}
}
