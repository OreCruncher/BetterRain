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

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockLilyPadHandler {

	private static final boolean ENABLE_FROG_SOUNDS = ModOptions.getEnableFrogCroakSound();
	private static final String FROG_SOUND = String.format("%s:%s", BetterRain.MOD_ID, "frog");

	/*
	 * Hooked into BlockLilyPad.randomDisplayTick(). Goal is to randomly spawn frog
	 * sounds.
	 */
	public static void randomDisplayTick(final Block theThis, final World world, final int x, final int y, final int z,
			final Random random) {
		if (ENABLE_FROG_SOUNDS && random.nextInt(25) == 0) {
			world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, FROG_SOUND, 2.0F, 1.0F, false);
		}
	}
}
