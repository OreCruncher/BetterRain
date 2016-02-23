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
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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

		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_AURORA, ModOptions.CONFIG_AURORA_ENABLED,
				"Aurora Feature"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_FOG, ModOptions.CONFIG_ALLOW_DESERT_FOG,
				"Desert Fog Feature"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_FOG,
				ModOptions.CONFIG_ENABLE_ELEVATION_HAZE, "Elevation Haze Feature"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_FOG, ModOptions.CONFIG_ENABLE_BIOME_FOG,
				"Biome Fog Feature"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_SOUND,
				ModOptions.CONFIG_ENABLE_BIOME_SOUNDS, "Biome Sound Feature"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_SOUND, ModOptions.CONFIG_ENABLE_JUMP_SOUND,
				"Player Jump Sound Effect"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_SOUND,
				ModOptions.CONFIG_ENABLE_SWING_SOUND, "Player Weapon Swing Sound Effect"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_SOUND,
				ModOptions.CONFIG_ENABLE_CRAFTING_SOUND, "Player Crafting Sound Effect"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_SOUND,
				ModOptions.CONFIG_ENABLE_BOW_PULL_SOUND, "Player Bow Pull Sound Effect"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_SOUND,
				ModOptions.CONFIG_ENABLE_FOOTSTEPS_SOUND, "Footstep Sound Effects"));
		this.configElements.add(getPropertyConfigElement(ModOptions.CATEGORY_POTION_HUD,
				ModOptions.CONFIG_POTION_HUD_ENABLE, "Potion HUD Overlay"));

		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_GENERAL, "General Settings"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_RAIN, "Rain Settings"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_FOG, "Fog Settings"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_AURORA, "Aurora Settings"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_BIOMES, "Biome Behaviors"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_DIMENSIONS, "Dimension Configuration"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_SOUND, "Sound Effects"));
		this.configElements.add(getCategoryConfigElement(ModOptions.CATEGORY_PLAYER, "Player Effects"));
	}

	private ConfigElement getCategoryConfigElement(final String category, final String label) {
		final ConfigCategory cat = config.getCategory(category);
		cat.setRequiresMcRestart(true);
		return new MyConfigElement(cat, label);
	}

	private ConfigElement getPropertyConfigElement(final String category, final String property, final String label) {
		final Property prop = config.getCategory(category).get(property);
		prop.setRequiresMcRestart(true);
		return new MyConfigElement(prop, label);
	}
}
