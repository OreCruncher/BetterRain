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

import java.util.Random;

import org.blockartistry.mod.DynSurround.client.fx.IParticleFactory;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ParticleFactory {

	private static final Random RANDOM = new XorShiftRandom();

	private ParticleFactory() {
	}

	public static final IParticleFactory bubbleJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityFX bubble = new EntityBubbleFX(world, x, y, z, 0.0D, 0.5D + particleID / 10.0D, 0.0D);
			return bubble;
		}
	};

	public static final IParticleFactory fireJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityFlameFX flame = (EntityFlameFX) new EntityFlameFX(world, x, y, z, 0.0D, particleID / 10.0D,
					0.0D);
			flame.flameScale *= particleID;
			return flame;
		}
	};

	public static final IParticleFactory lavaJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityLavaFX fx = new EntityLavaFX(world, x, y, z);
			return fx;
		}
	};

	public static final IParticleFactory steamJet = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final double motionX = RANDOM.nextGaussian() * 0.02D;
			final double motionZ = RANDOM.nextGaussian() * 0.02D;
			return myCloud.getEntityFX(particleID, world, x, y, z, motionX, 0.1F, motionZ);
		}
	};

	public static final IParticleFactory jet = new EntityJetFX.Factory();

	public static final IParticleFactory lavaSpark = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityLavaFX fx = new EntityLavaFX(world, x, y, z);
			return fx;
		}
	};

	public static final IParticleFactory smoke = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntitySmokeFX fx = new EntitySmokeFX(world, x, y, z, dX, dY, dZ);
			return fx;
		}
	};

	public static final IParticleFactory rain = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityRainFX fx = new EntityRainFX(world, x, y, z);
			return fx;
		}
	};

	public static final IParticleFactory myCloud = new IParticleFactory() {
		@Override
		public EntityFX getEntityFX(int particleID, World world, double x, double y, double z, double dX, double dY,
				double dZ, int... misc) {
			final EntityCloudFX fx = new EntityCloudFX(world, x, y, z, dX, dY, dZ) {
				@Override
				public void onUpdate() {
					this.prevPosX = this.posX;
					this.prevPosY = this.posY;
					this.prevPosZ = this.posZ;

					if (this.particleAge++ >= this.particleMaxAge) {
						this.setDead();
					}

					this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
					this.moveEntity(this.motionX, this.motionY, this.motionZ);
					this.motionX *= 0.9599999785423279D;
					this.motionY *= 0.9599999785423279D;
					this.motionZ *= 0.9599999785423279D;

					if (this.onGround) {
						this.motionX *= 0.699999988079071D;
						this.motionZ *= 0.699999988079071D;
					}
				}
			};
			return fx;
		}
	};
}
