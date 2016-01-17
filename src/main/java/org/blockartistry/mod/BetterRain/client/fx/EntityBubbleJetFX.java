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

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

/*
 * A special hidden entity that generates vertically moving buble entity
 * particles while it is alive.  These spawn in water blocks at are
 * above another solid block.  The EntityBubbleJetFX does not render
 * - it is just a way to maintain state across several ticks while it
 * spews bubble particles.
 */
public class EntityBubbleJetFX extends EntityJetFX {

	protected EntityBubbleJetFX(final int strength, final World world, final double x, final double y, final double z) {
		super(strength, world, x, y, z);
	}

	@Override
	protected EntityFX spawnJetParticle(final World world, final EffectRenderer renderer) {
		final EntityBubbleFX bubble = new EntityBubbleFX(world, this.posX, this.posY, this.posZ, 0.0D,
				0.5D + this.jetStrength / 10.0D, 0.0D);
		renderer.addEffect(bubble);
		return bubble;
	}

}
