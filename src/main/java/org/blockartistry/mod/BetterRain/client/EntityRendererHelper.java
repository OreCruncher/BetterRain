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

import org.blockartistry.mod.BetterRain.BetterRain;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraftforge.client.IRenderHandler;

public final class EntityRendererHelper {
	
	private static final boolean DESERT_DUST = ModOptions.getAllowDesertDust();
	
	// Dust in the desert
    public static ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
    public static ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
	public static ResourceLocation locationDustPng = new ResourceLocation(BetterRain.MOD_ID, "textures/environment/dust.png");
	
    /**
     * Render rain and snow
     */
    public static void renderRainSnow(final EntityRenderer theThis, final float particleTicks)
    {
        IRenderHandler renderer = null;
        if ((renderer = theThis.mc.theWorld.provider.getWeatherRenderer()) != null)
        {
            renderer.render(particleTicks, theThis.mc.theWorld, theThis.mc);
            return;
        }

        float f1 = theThis.mc.theWorld.getRainStrength(particleTicks);

        if (f1 > 0.0F)
        {
            theThis.enableLightmap((double)particleTicks);

            if (theThis.rainXCoords == null)
            {
                theThis.rainXCoords = new float[1024];
                theThis.rainYCoords = new float[1024];

                for (int i = 0; i < 32; ++i)
                {
                    for (int j = 0; j < 32; ++j)
                    {
                        float f2 = (float)(j - 16);
                        float f3 = (float)(i - 16);
                        float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);
                        theThis.rainXCoords[i << 5 | j] = -f3 / f4;
                        theThis.rainYCoords[i << 5 | j] = f2 / f4;
                    }
                }
            }

            EntityLivingBase entitylivingbase = theThis.mc.renderViewEntity;
            WorldClient worldclient = theThis.mc.theWorld;
            int k2 = MathHelper.floor_double(entitylivingbase.posX);
            int l2 = MathHelper.floor_double(entitylivingbase.posY);
            int i3 = MathHelper.floor_double(entitylivingbase.posZ);
            Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            double d0 = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * (double)particleTicks;
            double d1 = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * (double)particleTicks;
            double d2 = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * (double)particleTicks;
            int k = MathHelper.floor_double(d1);
            byte b0 = 5;

            if (theThis.mc.gameSettings.fancyGraphics)
            {
                b0 = 10;
            }

            byte b1 = -1;
            float f5 = (float)theThis.rendererUpdateCount + particleTicks;

            if (theThis.mc.gameSettings.fancyGraphics)
            {
                b0 = 10;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            for (int l = i3 - b0; l <= i3 + b0; ++l)
            {
                for (int i1 = k2 - b0; i1 <= k2 + b0; ++i1)
                {
                    int j1 = (l - i3 + 16) * 32 + i1 - k2 + 16;
                    float f6 = theThis.rainXCoords[j1] * 0.5F;
                    float f7 = theThis.rainYCoords[j1] * 0.5F;
                    final BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(i1, l);
                    final boolean isDesert = DESERT_DUST && biomegenbase instanceof BiomeGenDesert && !RainIntensity.doVanillaRain();

                    if (isDesert || biomegenbase.canSpawnLightningBolt() || biomegenbase.getEnableSnow())
                    {
                        int k1 = worldclient.getPrecipitationHeight(i1, l);
                        int l1 = l2 - b0;
                        int i2 = l2 + b0;

                        if (l1 < k1)
                        {
                            l1 = k1;
                        }

                        if (i2 < k1)
                        {
                            i2 = k1;
                        }

                        float f8 = 1.0F;
                        int j2 = k1;

                        if (k1 < k)
                        {
                            j2 = k;
                        }

                        if (l1 != i2)
                        {
                            theThis.random.setSeed((long)(i1 * i1 * 3121 + i1 * 45238971 ^ l * l * 418711 + l * 13761));
                            float f9 = biomegenbase.getFloatTemperature(i1, l1, l);
                            float f10;
                            double d4;

                            if (!isDesert && worldclient.getWorldChunkManager().getTemperatureAtHeight(f9, k1) >= 0.15F)
                            {
                                if (b1 != 0)
                                {
                                    if (b1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    b1 = 0;
                                    theThis.mc.getTextureManager().bindTexture(locationRainPng);
                                    tessellator.startDrawingQuads();
                                }

                                f10 = ((float)(theThis.rendererUpdateCount + i1 * i1 * 3121 + i1 * 45238971 + l * l * 418711 + l * 13761 & 31) + particleTicks) / 32.0F * (3.0F + theThis.random.nextFloat());
                                double d3 = (double)((float)i1 + 0.5F) - entitylivingbase.posX;
                                d4 = (double)((float)l + 0.5F) - entitylivingbase.posZ;
                                float f12 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / (float)b0;
                                float f13 = 1.0F;
                                tessellator.setBrightness(worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0));
                                tessellator.setColorRGBA_F(f13, f13, f13, ((1.0F - f12 * f12) * 0.5F + 0.5F) * f1);
                                tessellator.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                                tessellator.addVertexWithUV((double)((float)i1 - f6) + 0.5D, (double)l1, (double)((float)l - f7) + 0.5D, (double)(0.0F * f8), (double)((float)l1 * f8 / 4.0F + f10 * f8));
                                tessellator.addVertexWithUV((double)((float)i1 + f6) + 0.5D, (double)l1, (double)((float)l + f7) + 0.5D, (double)(1.0F * f8), (double)((float)l1 * f8 / 4.0F + f10 * f8));
                                tessellator.addVertexWithUV((double)((float)i1 + f6) + 0.5D, (double)i2, (double)((float)l + f7) + 0.5D, (double)(1.0F * f8), (double)((float)i2 * f8 / 4.0F + f10 * f8));
                                tessellator.addVertexWithUV((double)((float)i1 - f6) + 0.5D, (double)i2, (double)((float)l - f7) + 0.5D, (double)(0.0F * f8), (double)((float)i2 * f8 / 4.0F + f10 * f8));
                                tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                            }
                            else
                            {
                                if (b1 != 1)
                                {
                                    if (b1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    b1 = 1;
                                    theThis.mc.getTextureManager().bindTexture(isDesert ? locationDustPng : locationSnowPng);
                                    tessellator.startDrawingQuads();
                                }

                                f10 = ((float)(theThis.rendererUpdateCount & 511) + particleTicks) / 512.0F;
                                // The 0.2F factor was originally 0.01F.  It affects the horizontal
                                // movement of particles, which works well for dust.
                                final float factor = isDesert ? 0.2F : 0.01F;
                                float f16 = theThis.random.nextFloat() + f5 * factor * (float)theThis.random.nextGaussian();
                                float f11 = theThis.random.nextFloat() + f5 * (float)theThis.random.nextGaussian() * 0.001F;
                                d4 = (double)((float)i1 + 0.5F) - entitylivingbase.posX;
                                double d5 = (double)((float)l + 0.5F) - entitylivingbase.posZ;
                                float f14 = MathHelper.sqrt_double(d4 * d4 + d5 * d5) / (float)b0;
                                float f15 = 1.0F;
                                tessellator.setBrightness((worldclient.getLightBrightnessForSkyBlocks(i1, j2, l, 0) * 3 + 15728880) / 4);
                                tessellator.setColorRGBA_F(f15, f15, f15, ((1.0F - f14 * f14) * 0.3F + 0.5F) * f1);
                                tessellator.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
                                tessellator.addVertexWithUV((double)((float)i1 - f6) + 0.5D, (double)l1, (double)((float)l - f7) + 0.5D, (double)(0.0F * f8 + f16), (double)((float)l1 * f8 / 4.0F + f10 * f8 + f11));
                                tessellator.addVertexWithUV((double)((float)i1 + f6) + 0.5D, (double)l1, (double)((float)l + f7) + 0.5D, (double)(1.0F * f8 + f16), (double)((float)l1 * f8 / 4.0F + f10 * f8 + f11));
                                tessellator.addVertexWithUV((double)((float)i1 + f6) + 0.5D, (double)i2, (double)((float)l + f7) + 0.5D, (double)(1.0F * f8 + f16), (double)((float)i2 * f8 / 4.0F + f10 * f8 + f11));
                                tessellator.addVertexWithUV((double)((float)i1 - f6) + 0.5D, (double)i2, (double)((float)l - f7) + 0.5D, (double)(0.0F * f8 + f16), (double)((float)i2 * f8 / 4.0F + f10 * f8 + f11));
                                tessellator.setTranslation(0.0D, 0.0D, 0.0D);
                            }
                        }
                    }
                }
            }

            if (b1 >= 0)
            {
                tessellator.draw();
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            theThis.disableLightmap((double)particleTicks);
        }
    }

}
