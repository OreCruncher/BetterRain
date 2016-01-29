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

import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.util.Color;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class CloudRenderer extends IRenderHandler {

	private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");

	private static final CloudRenderer INSTANCE = new CloudRenderer();

	public static void initialize() {
		FMLCommonHandler.instance().bus().register(INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void preRenderCheck(final RenderTickEvent event) {
		final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (player == null)
			return;

		final World world = player.worldObj;
		if (world == null)
			return;

		if (event.phase == Phase.START && world.provider.getCloudRenderer() == null) {
			world.provider.setCloudRenderer(INSTANCE);
		}
	}
	
	private Color getCloudColor(final World world, final float partialTicks) {
		final Color color = new Color(world.getCloudColour(partialTicks));
		final float stormIntensity = world.getRainStrength(1.0F);
		if(stormIntensity > 0.0F) {
			// Need to darken the clouds based on intensity
			final float scale = (1.0F - stormIntensity) * 0.75F + 0.25F;
			color.scale(scale);
		}
		return color;
	}

	@Override
	public void render(final float partialTicks, final WorldClient world, final Minecraft mc) {
		if (!world.provider.isSurfaceWorld())
			return;

		if (mc.gameSettings.fancyGraphics) {
			this.renderCloudsFancy(partialTicks);
		} else {
			GL11.glDisable(GL11.GL_CULL_FACE);
			float f1 = (float) (mc.renderViewEntity.lastTickPosY
					+ (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * (double) partialTicks);
			byte b0 = 32;
			int i = 256 / b0;
			Tessellator tessellator = Tessellator.instance;
			mc.renderEngine.bindTexture(locationCloudsPng);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			final Color color = getCloudColor(world, partialTicks);
			float red = color.red;
			float green = color.green;
			float blue = color.blue;
			float f5;

			if (mc.gameSettings.anaglyph) {
				f5 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
				float f6 = (red * 30.0F + green * 70.0F) / 100.0F;
				float f7 = (red * 30.0F + blue * 70.0F) / 100.0F;
				red = f5;
				green = f6;
				blue = f7;
			}

			f5 = 4.8828125E-4F;
			double d2 = (double) ((float) mc.renderGlobal.cloudTickCounter + partialTicks);
			double d0 = mc.renderViewEntity.prevPosX
					+ (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * (double) partialTicks
					+ d2 * 0.029999999329447746D;
			double d1 = mc.renderViewEntity.prevPosZ
					+ (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * (double) partialTicks;
			int j = MathHelper.floor_double(d0 / 2048.0D);
			int k = MathHelper.floor_double(d1 / 2048.0D);
			d0 -= (double) (j * 2048);
			d1 -= (double) (k * 2048);
			float f8 = DimensionRegistry.getCloudHeight(world) - f1 + 0.33F;
			float f9 = (float) (d0 * (double) f5);
			float f10 = (float) (d1 * (double) f5);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_F(red, green, blue, 0.8F);

			for (int l = -b0 * i; l < b0 * i; l += b0) {
				for (int i1 = -b0 * i; i1 < b0 * i; i1 += b0) {
					tessellator.addVertexWithUV((double) (l + 0), (double) f8, (double) (i1 + b0),
							(double) ((float) (l + 0) * f5 + f9), (double) ((float) (i1 + b0) * f5 + f10));
					tessellator.addVertexWithUV((double) (l + b0), (double) f8, (double) (i1 + b0),
							(double) ((float) (l + b0) * f5 + f9), (double) ((float) (i1 + b0) * f5 + f10));
					tessellator.addVertexWithUV((double) (l + b0), (double) f8, (double) (i1 + 0),
							(double) ((float) (l + b0) * f5 + f9), (double) ((float) (i1 + 0) * f5 + f10));
					tessellator.addVertexWithUV((double) (l + 0), (double) f8, (double) (i1 + 0),
							(double) ((float) (l + 0) * f5 + f9), (double) ((float) (i1 + 0) * f5 + f10));
				}
			}

			tessellator.draw();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
		}

	}

	public void renderCloudsFancy(float partialTicks) {
		final Minecraft mc = Minecraft.getMinecraft();
		final World world = mc.theWorld;

		GL11.glDisable(GL11.GL_CULL_FACE);
		float f1 = (float) (mc.renderViewEntity.lastTickPosY
				+ (mc.renderViewEntity.posY - mc.renderViewEntity.lastTickPosY) * (double) partialTicks);
		Tessellator tessellator = Tessellator.instance;
		float f2 = 12.0F;
		float f3 = 4.0F;
		double d0 = (double) ((float) mc.renderGlobal.cloudTickCounter + partialTicks);
		double d1 = (mc.renderViewEntity.prevPosX
				+ (mc.renderViewEntity.posX - mc.renderViewEntity.prevPosX) * (double) partialTicks
				+ d0 * 0.029999999329447746D) / (double) f2;
		double d2 = (mc.renderViewEntity.prevPosZ
				+ (mc.renderViewEntity.posZ - mc.renderViewEntity.prevPosZ) * (double) partialTicks) / (double) f2
				+ 0.33000001311302185D;
		float f4 = DimensionRegistry.getCloudHeight(world) - f1 + 0.33F;
		int i = MathHelper.floor_double(d1 / 2048.0D);
		int j = MathHelper.floor_double(d2 / 2048.0D);
		d1 -= (double) (i * 2048);
		d2 -= (double) (j * 2048);
		mc.renderEngine.bindTexture(locationCloudsPng);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		final Color color = getCloudColor(world, partialTicks);
		float red = color.red;
		float green = color.green;
		float blue = color.blue;
		float f8;
		float f9;
		float f10;

		if (mc.gameSettings.anaglyph) {
			f8 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			f9 = (red * 30.0F + green * 70.0F) / 100.0F;
			f10 = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = f8;
			green = f9;
			blue = f10;
		}

		f8 = (float) (d1 * 0.0D);
		f9 = (float) (d2 * 0.0D);
		f10 = 0.00390625F;
		f8 = (float) MathHelper.floor_double(d1) * f10;
		f9 = (float) MathHelper.floor_double(d2) * f10;
		float f11 = (float) (d1 - (double) MathHelper.floor_double(d1));
		float f12 = (float) (d2 - (double) MathHelper.floor_double(d2));
		byte b0 = 8;
		byte b1 = 4;
		float f13 = 9.765625E-4F;
		GL11.glScalef(f2, 1.0F, f2);

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
					tessellator.startDrawingQuads();
					float f14 = (float) (l * b0);
					float f15 = (float) (i1 * b0);
					float f16 = f14 - f11;
					float f17 = f15 - f12;

					if (f4 > -f3 - 1.0F) {
						tessellator.setColorRGBA_F(red * 0.7F, green * 0.7F, blue * 0.7F, 0.8F);
						tessellator.setNormal(0.0F, -1.0F, 0.0F);
						tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + 0.0F),
								(double) (f17 + (float) b0), (double) ((f14 + 0.0F) * f10 + f8),
								(double) ((f15 + (float) b0) * f10 + f9));
						tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + 0.0F),
								(double) (f17 + (float) b0), (double) ((f14 + (float) b0) * f10 + f8),
								(double) ((f15 + (float) b0) * f10 + f9));
						tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + 0.0F),
								(double) (f17 + 0.0F), (double) ((f14 + (float) b0) * f10 + f8),
								(double) ((f15 + 0.0F) * f10 + f9));
						tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + 0.0F), (double) (f17 + 0.0F),
								(double) ((f14 + 0.0F) * f10 + f8), (double) ((f15 + 0.0F) * f10 + f9));
					}

					if (f4 <= f3 + 1.0F) {
						tessellator.setColorRGBA_F(red, green, blue, 0.8F);
						tessellator.setNormal(0.0F, 1.0F, 0.0F);
						tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + f3 - f13),
								(double) (f17 + (float) b0), (double) ((f14 + 0.0F) * f10 + f8),
								(double) ((f15 + (float) b0) * f10 + f9));
						tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + f3 - f13),
								(double) (f17 + (float) b0), (double) ((f14 + (float) b0) * f10 + f8),
								(double) ((f15 + (float) b0) * f10 + f9));
						tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + f3 - f13),
								(double) (f17 + 0.0F), (double) ((f14 + (float) b0) * f10 + f8),
								(double) ((f15 + 0.0F) * f10 + f9));
						tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + f3 - f13),
								(double) (f17 + 0.0F), (double) ((f14 + 0.0F) * f10 + f8),
								(double) ((f15 + 0.0F) * f10 + f9));
					}

					tessellator.setColorRGBA_F(red * 0.9F, green * 0.9F, blue * 0.9F, 0.8F);
					int j1;

					if (l > -1) {
						tessellator.setNormal(-1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 0.0F), (double) (f4 + 0.0F),
									(double) (f17 + (float) b0), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + (float) b0) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 0.0F), (double) (f4 + f3),
									(double) (f17 + (float) b0), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + (float) b0) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 0.0F), (double) (f4 + f3),
									(double) (f17 + 0.0F), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + 0.0F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 0.0F), (double) (f4 + 0.0F),
									(double) (f17 + 0.0F), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + 0.0F) * f10 + f9));
						}
					}

					if (l <= 1) {
						tessellator.setNormal(1.0F, 0.0F, 0.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 1.0F - f13), (double) (f4 + 0.0F),
									(double) (f17 + (float) b0), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + (float) b0) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 1.0F - f13), (double) (f4 + f3),
									(double) (f17 + (float) b0), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + (float) b0) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 1.0F - f13), (double) (f4 + f3),
									(double) (f17 + 0.0F), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + 0.0F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) j1 + 1.0F - f13), (double) (f4 + 0.0F),
									(double) (f17 + 0.0F), (double) ((f14 + (float) j1 + 0.5F) * f10 + f8),
									(double) ((f15 + 0.0F) * f10 + f9));
						}
					}

					tessellator.setColorRGBA_F(red * 0.8F, green * 0.8F, blue * 0.8F, 0.8F);

					if (i1 > -1) {
						tessellator.setNormal(0.0F, 0.0F, -1.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + f3),
									(double) (f17 + (float) j1 + 0.0F), (double) ((f14 + 0.0F) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + f3),
									(double) (f17 + (float) j1 + 0.0F), (double) ((f14 + (float) b0) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + 0.0F),
									(double) (f17 + (float) j1 + 0.0F), (double) ((f14 + (float) b0) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + 0.0F),
									(double) (f17 + (float) j1 + 0.0F), (double) ((f14 + 0.0F) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
						}
					}

					if (i1 <= 1) {
						tessellator.setNormal(0.0F, 0.0F, 1.0F);

						for (j1 = 0; j1 < b0; ++j1) {
							tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + f3),
									(double) (f17 + (float) j1 + 1.0F - f13), (double) ((f14 + 0.0F) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + f3),
									(double) (f17 + (float) j1 + 1.0F - f13), (double) ((f14 + (float) b0) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + (float) b0), (double) (f4 + 0.0F),
									(double) (f17 + (float) j1 + 1.0F - f13), (double) ((f14 + (float) b0) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
							tessellator.addVertexWithUV((double) (f16 + 0.0F), (double) (f4 + 0.0F),
									(double) (f17 + (float) j1 + 1.0F - f13), (double) ((f14 + 0.0F) * f10 + f8),
									(double) ((f15 + (float) j1 + 0.5F) * f10 + f9));
						}
					}

					tessellator.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
