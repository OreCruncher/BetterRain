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

package org.blockartistry.mod.BetterRain.client.firejet;

import java.util.Random;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

/*
 * A special hidden entity that generates vertically moving flame entity
 * particles while it is alive.  These can randomly spawn on top of
 * lava blocks to give the effect of a fire jet.  The EntityFireJetFX
 * itself does not render - it is just a way to maintain state across
 * several ticks while it spews fire particles.
 */
public class EntityFireJetFX extends EntityFX {

	private static final boolean ENABLE_FIREJETS = ModOptions.getEnableFireJets();
	private static final int SPAWN_CHANCE = ModOptions.getFireJetsSpawnChance();
	private static final int MAX_LAVA_BLOCKS = 10;
	private static final String FIRE_SOUND = "minecraft:fire.fire";

	private final int jetStrength;

	protected EntityFireJetFX(final int strength, final World world, final double x, final double y, final double z) {
		super(world, x, y, z);

		this.setAlphaF(0.0F);
		this.jetStrength = strength;
		this.particleMaxAge = (XorShiftRandom.shared.nextInt(strength) + 2) * 20;
	}

	/*
	 * Nothing to render so optimize out
	 */
	@Override
	public void renderParticle(final Tessellator tess, final float x, final float y, final float z, final float dX,
			final float dY, final float dZ) {
	}

	/*
	 * During update see if a flame needs to be spawned so that it can rise up.
	 */
	@Override
	public void onUpdate() {

		// Check to see if a flame needs to be generated
		if (this.particleAge % 2 == 0) {
			final EntityFlameFX flame = new EntityFlameFX(this.worldObj, this.posX, this.posY, this.posZ, 0.0D,
					this.jetStrength / 10.0D, 0.0D);
			flame.flameScale *= this.jetStrength;
			final Minecraft mc = Minecraft.getMinecraft();
			mc.effectRenderer.addEffect(flame);
			mc.theWorld.playSound(this.posX, this.posY, this.posZ, FIRE_SOUND, this.jetStrength, 1.0F, false);
		}

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}
	}

	private static int lavaBlocksBeneath(final World world, int x, int y, int z) {
		int count = 0;
		for (; count < MAX_LAVA_BLOCKS; count++)
			if (world.getBlock(x, y - count - 1, z).getMaterial() != Material.lava)
				break;
		return count;
	}

	/*
	 * Hooked into BlockLiquid.randomDisplayTick(). Goal is to spawn
	 * EntityFireJetFX particles as an effect, like flames and lava pops of
	 * ordinary lava.
	 */
	public static void randomDisplayTick(final Block theThis, final World world, int x, int y, int z, Random random) {
		if (!ENABLE_FIREJETS)
			return;

		// If the material we are looking at isn't lava then return
		if (theThis.getMaterial() != Material.lava)
			return;

		// If there isn't an air block above, or if the random time
		// isn't triggered, return.
		if (random.nextInt(SPAWN_CHANCE) != 0 || !world.isAirBlock(x, y + 1, z))
			return;

		// Get the lava block count under the current one. There
		// has to be at least one to trigger the jet.
		final int lavaBlocks = lavaBlocksBeneath(world, x, y, z);
		if (lavaBlocks == 0)
			return;

		// The number of lava blocks beneath determines the jet
		// strength.  Strength affects life span, size of flame
		// particle, and the sound volume.
		final EntityFireJetFX jet = new EntityFireJetFX(lavaBlocks, world, x + 0.5D, y + 1.1D, z + 0.5D);
		Minecraft.getMinecraft().effectRenderer.addEffect(jet);
	}
}
