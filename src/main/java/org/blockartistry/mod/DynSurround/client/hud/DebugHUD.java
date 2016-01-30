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

package org.blockartistry.mod.DynSurround.client.hud;

import org.blockartistry.mod.DynSurround.client.hud.GuiHUDHandler.IGuiOverlay;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

@SideOnly(Side.CLIENT)
public class DebugHUD extends Gui implements IGuiOverlay {

	private static final float TRANSPARENCY = 1.0F;
	private static final int TEXT_COLOR = (int) (255 * TRANSPARENCY) << 24 | 0xFFFFFF;
	private static final float GUITOP = 200;
	private static final float GUILEFT = 2;

	public void doRender(RenderGameOverlayEvent event) {

		if (event.isCancelable() || event.type != ElementType.EXPERIENCE) {
			return;
		}

		final Minecraft mc = Minecraft.getMinecraft();
		final FontRenderer font = mc.fontRenderer;
		final EntityPlayer player = mc.thePlayer;

		GL11.glPushMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, TRANSPARENCY);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslatef(GUILEFT, GUITOP, 0.0F);
		String s = "DynSurround Biome: " + BiomeRegistry.resolveName(PlayerUtils.getPlayerBiome(player, false));
		font.drawStringWithShadow(s, 0, 0, TEXT_COLOR);
		GL11.glPopMatrix();
	}
}
