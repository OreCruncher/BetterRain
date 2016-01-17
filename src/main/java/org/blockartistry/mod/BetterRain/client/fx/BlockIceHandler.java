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

package org.blockartistry.mod.BetterRain.client.fx;

import java.util.Random;

import org.blockartistry.mod.BetterRain.BetterRain;
import org.blockartistry.mod.BetterRain.ModOptions;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockIceHandler {

	private static final boolean ENABLE_ICE_CRACK_SOUND = ModOptions.getEnableIceCrackSound();
	private static final String ICE_SOUND = String.format("%s:%s", BetterRain.MOD_ID, "ice");

	/*
	 * Hooked into BlockIce.randomDisplayTick(). Goal is to randomly spawn ice
	 * cracking sounds.
	 */
	public static void randomDisplayTick(final World world, final BlockPos pos, final IBlockState state,
			final Random random) {
		if (ENABLE_ICE_CRACK_SOUND && random.nextInt(10000) == 0) {
			world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ICE_SOUND, 0.3F, 0.1F, false);
		}
	}
}
