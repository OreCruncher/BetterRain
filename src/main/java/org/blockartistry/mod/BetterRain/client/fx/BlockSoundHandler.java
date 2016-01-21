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

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.client.fx.blocks.IceBlockHandler;
import org.blockartistry.mod.BetterRain.client.fx.blocks.LilyPadBlockHandler;
import org.blockartistry.mod.BetterRain.client.fx.blocks.RedstoneOreBlockHandler;
import org.blockartistry.mod.BetterRain.client.fx.blocks.SoulSandBlockHandler;
import org.blockartistry.mod.BetterRain.client.fx.blocks.SoundHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class BlockSoundHandler {

	private static Map<Class<? extends Block>, SoundHandler> handlers = new IdentityHashMap<Class<? extends Block>, SoundHandler>();

	public static void initialize() {
		if (ModOptions.getEnableIceCrackSound()) {
			final SoundHandler handler = new IceBlockHandler();
			handlers.put(BlockIce.class, handler);
			handlers.put(BlockPackedIce.class, handler);

			// These blocks don't tick randomly
			// so they need to be configured.
			Blocks.packed_ice.setTickRandomly(true);
		}

		if (ModOptions.getEnableFrogCroakSound()) {
			handlers.put(BlockLilyPad.class, new LilyPadBlockHandler());
		}

		if (ModOptions.getEnableRedstoneOreSound()) {
			handlers.put(BlockRedstoneOre.class, new RedstoneOreBlockHandler());
		}
		
		if(ModOptions.getEnableSoulSandSound()) {
			handlers.put(BlockSoulSand.class, new SoulSandBlockHandler());
			Blocks.soul_sand.setTickRandomly(true);
		}
	}

	/*
	 * Hooked into the Blocks randomDisplayTick(). Goal is to randomly spawn
	 * sound effects.
	 */
	public static void randomDisplayTick(final World world, final BlockPos pos, final IBlockState state,
			final Random random) {
		final SoundHandler handler = handlers.get(state.getBlock().getClass());
		if (handler != null && handler.trigger())
			handler.doSound(world, pos.getX(), pos.getY(), pos.getZ());
	}
}
