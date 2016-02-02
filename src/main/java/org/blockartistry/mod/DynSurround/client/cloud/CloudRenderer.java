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
package org.blockartistry.mod.DynSurround.client.cloud;

import org.blockartistry.mod.DynSurround.client.ClientEffectHandler;
import org.blockartistry.mod.DynSurround.util.Color;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

@SideOnly(Side.CLIENT)
public final class CloudRenderer extends IRenderHandler {

	private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");

	public CloudRenderer() {

	}

	private static void drawCloudCube(final float posX, final float posY, final float posZ, final Color color,
			final float alpha) {

		GL11.glPushMatrix();
		GL11.glTranslatef(posX, posY, posZ);

		Tessellator tess = Tessellator.instance;
		int c = color.rgbWithAlpha(alpha);
		tess.startDrawing(3);

		tess.setColorOpaque_I(c);
		tess.addVertex(0, 0, 0);
		tess.addVertex(1, 0, 0);
		tess.addVertex(1, 0, 1);
		tess.addVertex(0, 0, 1);
		tess.addVertex(0, 0, 0);
		tess.draw();

		tess.startDrawing(3);
		tess.setColorOpaque_I(c);

		tess.addVertex(0, 1, 0);
		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 1, 1);
		tess.addVertex(0, 1, 1);
		tess.addVertex(0, 1, 0);
		tess.draw();
		tess.startDrawing(1);

		tess.setColorOpaque_I(c);

		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 1, 0);
		tess.addVertex(1, 0, 0);
		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 0, 1);
		tess.addVertex(1, 1, 1);
		tess.addVertex(0, 0, 1);
		tess.addVertex(0, 1, 1);
		tess.draw();

		GL11.glPopMatrix();
	}

	@Override
	public void render(final float partialTicks, final WorldClient world, final Minecraft mc) {

		final float alpha = 0.8F;

		final Tessellator tess = Tessellator.instance;
		final EntityLivingBase entity = mc.renderViewEntity;

		final Color cloudColor = new Color(world.getCloudColour(partialTicks));
		if (mc.gameSettings.anaglyph)
			cloudColor.anaglyph();

		GL11.glDisable(GL11.GL_CULL_FACE);
		float posY = (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks);
		float f2 = 12.0F;
		float f3 = 4.0F;
		double d0 = (double) ((float) ClientEffectHandler.getTickCount() + partialTicks);
		double posX = (entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks
				+ d0 * 0.029999999329447746D) / (double) f2;
		double posZ = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks) / (double) f2
				+ 0.33000001311302185D;
		float f4 = world.provider.getCloudHeight() - posY + 0.33F;
		int i = MathHelper.floor_double(posX / 2048.0D);
		int j = MathHelper.floor_double(posZ / 2048.0D);
		posX -= (double) (i * 2048);
		posZ -= (double) (j * 2048);
		mc.renderEngine.bindTexture(locationCloudsPng);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		float f10 = 0.00390625F;
		float f8 = (float) MathHelper.floor_double(posX) * f10;
		float f9 = (float) MathHelper.floor_double(posZ) * f10;
		float f11 = (float) (posX - (double) MathHelper.floor_double(posX));
		float f12 = (float) (posZ - (double) MathHelper.floor_double(posZ));
		byte b0 = 8;
		byte b1 = 4;
		float f13 = 9.765625E-4F;
		GL11.glScalef(f2, 1.0F, f2);

		// k = passes?
		for (int k = 0; k < 2; ++k) {
			if (k == 0) {
				GL11.glColorMask(false, false, false, false);
			} else if (mc.gameSettings.anaglyph) {
				if (EntityRenderer.anaglyphField == 0) {
					GL11.glColorMask(false, true, true, true);
				} else {
					GL11.glColorMask(true, false, false, true);
				}
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for (int l = -b1 + 1; l <= b1; ++l) {
				for (int i1 = -b1 + 1; i1 <= b1; ++i1) {
					float f14 = (float) (l * b0);
					float f15 = (float) (i1 * b0);
					float f16 = f14 - f11;
					float f17 = f15 - f12;

					final float scale = MathHelper.abs(i1) / (b1 * 2);
					drawCloudCube(f16, f4, f17, Color.scale(cloudColor, scale), alpha);
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
