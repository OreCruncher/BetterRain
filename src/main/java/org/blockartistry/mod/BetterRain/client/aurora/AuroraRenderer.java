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

package org.blockartistry.mod.BetterRain.client.aurora;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;

import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.client.ClientEffectHandler;
import org.blockartistry.mod.BetterRain.client.IAtmosRenderer;
import org.blockartistry.mod.BetterRain.util.Color;
import org.blockartistry.mod.BetterRain.util.WorldUtils;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class AuroraRenderer implements IAtmosRenderer {

	private static final boolean ANIMATE = ModOptions.getAuroraAnimate();
	private static final boolean HEIGHT_PLAYER_RELATIVE = ModOptions.getAuroraHeightPlayerRelative();
	private static final float PLAYER_FIXED_HEIGHT = ModOptions.getPlayerFixedHeight();

	@Override
	public void render(final EntityRenderer renderer, final float partialTick) {
		if (ClientEffectHandler.currentAurora != null) {
			renderAurora(partialTick, ClientEffectHandler.currentAurora);
		}
	}

	public static void renderAurora(final float partialTick, final Aurora aurora) {
		final Tessellator tess = Tessellator.getInstance();
		final WorldRenderer renderer = tess.getWorldRenderer();
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		final float tranY;
		if (HEIGHT_PLAYER_RELATIVE) {
			// Fix height above player
			tranY = PLAYER_FIXED_HEIGHT;
		} else {
			// Adjust to keep aurora at the same altitude
			tranY = WorldUtils.getCloudHeight(minecraft.theWorld) + 5 - (float) (minecraft.thePlayer.lastTickPosY
					+ (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY) * partialTick);
		}

		final double tranX = aurora.posX - (minecraft.thePlayer.lastTickPosX
				+ (minecraft.thePlayer.posX - minecraft.thePlayer.lastTickPosX) * partialTick);

		final double tranZ = aurora.posZ - (minecraft.thePlayer.lastTickPosZ
				+ (minecraft.thePlayer.posZ - minecraft.thePlayer.lastTickPosZ) * partialTick);

		if (ANIMATE)
			aurora.translate(partialTick);

		final Color base = aurora.getBaseColor();
		final Color fade = aurora.getFadeColor();
		final int alpha = aurora.getAlpha();
		final double lowY = 0.0D;
		final double lowY2 = 0.0D;

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) tranX, tranY, (float) tranZ);
		GlStateManager.scale(0.5D, 8.0D, 0.5D);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);

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

				renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				renderer.pos(posX, lowY, posZ).color(base.red, base.blue, base.green, alpha).endVertex();
				renderer.pos(posX, posY, posZ).color(fade.red, fade.green, fade.blue, 0).endVertex();
				renderer.pos(posX2, posY2, posZ2).color(fade.red, fade.green, fade.blue, 0).endVertex();
				renderer.pos(posX2, lowY2, posZ2).color(base.red, base.blue, base.green, alpha).endVertex();
				tess.draw();
				
				// tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				// setColor(base, alpha);
				// tess.addVertex(posX, lowY, posZ);
				// setColor(fade, 0);
				// tess.addVertex(posX, posY, posZ);
				// tess.addVertex(posX2, posY2, posZ2);
				// setColor(base, alpha);
				// tess.addVertex(posX2, lowY2, posZ2);
				// tess.draw();

				renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				renderer.pos(posX, lowY, posZ).color(base.red, base.green, base.blue, alpha).endVertex();
				renderer.pos(posX2, lowY2, posZ2).color(base.red, base.green, base.blue, alpha).endVertex();
				renderer.pos(tetX2, lowY2, tetZ2).color(base.red, base.green, base.blue, alpha).endVertex();
				renderer.pos(tetX, lowY, tetZ).color(base.red, base.green, base.blue, alpha).endVertex();
				tess.draw();

				// tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				// setColor(base, alpha);
				// tess.addVertex(posX, lowY, posZ);
				// tess.addVertex(posX2, lowY2, posZ2);
				// tess.addVertex(tetX2, lowY2, tetZ2);
				// tess.addVertex(tetX, lowY, tetZ);
				// tess.draw();

				renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				renderer.pos(tetX, lowY, tetZ).color(base.red, base.green, base.blue, alpha).endVertex();
				renderer.pos(tetX, posY, tetZ).color(fade.red, fade.blue, fade.green, 0).endVertex();
				renderer.pos(tetX2, posY2, tetZ2).color(fade.red, fade.blue, fade.green, 0).endVertex();
				renderer.pos(tetX2, lowY2, tetZ2).color(base.red, base.green, base.blue, alpha).endVertex();
				tess.draw();

				// tess.startDrawing(GL11.GL_TRIANGLE_FAN);
				// setColor(base, alpha);
				// tess.addVertex(tetX, lowY, tetZ);
				// setColor(fade, 0);
				// tess.addVertex(tetX, posY, tetZ);
				// tess.addVertex(tetX2, posY2, tetZ2);
				// setColor(base, alpha);
				// tess.addVertex(tetX2, lowY2, tetZ2);
				// tess.draw();
			}
		}

		GlStateManager.scale(3.5D, 25.0D, 3.5D);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.popMatrix();
	}
}
