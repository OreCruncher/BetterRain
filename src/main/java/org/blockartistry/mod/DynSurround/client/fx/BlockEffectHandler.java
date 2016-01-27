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
	private static final int RANGE = ModOptions.getSpecialEffectRange();
	private static final double RATIO = 0.0335671847202175D;
	private static final int CHECK_COUNT = (int) (Math.pow(RANGE * 2 - 1, 3) * RATIO);

	private static final Map<Block, List<BlockEffect>> effects = new IdentityHashMap<Block, List<BlockEffect>>();

	public static void register(final Block block, final BlockEffect effect) {
		List<BlockEffect> chain = effects.get(block);
		if (chain == null) {
			effects.put(block, chain = new ArrayList<BlockEffect>());
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
				final List<BlockEffect> chain = effects.get(block);
				if (chain != null) {
					for (final BlockEffect effect : chain)
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
		if (ModOptions.getEnableFireJets())
			register(Blocks.lava, new JetEffect.Fire());
		if (ModOptions.getEnableBubbleJets())
			register(Blocks.water, new JetEffect.Bubble());

		// Sounds
		if (ModOptions.getEnableIceCrackSound()) {
			final SoundEffect handler = new SoundEffect(Module.MOD_ID + ":ice");
			handler.setChance(ModOptions.getIceCrackSoundChance());
			handler.setScale(ModOptions.getIceCrackScaleFactor());
			handler.setVolume(0.3F);
			BlockEffectHandler.register(Blocks.ice, handler);
			BlockEffectHandler.register(Blocks.packed_ice, handler);
		}

		if (ModOptions.getEnableFrogCroakSound()) {
			final SoundEffect handler = new SoundEffect(Module.MOD_ID + ":frog");
			handler.setChance(ModOptions.getFrogCroakSoundChance());
			handler.setScale(ModOptions.getFrogCroakScaleFactor());
			handler.setVolume(0.4F);
			handler.setVariablePitch(true);
			BlockEffectHandler.register(Blocks.waterlily, handler);
		}

		if (ModOptions.getEnableRedstoneOreSound()) {
			final SoundEffect handler = new SoundEffect("minecraft:random.fizz");
			handler.setChance(ModOptions.getRedstoneOreSoundChance());
			handler.setScale(ModOptions.getRedstoneOreScaleFactor());
			handler.setVolume(0.3F);
			BlockEffectHandler.register(Blocks.redstone_ore, handler);
		}

		if (ModOptions.getEnableSoulSandSound()) {
			final SoundEffect handler = new SoundEffect(Module.MOD_ID + ":soulsand");
			handler.setChance(ModOptions.getSoulSandSoundChance());
			handler.setScale(ModOptions.getSoulSandScaleFactor());
			handler.setVolume(0.2F);
			handler.setVariablePitch(true);
			BlockEffectHandler.register(Blocks.soul_sand, handler);
		}
	}

}
