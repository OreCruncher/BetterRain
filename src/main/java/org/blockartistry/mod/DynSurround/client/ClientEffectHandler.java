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

package org.blockartistry.mod.DynSurround.client;

import java.util.ArrayList;
import java.util.List;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.fx.BlockEffectHandler;
import org.blockartistry.mod.DynSurround.client.storm.StormProperties;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.world.WorldEvent;

@SideOnly(Side.CLIENT)
public class ClientEffectHandler {

	private static final boolean ALWAYS_OVERRIDE_SOUND = ModOptions.getAlwaysOverrideSound();

	private static final List<IClientEffectHandler> effectHandlers = new ArrayList<IClientEffectHandler>();

	public static void register(final IClientEffectHandler handler) {
		effectHandlers.add(handler);
		if (handler.hasEvents()) {
			MinecraftForge.EVENT_BUS.register(handler);
			FMLCommonHandler.instance().bus().register(handler);
		}
	}

	private ClientEffectHandler() {
	}

	public static void initialize() {
		final ClientEffectHandler handler = new ClientEffectHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);

		register(new FogEffectHandler());
		register(new BlockEffectHandler());

		if (ModOptions.getAuroraEnable())
			register(new AuroraEffectHandler());

		if (ModOptions.getEnableBiomeSounds())
			register(new PlayerSoundEffectHandler());

		if (ModOptions.getSuppressPotionParticleEffect())
			register(new PotionParticleScrubHandler());
	}

	/*
	 * Determines if the sound needs to be replaced by the event handler.
	 */
	private static boolean replaceRainSound(final String name) {
		return "ambient.weather.rain".equals(name);
	}

	/*
	 * Intercept the sound events and patch up the rain sound. If the rain
	 * experience is to be Vanilla let it just roll on through.
	 */
	@SubscribeEvent
	public void soundEvent(final PlaySoundEvent17 event) {
		if ((ALWAYS_OVERRIDE_SOUND || !StormProperties.doVanilla()) && replaceRainSound(event.name)) {
			final ISound sound = event.sound;
			event.result = new PositionedSoundRecord(StormProperties.getCurrentStormSound(),
					StormProperties.getCurrentVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(),
					sound.getZPosF());
		}
	}

	private static List<EntityDropParticleFX> drops = new ArrayList<EntityDropParticleFX>();

	// Don't do it for now - water dripping into other liquid blocks also make a sound :\
	//@SubscribeEvent
	public void entityCreateEvent(final EntityConstructing event) {
		if (event.entity instanceof EntityDropParticleFX) {
			drops.add((EntityDropParticleFX) event.entity);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void clientTick(final TickEvent.ClientTickEvent event) {
		final World world = FMLClientHandler.instance().getClient().theWorld;
		if (world == null)
			return;

		if (event.phase == Phase.START) {
			drops.clear();
			final EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
			for (final IClientEffectHandler handler : effectHandlers)
				handler.process(world, player);
		} else if (event.phase == Phase.END) {
			for (final EntityDropParticleFX drop : drops) {
				if (drop.isEntityAlive()) {
					final int x = MathHelper.floor_double(drop.posX);
					final int y = MathHelper.floor_double(drop.posY + 0.3D);
					final int z = MathHelper.floor_double(drop.posZ);
					final Block source = world.getBlock(x, y, z);
					if (source != Blocks.air && !source.isLeaves(world, x, y, z)) {
						PlayerSoundEffectHandler.playSoundAtPlayer(null, BiomeRegistry.WATER_DRIP, 50);
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldLoad(final WorldEvent.Load e) {
		if (!e.world.isRemote)
			return;

		// Tickle the Dimension Registry so it has the
		// latest info.
		DimensionRegistry.loading(e.world);

		// Shim the provider so we can tap into the
		// sky and cloud stuff.
		if (ModOptions.getEnableFancyCloudHandling())
			e.world.provider = new WorldProviderShim(e.world, e.world.provider);
	}

}
