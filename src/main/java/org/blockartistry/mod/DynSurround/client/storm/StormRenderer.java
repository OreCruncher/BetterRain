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

package org.blockartistry.mod.DynSurround.client.storm;

import org.blockartistry.mod.DynSurround.Module;
import org.blockartistry.mod.DynSurround.client.IAtmosRenderer;
import org.blockartistry.mod.DynSurround.client.WeatherUtils;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.util.Color;
import org.blockartistry.mod.DynSurround.util.random.XorShiftRandom;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.IRenderHandler;

@SideOnly(Side.CLIENT)
public class StormRenderer implements IAtmosRenderer {

	private static final XorShiftRandom random = new XorShiftRandom();

	public static ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
	public static ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
	public static ResourceLocation locationDustPng = new ResourceLocation(Module.MOD_ID,
			"textures/environment/dust.png");

	private static final float[] RAIN_X_COORDS = new float[1024];
	private static final float[] RAIN_Y_COORDS = new float[1024];

	static {
		for (int i = 0; i < 32; ++i) {
			for (int j = 0; j < 32; ++j) {
				final float f2 = j - 16;
				final float f3 = i - 16;
				final float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);
				RAIN_X_COORDS[i << 5 | j] = -f3 / f4;
				RAIN_Y_COORDS[i << 5 | j] = f2 / f4;
			}
		}
	}

	private static int getPrecipitationHeight(final World world, final int x, final int z) {
		if (world.provider.dimensionId == -1)
			return 0;
		return world.getPrecipitationHeight(x, z);
	}

	@Override
	public void render(final EntityRenderer renderer, final float partialTicks) {
		// Set our rain/snow/dust textures
		StormProperties.setTextures();
		final WorldClient world = renderer.mc.theWorld;

		IRenderHandler r = null;
		if ((r = world.provider.getWeatherRenderer()) != null) {
			r.render(partialTicks, world, renderer.mc);
			return;
		}

		if (!DimensionRegistry.hasWeather(world))
			return;

		final float rainStrength = world.getRainStrength(partialTicks);
		if (rainStrength <= 0.0F)
			return;

		final float alphaRatio;
		if (StormProperties.isRaining())
			alphaRatio = StormProperties.getIntensityLevel() / StormProperties.getMaxIntensityLevel();
		else
			alphaRatio = rainStrength;

		renderer.enableLightmap(partialTicks);

		final EntityLivingBase entity = renderer.mc.renderViewEntity;
		final int playerX = MathHelper.floor_double(entity.posX);
		final int playerY = MathHelper.floor_double(entity.posY);
		final int playerZ = MathHelper.floor_double(entity.posZ);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

		final double spawnX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		final double spawnY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		final double spawnZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		final int locY = MathHelper.floor_double(spawnY);

		final int b0 = renderer.mc.gameSettings.fancyGraphics ? 10 : 5;

		byte b1 = -1;
		final float f5 = renderer.rendererUpdateCount + partialTicks;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		final Tessellator tessellator = Tessellator.instance;
		for (int locZ = playerZ - b0; locZ <= playerZ + b0; ++locZ) {
			for (int locX = playerX - b0; locX <= playerX + b0; ++locX) {
				final int idx = (locZ - playerZ + 16) * 32 + locX - playerX + 16;
				final float f6 = RAIN_X_COORDS[idx] * 0.5F;
				final float f7 = RAIN_Y_COORDS[idx] * 0.5F;
				final BiomeGenBase biome = world.getBiomeGenForCoords(locX, locZ);
				final boolean hasDust = WeatherUtils.biomeHasDust(biome);

				if (hasDust || BiomeRegistry.hasPrecipitation(biome)) {
					final int k1 = getPrecipitationHeight(world, locX, locZ);
					int l1 = playerY - b0;
					int i2 = playerY + b0;

					if (l1 < k1) {
						l1 = k1;
					}

					if (i2 < k1) {
						i2 = k1;
					}

					final float f8 = 1.0F;
					int j2 = k1;

					if (k1 < locY) {
						j2 = locY;
					}

					if (l1 != i2) {
						random.setSeed(locX * locX * 3121 + locX * 45238971 ^ locZ * locZ * 418711 + locZ * 13761);

						final float heightTemp = world.getWorldChunkManager()
								.getTemperatureAtHeight(biome.getFloatTemperature(locX, l1, locZ), k1);
						float f10;

						if (!hasDust && heightTemp >= 0.15F) {
							if (b1 != 0) {
								if (b1 >= 0) {
									tessellator.draw();
								}

								b1 = 0;
								renderer.mc.getTextureManager().bindTexture(locationRainPng);
								tessellator.startDrawingQuads();
							}

							f10 = ((renderer.rendererUpdateCount + locX * locX * 3121 + locX * 45238971
									+ locZ * locZ * 418711 + locZ * 13761 & 31) + partialTicks) / 32.0F
									* (3.0F + random.nextFloat());
							final double deltaX = locX + 0.5F - entity.posX;
							final double deltaZ = locZ + 0.5F - entity.posZ;
							final float dist = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) / b0;
							tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(locX, j2, locZ, 0));

							tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F,
									((1.0F - dist * dist) * 0.5F + 0.5F) * alphaRatio);
							tessellator.setTranslation(-spawnX * 1.0D, -spawnY * 1.0D, -spawnZ * 1.0D);
							tessellator.addVertexWithUV(locX - f6 + 0.5D, l1, locZ - f7 + 0.5D, 0.0F * f8,
									l1 * f8 / 4.0F + f10 * f8);
							tessellator.addVertexWithUV(locX + f6 + 0.5D, l1, locZ + f7 + 0.5D, 1.0F * f8,
									l1 * f8 / 4.0F + f10 * f8);
							tessellator.addVertexWithUV(locX + f6 + 0.5D, i2, locZ + f7 + 0.5D, 1.0F * f8,
									i2 * f8 / 4.0F + f10 * f8);
							tessellator.addVertexWithUV(locX - f6 + 0.5D, i2, locZ - f7 + 0.5D, 0.0F * f8,
									i2 * f8 / 4.0F + f10 * f8);
							tessellator.setTranslation(0.0D, 0.0D, 0.0D);
						} else {
							if (b1 != 1) {
								if (b1 >= 0) {
									tessellator.draw();
								}

								// If cold enough the dust texture will be
								// snow that blows sideways
								ResourceLocation texture = locationSnowPng;
								if (hasDust && heightTemp >= 0.15F)
									texture = locationDustPng;
								b1 = 1;
								renderer.mc.getTextureManager().bindTexture(texture);
								tessellator.startDrawingQuads();
							}

							f10 = ((renderer.rendererUpdateCount & 511) + partialTicks) / 512.0F;
							// The 0.2F factor was originally 0.01F. It
							// affects the horizontal
							// movement of particles, which works well for
							// dust.
							final float factor = hasDust ? 0.2F : 0.01F;
							final float f16 = random.nextFloat() + f5 * factor * (float) random.nextGaussian();
							final float f11 = random.nextFloat() + f5 * (float) random.nextGaussian() * 0.001F;

							final double deltaX = locX + 0.5F - entity.posX;
							final double deltaZ = locZ + 0.5F - entity.posZ;
							final float dist = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) / b0;
							tessellator.setBrightness(
									(world.getLightBrightnessForSkyBlocks(locX, j2, locZ, 0) * 3 + 15728880) / 4);

							final Color color = new Color(1.0F, 1.0F, 1.0F);
							if (world.provider.dimensionId == -1) {
								final Color c = BiomeRegistry.getDustColor(biome);
								if (c != null)
									color.mix(c);
							}
							tessellator.setColorRGBA_F(color.red, color.green, color.blue,
									((1.0F - dist * dist) * 0.3F + 0.5F) * alphaRatio);
							tessellator.setTranslation(-spawnX * 1.0D, -spawnY * 1.0D, -spawnZ * 1.0D);
							tessellator.addVertexWithUV(locX - f6 + 0.5D, l1, locZ - f7 + 0.5D, 0.0F * f8 + f16,
									l1 * f8 / 4.0F + f10 * f8 + f11);
							tessellator.addVertexWithUV(locX + f6 + 0.5D, l1, locZ + f7 + 0.5D, 1.0F * f8 + f16,
									l1 * f8 / 4.0F + f10 * f8 + f11);
							tessellator.addVertexWithUV(locX + f6 + 0.5D, i2, locZ + f7 + 0.5D, 1.0F * f8 + f16,
									i2 * f8 / 4.0F + f10 * f8 + f11);
							tessellator.addVertexWithUV(locX - f6 + 0.5D, i2, locZ - f7 + 0.5D, 0.0F * f8 + f16,
									i2 * f8 / 4.0F + f10 * f8 + f11);
							tessellator.setTranslation(0.0D, 0.0D, 0.0D);
						}
					}
				}
			}
		}

		if (b1 >= 0) {
			tessellator.draw();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		renderer.disableLightmap(partialTicks);
	}
}
