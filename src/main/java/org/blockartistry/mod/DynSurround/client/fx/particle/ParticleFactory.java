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

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ParticleFactory {

	private ParticleFactory() {
	}

	public static final IParticleFactory bubbleJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {

			final EntityFX bubble = new EntityBubbleFX.Factory().getEntityFX(0, world, x, y, z, 0.0D,
					0.5D + particleID / 10.0D, 0.0D);
			return bubble;
		}
	};

	public static final IParticleFactory fireJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityFlameFX flame = (EntityFlameFX) new EntityFlameFX.Factory().getEntityFX(0, world, x, y, z, 0.0D,
					particleID / 10.0D, 0.0D);
			flame.flameScale *= particleID;
			return flame;
		}
	};

	public static final IParticleFactory lavaJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityLavaFX fx = (EntityLavaFX) new EntityLavaFX.Factory().getEntityFX(0, world, x, y, z, 0, 0, 0);
			return fx;
		}
	};

	public static final IParticleFactory jet = new EntityJetFX.Factory();
	public static final IParticleFactory lavaSpark = new EntityLavaFX.Factory();
	public static final IParticleFactory smoke = new EntitySmokeFX.Factory();
	public static final IParticleFactory rain = new EntityRainFX.Factory();
}