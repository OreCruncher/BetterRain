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

package org.blockartistry.mod.DynSurround.client.gui;

import java.util.ArrayList;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.Module;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class DynSurroundConfigGui extends GuiConfig {
	
	private final Configuration config = Module.config();
	
	public DynSurroundConfigGui(final GuiScreen parentScreen) {
		super(parentScreen, new ArrayList<IConfigElement>(), Module.MOD_ID, true, true, Module.MOD_NAME);
		this.titleLine2 = this.config.getConfigFile().getAbsolutePath();

		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_GENERAL)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_RAIN)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_FOG)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_AURORA)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_BIOMES)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_DIMENSIONS)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_SOUND)));
		this.configElements.add(new ConfigElement(config.getCategory(ModOptions.CATEGORY_PLAYER)));
	}

}
