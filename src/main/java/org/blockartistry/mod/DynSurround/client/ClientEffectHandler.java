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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ClientEffectHandler {

	private static final boolean ALWAYS_OVERRIDE_SOUND = ModOptions.getAlwaysOverrideSound();

	private static final List<IClientEffectHandler> effectHandlers = new ArrayList<IClientEffectHandler>();

	public static void register(final IClientEffectHandler handler) {
		effectHandlers.add(handler);
		if (handler.hasEvents()) {
			MinecraftForge.EVENT_BUS.register(handler);
		}
	}

	private ClientEffectHandler() {
	}

	public static void initialize() {
		final ClientEffectHandler handler = new ClientEffectHandler();
		MinecraftForge.EVENT_BUS.register(handler);

		if(ModOptions.getEnableDebugLogging())
			register(new DiagnosticHandler());

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
	public void soundEvent(final PlaySoundEvent event) {
		if ((ALWAYS_OVERRIDE_SOUND || !StormProperties.doVanilla()) && replaceRainSound(event.name)) {
			final ISound sound = event.sound;
			event.result = new PositionedSoundRecord(StormProperties.getCurrentStormSound(),
					StormProperties.getCurrentVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(),
					sound.getZPosF());
		}
	}
	
	private static int tickCount = 0;
	public static int getTickCount() {
		return tickCount;
	}
	
	private static List<EntityDropParticleFX> drops = new ArrayList<EntityDropParticleFX>();

	@SubscribeEvent
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
			if(!Minecraft.getMinecraft().isGamePaused())
				tickCount++;
			drops.clear();
			final EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
			for (final IClientEffectHandler handler : effectHandlers)
				handler.process(world, player);
		} else if (event.phase == Phase.END) {
			final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
			for (final EntityDropParticleFX drop : drops) {
				if (drop.isEntityAlive()) {
					final int x = MathHelper.floor_double(drop.posX);
					final int y = MathHelper.floor_double(drop.posY + 0.3D);
					final int z = MathHelper.floor_double(drop.posZ);
					pos.set(x, y, z);
					Block block = world.getBlockState(pos).getBlock();
					if (block != Blocks.air && !block.isLeaves(world, pos)) {
						// Find out where it is going to hit
						BlockPos soundPos = pos.down();
						while((block = world.getBlockState(soundPos).getBlock()) == Blocks.air)
							soundPos = soundPos.down();
						
						if(block.getMaterial().isSolid()) {
							final int distance = y - soundPos.getY();
							PlayerSoundEffectHandler.playSoundAt(soundPos, BiomeRegistry.WATER_DRIP, 40 + distance * 2);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onWorldLoad(final WorldEvent.Load e) {
		if (!e.world.isRemote)
			return;

		// Tickle the Dimension Registry so it has the
		// latest info.
		DimensionRegistry.loading(e.world);

		// Shim the provider so we can tap into the
		// sky and cloud stuff.
		if (ModOptions.getEnableFancyCloudHandling()) {
//			if(e.world.provider.isSurfaceWorld() && e.world.provider.getCloudRenderer() == null)
//				e.world.provider.setCloudRenderer(new CloudRenderer());
			e.world.provider = new WorldProviderShim(e.world, e.world.provider);
		}
	}
}
