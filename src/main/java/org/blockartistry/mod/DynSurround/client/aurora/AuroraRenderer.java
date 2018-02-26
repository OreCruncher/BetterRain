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

package org.blockartistry.mod.DynSurround.client.aurora;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.AuroraEffectHandler;
import org.blockartistry.mod.DynSurround.client.IAtmosRenderer;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.DiurnalUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public final class AuroraRenderer implements IAtmosRenderer {

	@Override
	public void render(final EntityRenderer renderer, final float partialTick) {
		if (AuroraEffectHandler.currentAurora != null) {
			renderAurora(partialTick, AuroraEffectHandler.currentAurora);
		}
	}

	private static void setColor(final Color color, final float alpha) {
		Tessellator.instance.setColorRGBA_F(color.red, color.green, color.blue, alpha);
	}

	public static float moonlightFactor(final World world) {
		final float moonFactor = 1.0F - DiurnalUtils.getMoonPhaseFactor(world) * 1.1F;
		if (moonFactor <= 0.0F)
			return 0.0F;
		return MathHelper.clamp_float(moonFactor * moonFactor, 0.0F, 1.0F);
	}

	public static void renderAurora(final float partialTick, final Aurora aurora) {

		final Minecraft mc = FMLClientHandler.instance().getClient();
		final float alpha = (aurora.getAlpha() * moonlightFactor(mc.theWorld)) / 255.0F;
		if (alpha <= 0)
			return;

		final Tessellator tess = Tessellator.instance;
		final float tranY;
		if (ModOptions.auroraHeightPlayerRelative) {
			// Fix height above player
			tranY = ModOptions.playerFixedHeight;
		} else {
			// Adjust to keep aurora at the same altitude
			tranY = DimensionRegistry.getCloudHeight(mc.theWorld) + 5 - (float) (mc.thePlayer.lastTickPosY
					+ (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTick);
		}

		final double tranX = aurora.posX
				- (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTick);

		final double tranZ = aurora.posZ
				- (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTick);

		if (ModOptions.auroraAnimate)
			aurora.translate(partialTick);

		final Color base = aurora.getBaseColor();
		final Color fade = aurora.getFadeColor();
		final double lowY = 0.0D;
		final double lowY2 = 0.0D;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) tranX, tranY, (float) tranZ);
		GL11.glScaled(0.5D, 8.0D, 0.5D);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, 1);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);

		for (final Node[] array : aurora.getNodeList()) {
			for (int i = 0; i < array.length - 1; i++) {

				final Node node = array[i];

				final double posY = node.getModdedY();
				final double posX = node.tetX;
				final double posZ = node.tetZ;
				final double tetX = node.tetX2;
				final double tetZ = node.tetZ2;

				final double posX2;
				final double posZ2;
				final double tetX2;
				final double tetZ2;
				final double posY2;

				if (i < array.length - 2) {
					final Node nodePlus = array[i + 1];
					posX2 = nodePlus.tetX;
					posZ2 = nodePlus.tetZ;
					tetX2 = nodePlus.tetX2;
					tetZ2 = nodePlus.tetZ2;
					posY2 = nodePlus.getModdedY();
				} else {
					posX2 = tetX2 = node.posX;
					posZ2 = tetZ2 = node.getModdedZ();
					posY2 = 0.0D;
				}

				tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				setColor(base, alpha);
				tess.addVertex(posX, lowY, posZ);
				setColor(fade, 0);
				tess.addVertex(posX, posY, posZ);
				tess.addVertex(posX2, posY2, posZ2);
				setColor(base, alpha);
				tess.addVertex(posX2, lowY2, posZ2);
				tess.draw();

				tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				setColor(base, alpha);
				tess.addVertex(posX, lowY, posZ);
				tess.addVertex(posX2, lowY2, posZ2);
				tess.addVertex(tetX2, lowY2, tetZ2);
				tess.addVertex(tetX, lowY, tetZ);
				tess.draw();

				tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				setColor(base, alpha);
				tess.addVertex(tetX, lowY, tetZ);
				setColor(fade, 0);
				tess.addVertex(tetX, posY, tetZ);
				tess.addVertex(tetX2, posY2, tetZ2);
				setColor(base, alpha);
				tess.addVertex(tetX2, lowY2, tetZ2);
				tess.draw();
			}
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
