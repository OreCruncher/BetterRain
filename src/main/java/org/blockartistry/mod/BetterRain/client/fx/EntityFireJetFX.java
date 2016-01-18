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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.world.World;

/*
 * A special hidden entity that generates vertically moving flame entity
 * particles while it is alive.  These can randomly spawn on top of
 * lava blocks to give the effect of a fire jet.  The EntityFireJetFX
 * itself does not render - it is just a way to maintain state across
 * several ticks while it spews fire particles.
 */
@SideOnly(Side.CLIENT)
public class EntityFireJetFX extends EntityJetFX {

	private static final String FIRE_SOUND = "minecraft:fire.fire";

	protected EntityFireJetFX(final int strength, final World world, final double x, final double y, final double z) {
		super(strength, world, x, y, z);
	}

	protected EntityFX spawnJetParticle(final World world, final EffectRenderer renderer) {
		final EntityFlameFX flame = new EntityFlameFX(world, this.posX, this.posY, this.posZ, 0.0D,
				this.jetStrength / 10.0D, 0.0D);
		flame.flameScale *= this.jetStrength;
		renderer.addEffect(flame);
		world.playSound(this.posX, this.posY, this.posZ, FIRE_SOUND, this.jetStrength, 1.0F, false);
		return flame;
	}
}
