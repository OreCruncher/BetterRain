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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.client.IClientEffectHandler;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/*
 * Based on doVoidParticles().
 */
@SideOnly(Side.CLIENT)
public class BlockEffectHandler implements IClientEffectHandler {

	private static final Random random = new XorShiftRandom();
	private static final int RANGE = 16;
	private static final int CHECK_COUNT = 1000;

	public static interface IBlockEffect {
		boolean trigger(final Block block, final World world, final int x, final int y, final int z,
				final Random random);

		void doEffect(final Block block, final World world, final int x, final int y, final int z, final Random random);
	}

	private static final Map<Block, List<IBlockEffect>> effects = new IdentityHashMap<Block, List<IBlockEffect>>();

	public static void register(final Block block, final IBlockEffect effect) {
		List<IBlockEffect> chain = effects.get(block);
		if (chain == null) {
			effects.put(block, chain = new ArrayList<IBlockEffect>());
		}
		chain.add(effect);
	}

	@Override
	public void process(final World world, final EntityPlayer player) {
		if (Minecraft.getMinecraft().isGamePaused())
			return;

		final int playerX = MathHelper.floor_double(player.posX);
		final int playerY = MathHelper.floor_double(player.posY);
		final int playerZ = MathHelper.floor_double(player.posZ);

		for (int i = 0; i < CHECK_COUNT; i++) {
			final int x = playerX + random.nextInt(RANGE) - random.nextInt(RANGE);
			final int y = playerY + random.nextInt(RANGE) - random.nextInt(RANGE);
			final int z = playerZ + random.nextInt(RANGE) - random.nextInt(RANGE);
			final Block block = world.getBlock(x, y, z);
			if (block != Blocks.air) {
				final List<IBlockEffect> chain = effects.get(block);
				if (chain != null) {
					for (final IBlockEffect effect : chain)
						if (effect.trigger(block, world, x, y, z, random))
							effect.doEffect(block, world, x, y, z, random);
				}
			}
		}
	}

	@Override
	public boolean hasEvents() {
		return false;
	}

	public static void initialize() {

		// Particles
		final BlockLiquidHandler jets = new BlockLiquidHandler();
		if (ModOptions.getEnableFireJets())
			register(Blocks.lava, jets);
		if (ModOptions.getEnableBubbleJets())
			register(Blocks.water, jets);

		// Sounds
		if (ModOptions.getEnableIceCrackSound()) {
			final SoundEffect handler = new BasicSoundHandler(Module.MOD_ID + ":ice");
			handler.setChance(ModOptions.getIceCrackSoundChance());
			handler.setScale(ModOptions.getIceCrackScaleFactor());
			handler.setVolume(0.3F);
			BlockEffectHandler.register(Blocks.ice, handler);
			BlockEffectHandler.register(Blocks.packed_ice, handler);
		}

		if (ModOptions.getEnableFrogCroakSound()) {
			final SoundEffect handler = new VariablePitchSoundHandler(Module.MOD_ID + ":frog");
			handler.setChance(ModOptions.getFrogCroakSoundChance());
			handler.setScale(ModOptions.getFrogCroakScaleFactor());
			handler.setVolume(0.4F);
			BlockEffectHandler.register(Blocks.waterlily, handler);
		}

		if (ModOptions.getEnableRedstoneOreSound()) {
			final SoundEffect handler = new BasicSoundHandler("minecraft:random.fizz");
			handler.setChance(ModOptions.getRedstoneOreSoundChance());
			handler.setScale(ModOptions.getRedstoneOreScaleFactor());
			handler.setVolume(0.3F);
			BlockEffectHandler.register(Blocks.redstone_ore, handler);
		}

		if (ModOptions.getEnableSoulSandSound()) {
			final SoundEffect handler = new VariablePitchSoundHandler(Module.MOD_ID + ":soulsand");
			handler.setChance(ModOptions.getSoulSandSoundChance());
			handler.setScale(ModOptions.getSoulSandScaleFactor());
			handler.setVolume(0.2F);
			BlockEffectHandler.register(Blocks.soul_sand, handler);
		}
	}

}
