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

package org.blockartistry.mod.DynSurround.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.blockartistry.mod.DynSurround.ModLog;
import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.ClientEffectHandler;
import org.blockartistry.mod.DynSurround.client.footsteps.game.user.GenerateBlockReport;
import org.blockartistry.mod.DynSurround.client.hud.GuiHUDHandler;
import org.blockartistry.mod.DynSurround.data.BlockRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystemConfig;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {

	@Override
	public void preInit(final FMLPreInitializationEvent event) {
		super.preInit(event);

		ModLog.info("Sound channels: %d normal, %d streaming", ModOptions.getNumberNormalSoundChannels(),
				ModOptions.getNumberStreamingSoundChannels());
		SoundSystemConfig.setNumberNormalChannels(ModOptions.getNumberNormalSoundChannels());
		SoundSystemConfig.setNumberStreamingChannels(ModOptions.getNumberStreamingSoundChannels());
	}

	@Override
	public void init(final FMLInitializationEvent event) {
		super.init(event);
		BlockRegistry.initialize();
		ClientEffectHandler.initialize();
		GuiHUDHandler.initialize();
	}
	
	@Override
	public void postInit(final FMLPostInitializationEvent event) {
		super.postInit(event);

		if (ModOptions.getEnableDebugLogging()) {
			final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
			final List<String> sounds = new ArrayList<String>();
			for (final Object resource : handler.sndRegistry.getKeys())
				sounds.add(resource.toString());
			Collections.sort(sounds);

			ModLog.info("*** SOUND REGISTRY ***");
			for (final String sound : sounds)
				ModLog.info(sound);
			
			final GenerateBlockReport report = new GenerateBlockReport();
			for(final Entry<String, String> entry: report.getResults().getAllProperties().entrySet()) {
				ModLog.info("%s = %s", entry.getKey(), entry.getValue());
			}
		}
	}

}
