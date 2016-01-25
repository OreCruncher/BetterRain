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

package org.blockartistry.mod.DynSurround.client.fx.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * A special hidden entity that generates vertically moving flame entity
 * particles while it is alive.  These can randomly spawn on top of
 * lava blocks to give the effect of a fire jet.  The FireFactory
 * itself does not render - it is just a way to maintain state across
 * several ticks while it spews fire particles.
 */
@SideOnly(Side.CLIENT)
public final class FireFactory implements IParticleFactory {

	private static final String FIRE_SOUND = "minecraft:fire.fire";

	public FireFactory() {
	}

	@Override
	public EntityFX getEntityFX(final int jetStrength, final World world, final double x, final double y,
			final double z, final double sX, final double sY, final double sZ, final int... notUsed) {
		final EntityFlameFX flame = (EntityFlameFX) new EntityFlameFX.Factory().getEntityFX(0, world, x, y, z, 0.0D,
				jetStrength / 10.0D, 0.0D);
		flame.flameScale *= jetStrength;
		world.playSound(x, y, z, FIRE_SOUND, jetStrength, 1.0F, false);
		return flame;
	}
}
