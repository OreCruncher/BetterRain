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

import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * A special hidden entity that generates vertically moving buble entity
 * particles while it is alive.  These spawn in water blocks at are
 * above another solid block.  The EntityBubbleJetFX does not render
 * - it is just a way to maintain state across several ticks while it
 * spews bubble particles.
 */
@SideOnly(Side.CLIENT)
public final class EntityBubbleJetFX implements IParticleFactory {
	
	public static final IParticleFactory factory = new EntityBubbleJetFX();

	private EntityBubbleJetFX() {
	}

	@Override
	public EntityFX getEntityFX(int jetStrength, World world, double x, double y, double z,
			double sX, double sY, double sZ, int... notUsed) {
		final EntityFX bubble = new EntityBubbleFX.Factory().getEntityFX(0, world, x, y, z, 0.0D,
				0.5D + jetStrength / 10.0D, 0.0D);
		return bubble;
	}
}
