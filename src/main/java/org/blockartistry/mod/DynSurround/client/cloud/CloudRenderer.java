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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class CloudRenderer extends IRenderHandler {

	private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");

	private static final CloudRenderer INSTANCE = new CloudRenderer();

	public static void initialize() {
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
		if (stormIntensity > 0.0F) {
			// Need to darken the clouds based on intensity
			color.scale((1.0F - stormIntensity) * 0.5F + 0.5F);
		}
		return color;
	}

	@Override
	public void render(final float partialTicks, final WorldClient world, final Minecraft mc) {
		if (!world.provider.isSurfaceWorld())
			return;

		if (mc.gameSettings.func_181147_e() == 2) {
			this.renderCloudsFancy(partialTicks, 2);
		} else {
			GlStateManager.disableCull();
			float f = (float) (mc.getRenderViewEntity().lastTickPosY
					+ (mc.getRenderViewEntity().posY - mc.getRenderViewEntity().lastTickPosY) * (double) partialTicks);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			mc.renderEngine.bindTexture(locationCloudsPng);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			final Color color = getCloudColor(world, partialTicks);
			float red = color.red;
			float green = color.green;
			float blue = color.blue;

			double d2 = (double) ((float) mc.renderGlobal.cloudTickCounter + partialTicks);
			double d0 = mc.getRenderViewEntity().prevPosX
					+ (mc.getRenderViewEntity().posX - mc.getRenderViewEntity().prevPosX) * (double) partialTicks
					+ d2 * 0.029999999329447746D;
			double d1 = mc.getRenderViewEntity().prevPosZ
					+ (mc.getRenderViewEntity().posZ - mc.getRenderViewEntity().prevPosZ) * (double) partialTicks;
			int k = MathHelper.floor_double(d0 / 2048.0D);
			int l = MathHelper.floor_double(d1 / 2048.0D);
			d0 = d0 - (double) (k * 2048);
			d1 = d1 - (double) (l * 2048);
			float f7 = DimensionRegistry.getCloudHeight(world) - f + 0.33F;
			float f8 = (float) (d0 * 4.8828125E-4D);
			float f9 = (float) (d1 * 4.8828125E-4D);
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

			for (int i1 = -256; i1 < 256; i1 += 32) {
				for (int j1 = -256; j1 < 256; j1 += 32) {
					worldrenderer.pos((double) (i1 + 0), (double) f7, (double) (j1 + 32))
							.tex((double) ((float) (i1 + 0) * 4.8828125E-4F + f8),
									(double) ((float) (j1 + 32) * 4.8828125E-4F + f9))
							.color(red, green, blue, 0.8F).endVertex();
					worldrenderer.pos((double) (i1 + 32), (double) f7, (double) (j1 + 32))
							.tex((double) ((float) (i1 + 32) * 4.8828125E-4F + f8),
									(double) ((float) (j1 + 32) * 4.8828125E-4F + f9))
							.color(red, green, blue, 0.8F).endVertex();
					worldrenderer.pos((double) (i1 + 32), (double) f7, (double) (j1 + 0))
							.tex((double) ((float) (i1 + 32) * 4.8828125E-4F + f8),
									(double) ((float) (j1 + 0) * 4.8828125E-4F + f9))
							.color(red, green, blue, 0.8F).endVertex();
					worldrenderer.pos((double) (i1 + 0), (double) f7, (double) (j1 + 0))
							.tex((double) ((float) (i1 + 0) * 4.8828125E-4F + f8),
									(double) ((float) (j1 + 0) * 4.8828125E-4F + f9))
							.color(red, green, blue, 0.8F).endVertex();
				}
			}

			tessellator.draw();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableCull();
		}
	}

	public void renderCloudsFancy(final float partialTicks, final int pass) {
		final Minecraft mc = Minecraft.getMinecraft();
		final World world = mc.theWorld;

		GlStateManager.disableCull();
		float f = (float) (mc.getRenderViewEntity().lastTickPosY
				+ (mc.getRenderViewEntity().posY - mc.getRenderViewEntity().lastTickPosY) * (double) partialTicks);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		double d0 = (double) ((float) mc.renderGlobal.cloudTickCounter + partialTicks);
		double d1 = (mc.getRenderViewEntity().prevPosX
				+ (mc.getRenderViewEntity().posX - mc.getRenderViewEntity().prevPosX) * (double) partialTicks
				+ d0 * 0.029999999329447746D) / 12.0D;
		double d2 = (mc.getRenderViewEntity().prevPosZ
				+ (mc.getRenderViewEntity().posZ - mc.getRenderViewEntity().prevPosZ) * (double) partialTicks) / 12.0D
				+ 0.33000001311302185D;
		float f3 = DimensionRegistry.getCloudHeight(world) - f + 0.33F;
		int i = MathHelper.floor_double(d1 / 2048.0D);
		int j = MathHelper.floor_double(d2 / 2048.0D);
		d1 = d1 - (double) (i * 2048);
		d2 = d2 - (double) (j * 2048);
		mc.renderEngine.bindTexture(locationCloudsPng);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		final Color color = getCloudColor(world, partialTicks);
		float red = color.red;
		float green = color.green;
		float blue = color.blue;

		if (pass != 2) {
			float f7 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float f8 = (red * 30.0F + green * 70.0F) / 100.0F;
			float f9 = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = f7;
			green = f8;
			blue = f9;
		}

		float f26 = red * 0.9F;
		float f27 = green * 0.9F;
		float f28 = blue * 0.9F;
		float f10 = red * 0.7F;
		float f11 = green * 0.7F;
		float f12 = blue * 0.7F;
		float f13 = red * 0.8F;
		float f14 = green * 0.8F;
		float f15 = blue * 0.8F;
		float f17 = (float) MathHelper.floor_double(d1) * 0.00390625F;
		float f18 = (float) MathHelper.floor_double(d2) * 0.00390625F;
		float f19 = (float) (d1 - (double) MathHelper.floor_double(d1));
		float f20 = (float) (d2 - (double) MathHelper.floor_double(d2));
		GlStateManager.scale(12.0F, 1.0F, 12.0F);

		for (int i1 = 0; i1 < 2; ++i1) {
			if (i1 == 0) {
				GlStateManager.colorMask(false, false, false, false);
			} else {
				switch (pass) {
				case 0:
					GlStateManager.colorMask(false, true, true, true);
					break;
				case 1:
					GlStateManager.colorMask(true, false, false, true);
					break;
				case 2:
					GlStateManager.colorMask(true, true, true, true);
				}
			}

			for (int j1 = -3; j1 <= 4; ++j1) {
				for (int k1 = -3; k1 <= 4; ++k1) {
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
					float f22 = (float) (j1 * 8);
					float f23 = (float) (k1 * 8);
					float f24 = f22 - f19;
					float f25 = f23 - f20;

					if (f3 > -5.0F) {
						worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + 8.0F))
								.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
										(double) ((f23 + 8.0F) * 0.00390625F + f18))
								.color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F), (double) (f25 + 8.0F))
								.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
										(double) ((f23 + 8.0F) * 0.00390625F + f18))
								.color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F), (double) (f25 + 0.0F))
								.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
										(double) ((f23 + 0.0F) * 0.00390625F + f18))
								.color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
						worldrenderer.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F), (double) (f25 + 0.0F))
								.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
										(double) ((f23 + 0.0F) * 0.00390625F + f18))
								.color(f10, f11, f12, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
					}

					if (f3 <= 5.0F) {
						worldrenderer
								.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 8.0F))
								.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
										(double) ((f23 + 8.0F) * 0.00390625F + f18))
								.color(red, green, blue, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						worldrenderer
								.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 8.0F))
								.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
										(double) ((f23 + 8.0F) * 0.00390625F + f18))
								.color(red, green, blue, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						worldrenderer
								.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 0.0F))
								.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
										(double) ((f23 + 0.0F) * 0.00390625F + f18))
								.color(red, green, blue, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
						worldrenderer
								.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F - 9.765625E-4F), (double) (f25 + 0.0F))
								.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
										(double) ((f23 + 0.0F) * 0.00390625F + f18))
								.color(red, green, blue, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
					}

					if (j1 > -1) {
						for (int l1 = 0; l1 < 8; ++l1) {
							worldrenderer
									.pos((double) (f24 + (float) l1 + 0.0F), (double) (f3 + 0.0F),
											(double) (f25 + 8.0F))
									.tex((double) ((f22 + (float) l1 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 8.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + (float) l1 + 0.0F), (double) (f3 + 4.0F),
											(double) (f25 + 8.0F))
									.tex((double) ((f22 + (float) l1 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 8.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + (float) l1 + 0.0F), (double) (f3 + 4.0F),
											(double) (f25 + 0.0F))
									.tex((double) ((f22 + (float) l1 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 0.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + (float) l1 + 0.0F), (double) (f3 + 0.0F),
											(double) (f25 + 0.0F))
									.tex((double) ((f22 + (float) l1 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 0.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (j1 <= 1) {
						for (int i2 = 0; i2 < 8; ++i2) {
							worldrenderer
									.pos((double) (f24 + (float) i2 + 1.0F - 9.765625E-4F), (double) (f3 + 0.0F),
											(double) (f25 + 8.0F))
									.tex((double) ((f22 + (float) i2 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 8.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + (float) i2 + 1.0F - 9.765625E-4F), (double) (f3 + 4.0F),
											(double) (f25 + 8.0F))
									.tex((double) ((f22 + (float) i2 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 8.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + (float) i2 + 1.0F - 9.765625E-4F), (double) (f3 + 4.0F),
											(double) (f25 + 0.0F))
									.tex((double) ((f22 + (float) i2 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 0.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + (float) i2 + 1.0F - 9.765625E-4F), (double) (f3 + 0.0F),
											(double) (f25 + 0.0F))
									.tex((double) ((f22 + (float) i2 + 0.5F) * 0.00390625F + f17),
											(double) ((f23 + 0.0F) * 0.00390625F + f18))
									.color(f26, f27, f28, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
						}
					}

					if (k1 > -1) {
						for (int j2 = 0; j2 < 8; ++j2) {
							worldrenderer
									.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F),
											(double) (f25 + (float) j2 + 0.0F))
									.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) j2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F),
											(double) (f25 + (float) j2 + 0.0F))
									.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) j2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F),
											(double) (f25 + (float) j2 + 0.0F))
									.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) j2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F),
											(double) (f25 + (float) j2 + 0.0F))
									.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) j2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
						}
					}

					if (k1 <= 1) {
						for (int k2 = 0; k2 < 8; ++k2) {
							worldrenderer
									.pos((double) (f24 + 0.0F), (double) (f3 + 4.0F),
											(double) (f25 + (float) k2 + 1.0F - 9.765625E-4F))
									.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) k2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + 8.0F), (double) (f3 + 4.0F),
											(double) (f25 + (float) k2 + 1.0F - 9.765625E-4F))
									.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) k2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + 8.0F), (double) (f3 + 0.0F),
											(double) (f25 + (float) k2 + 1.0F - 9.765625E-4F))
									.tex((double) ((f22 + 8.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) k2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
							worldrenderer
									.pos((double) (f24 + 0.0F), (double) (f3 + 0.0F),
											(double) (f25 + (float) k2 + 1.0F - 9.765625E-4F))
									.tex((double) ((f22 + 0.0F) * 0.00390625F + f17),
											(double) ((f23 + (float) k2 + 0.5F) * 0.00390625F + f18))
									.color(f13, f14, f15, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
						}
					}

					tessellator.draw();
				}
			}
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
	}

}
