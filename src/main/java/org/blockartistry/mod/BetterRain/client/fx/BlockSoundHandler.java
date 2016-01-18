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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

import org.blockartistry.mod.BetterRain.BetterRain;
import org.blockartistry.mod.BetterRain.ModOptions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class BlockSoundHandler {

	private static final boolean ENABLE_ICE_CRACK_SOUND = ModOptions.getEnableIceCrackSound();
	private static final boolean ENABLE_FROG_SOUNDS = ModOptions.getEnableFrogCroakSound();

	private interface IBlockSoundHandler {
		int chance();

		void doSound(final World world, final int x, final int y, final int z, final Random random);
	};

	private static Map<Class<? extends Block>, IBlockSoundHandler> handlers = new IdentityHashMap<Class<? extends Block>, IBlockSoundHandler>();

	static {
		if (ENABLE_ICE_CRACK_SOUND) {
			handlers.put(BlockIce.class, new IBlockSoundHandler() {

				private final String ICE_SOUND = BetterRain.MOD_ID + ":ice";

				@Override
				public int chance() {
					return 10000;
				}

				@Override
				public void doSound(final World world, final int x, final int y, final int z, final Random random) {
					world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, ICE_SOUND, 0.3F, 0.1F, false);
				}
			});

		}

		if (ENABLE_FROG_SOUNDS) {
			handlers.put(BlockLilyPad.class, new IBlockSoundHandler() {

				private final String FROG_SOUND = BetterRain.MOD_ID + ":frog";
				private final float[] pitch = { 0.8F, 1.0F, 1.0F, 1.2F, 1.2F, 1.2F };

				@Override
				public int chance() {
					return 25;
				}

				@Override
				public void doSound(final World world, final int x, final int y, final int z, final Random random) {
					world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, FROG_SOUND, 2.0F, pitch[random.nextInt(pitch.length)],
							false);
				}
			});
		}
	}

	/*
	 * Hooked into the Blocks randomDisplayTick(). Goal is to randomly spawn
	 * sound effects.
	 */
	public static void randomDisplayTick(final World world, final BlockPos pos, final IBlockState state,
			final Random random) {
		final IBlockSoundHandler handler = handlers.get(state.getBlock().getClass());
		if (handlers != null && random.nextInt(handler.chance()) == 0)
			handler.doSound(world, pos.getX(), pos.getY(), pos.getZ(), random);
	}

}
