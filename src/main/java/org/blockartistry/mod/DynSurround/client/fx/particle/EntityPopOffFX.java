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

import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.XorShiftRandom;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class EntityPopOffFX extends EntityFX {

	private static final String[] POWER_WORDS = new String[] { "BAM", "BANG", "BONK", "CRRACK", "CRASH", "KRUNCH",
			"OOOOFF", "POWIE", "SPLATT", "THUNK", "TWAPE", "WHAMMM", "ZAP" };

	private static String getPowerWord() {
		return POWER_WORDS[XorShiftRandom.shared.nextInt(POWER_WORDS.length)];
	}

	private static final float GRAVITY = 0.8F;
	private static final float SIZE = 3.0F;
	private static final int LIFESPAN = 12;

	private static final Color HEAL_COLOR = Color.GREEN;
	private static final Color DAMAGE_COLOR = Color.RED;
	private static final Color CRITICAL_COLOR = Color.ORANGE;

	private Color renderColor;
	private int damage;
	private boolean criticalhit = false;
	private boolean grow = true;
	private String powerWord = null;
	private boolean shouldOnTop = false;

	public EntityPopOffFX(World world, double x, double y, double z, double dX, double dY, double dZ) {
		this(world, x, y, z, dX, dY, dZ, 0);
		this.criticalhit = true;
		this.shouldOnTop = true;
		this.particleGravity = -0.025F;
		this.renderColor = CRITICAL_COLOR;
		this.particleMaxAge += this.particleAge / 2;
		this.powerWord = getPowerWord() + "!";
	}

	public EntityPopOffFX(World world, double x, double y, double z, double dX, double dY, double dZ, int damage) {
		super(world, x, y, z, dX, dY, dZ);

		if (damage < 0) {
			this.renderColor = HEAL_COLOR;
			this.damage = Math.abs(damage);
		} else {
			this.renderColor = DAMAGE_COLOR;
			this.damage = damage;
		}

		this.motionX = dX;
		this.motionY = dY;
		this.motionZ = dZ;
		final float dist = MathHelper
				.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = (this.motionX / dist * 0.12D);
		this.motionY = (this.motionY / dist * 0.12D);
		this.motionZ = (this.motionZ / dist * 0.12D);
		this.particleTextureJitterX = 1.5F;
		this.particleTextureJitterY = 1.5F;
		this.particleGravity = GRAVITY;
		this.particleScale = SIZE;
		this.particleMaxAge = LIFESPAN;
	}

	public void renderParticle(Tessellator p_70539_1_, float x, float y, float z, float dX, float dY, float dZ) {
		this.rotationYaw = (-Minecraft.getMinecraft().thePlayer.rotationYaw);
		this.rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

		final float locX = ((float) (this.prevPosX + (this.posX - this.prevPosX) * x - interpPosX));
		final float locY = ((float) (this.prevPosY + (this.posY - this.prevPosY) * y - interpPosY));
		final float locZ = ((float) (this.prevPosZ + (this.posZ - this.prevPosZ) * z - interpPosZ));

		GL11.glPushMatrix();
		if (this.shouldOnTop) {
			GL11.glDepthFunc(519);
		} else {
			GL11.glDepthFunc(515);
		}
		GL11.glTranslatef(locX, locY, locZ);
		GL11.glRotatef(this.rotationYaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(this.rotationPitch, 1.0F, 0.0F, 0.0F);

		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glScaled(this.particleScale * 0.008D, this.particleScale * 0.008D, this.particleScale * 0.008D);
		if (this.criticalhit) {
			GL11.glScaled(0.5D, 0.5D, 0.5D);
		}
		
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(3553);
		GL11.glEnable(2929);
		GL11.glDisable(2896);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(3042);
		GL11.glEnable(3008);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (this.criticalhit) {
			fontRenderer.drawStringWithShadow(this.powerWord,
					-MathHelper.floor_float(fontRenderer.getStringWidth(this.powerWord) / 2.0F) + 1,
					-MathHelper.floor_float(fontRenderer.FONT_HEIGHT / 2.0F) + 1, this.renderColor.rgb());
		} else {
			final String text = String.valueOf(this.damage);
			final Color scaledColor = Color.scale(this.renderColor, 0.5F);
			fontRenderer.drawStringWithShadow(text,
					-MathHelper.floor_float(fontRenderer.getStringWidth(text) / 2.0F) + 1,
					-MathHelper.floor_float(fontRenderer.FONT_HEIGHT / 2.0F) + 1, scaledColor.rgb());
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthFunc(515);

		GL11.glPopMatrix();
		if (this.grow) {
			this.particleScale *= 1.08F;
			if (this.particleScale > SIZE * 3.0D) {
				this.grow = false;
			}
		} else {
			this.particleScale *= 0.96F;
		}
	}

	public int getFXLayer() {
		return 3;
	}
}
