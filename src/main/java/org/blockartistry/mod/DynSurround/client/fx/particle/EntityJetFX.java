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

import org.blockartistry.mod.DynSurround.util.random.XorShiftRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

/*
 * Base for particle entities that are long lived and generate
 * other particles as a jet.  This entity does not render - just
 * serves as a particle factory.
 */
@SideOnly(Side.CLIENT)
public abstract class EntityJetFX extends EntityFX {

	protected static final Random RANDOM = XorShiftRandom.current();
	
	protected final int jetStrength;
	protected final int updateFrequency;

	public EntityJetFX(final int strength, final World world, final double x,
			final double y, final double z) {
		this(strength, world, x, y, z, 3);
	}
	
	public EntityJetFX(final int strength, final World world, final double x,
			final double y, final double z, final int freq) {
		super(world, x, y, z);

		this.setAlphaF(0.0F);
		this.jetStrength = strength;
		this.updateFrequency = freq;
		this.particleMaxAge = (RANDOM.nextInt(strength) + 2) * 20;
	}

	/*
	 * Nothing to render so optimize out
	 */
	@Override
	public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_,
			float p_70539_5_, float p_70539_6_, float p_70539_7_) {
	}
	
	/*
	 * Override in derived class to provide particle for the
	 * jet.
	 */
	protected abstract EntityFX getJetParticle();

	/*
	 * Hook to play sound when the jet is created
	 */
	public void playSound() { }
	
	/*
	 * During update see if a particle needs to be spawned so that it can rise
	 * up.
	 */
	@Override
	public void onUpdate() {

		// Check to see if a particle needs to be generated
		if (this.particleAge % this.updateFrequency == 0) {
			Minecraft.getMinecraft().effectRenderer.addEffect(getJetParticle());
		}

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}
	}
}
