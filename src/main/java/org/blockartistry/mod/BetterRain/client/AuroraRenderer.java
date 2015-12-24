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

package org.blockartistry.mod.BetterRain.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.aurora.Color;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class AuroraRenderer {

	private static final boolean ANIMATE = true;
	private static final boolean FIXED_HEIGHT = ModOptions.getAuroraYTranslation();

	public static void render(final float partialTick) {
		if (ClientEffectHandler.currentAurora != null) {
			renderAurora(partialTick, ClientEffectHandler.currentAurora);
		}
	}

	private static void setColor(final Color color, final int alpha) {
		Tessellator.instance.setColorRGBA(color.red, color.green, color.blue, alpha);
	}

	public static void renderAurora(final float partialTick, final Aurora aurora) {
		final Tessellator tess = Tessellator.instance;
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		float var12 = 0.0F;
		if (FIXED_HEIGHT) {
			// Fix height above player
			var12 = 128.33F - 64.0F;
		} else {
			// Adjust to keep aurora at the same altitude
			var12 = 156.33F - (float) (minecraft.thePlayer.lastTickPosY
					+ (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY) * partialTick);
		}

		double var8 = aurora.posX - (minecraft.thePlayer.lastTickPosX
				+ (minecraft.thePlayer.posX - minecraft.thePlayer.lastTickPosX) * partialTick);

		double var10 = aurora.posZ - (minecraft.thePlayer.lastTickPosZ
				+ (minecraft.thePlayer.posZ - minecraft.thePlayer.lastTickPosZ) * partialTick);
		
		if(ANIMATE)
			aurora.translateArrays(partialTick);

		for (final Node[] array : aurora.getNodeList()) {

			GL11.glPushMatrix();
			GL11.glTranslatef((float) var8, var12, (float) var10);

			GL11.glScaled(0.5D, 8.0D, 0.5D);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, 1);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);

			for (int i = 0; i < array.length - 1; i++) {

				final Node node = array[i];
				final float[] tet = node.getTet();
				float[] tet2 = null;

				double posY2 = 0.0D;
				if (i < array.length - 2) {
					final Node nodePlus = array[i + 1];
					tet2 = nodePlus.getTet();
					posY2 = nodePlus.getModdedY();
				} else {
					tet2 = node.getTet2();
				}

				double posX = tet[0];
				double posX2 = tet2[0];
				double posZ = tet[2];
				double posZ2 = tet2[2];
				double posY = node.getModdedY();

				double lowY = 0.0D;
				double lowY2 = 0.0D;

				double tetX = tet[1];
				double tetX2 = tet2[1];
				double tetZ = tet[3];
				double tetZ2 = tet2[3];

				Color color1 = null;
				Color color2 = null;

				if (i == 0) {
					color1 = color2 = Color.WHITE;
				} else if (i == array.length - 2) {
					color1 = color2 = Color.YELLOW;
				} else {
					color1 = aurora.getColor1();
					color2 = aurora.getColor2();
				}

				tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				setColor(color1, node.alpha);
				tess.addVertex(posX, lowY, posZ);
				setColor(color2, 0);
				tess.addVertex(posX, posY, posZ);
				tess.addVertex(posX2, posY2, posZ2);
				setColor(color1, node.alpha);
				tess.addVertex(posX2, lowY2, posZ2);
				tess.draw();

				tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				setColor(color1, node.alpha);
				tess.addVertex(posX, lowY, posZ);
				tess.addVertex(posX2, lowY2, posZ2);
				tess.addVertex(tetX2, lowY2, tetZ2);
				tess.addVertex(tetX, lowY, tetZ);
				tess.draw();

				posX = tetX;
				posX2 = tetX2;
				posZ = tetZ;
				posZ2 = tetZ2;

				tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				setColor(color1, node.alpha);
				tess.addVertex(posX, lowY, posZ);
				setColor(color2, 0);
				tess.addVertex(posX, posY, posZ);
				tess.addVertex(posX2, posY2, posZ2);
				setColor(color1, node.alpha);
				tess.addVertex(posX2, lowY2, posZ2);
				tess.draw();
			}

			GL11.glScaled(3.5D, 25.0D, 3.5D);
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glPopMatrix();
		}
	}
}
