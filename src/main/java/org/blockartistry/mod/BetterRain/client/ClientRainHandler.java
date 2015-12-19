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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Random;

import org.blockartistry.mod.BetterRain.ModOptions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

@SideOnly(Side.CLIENT)
public class ClientRainHandler {

	private static final Random random = new Random();

	private static final int PARTICLE_SETTING_ALL = 0;
	@SuppressWarnings("unused")
	private static final int PARTICLE_SETTING_DECREASED = 1;
	private static final int PARTICLE_SETTING_MINIMAL = 2;

	private static final float MIN_STRENGTH_FOR_PARTICLE_SPAWN = 0.1F;
	private static final int RANGE_FACTOR = 10;

	private static final boolean ALWAYS_OVERRIDE_SOUND = ModOptions.getAlwaysOverrideSound();

	private ClientRainHandler() {
	}

	public static void initialize() {
		final ClientRainHandler handler = new ClientRainHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}

	private static boolean generateParticle() {
		return Minecraft.getMinecraft().gameSettings.particleSetting != PARTICLE_SETTING_MINIMAL
				&& RainIntensity.getStrength() >= MIN_STRENGTH_FOR_PARTICLE_SPAWN;
	}

	/*
	 * Cull out some of the rain visuals as they get constructed. This gives
	 * some "density" to drops hitting the ground. It is done this way because
	 * vanilla spawns some of these particles, too.
	 * 
	 * Lava is a little tricky since smoke can generate naturally. We only cull
	 * smoke particle effects if it is raining.
	 */
	@SubscribeEvent
	public void entityEvent(final EntityEvent.EntityConstructing event) {
		if (RainIntensity.doVanillaRain())
			return;
		final float strength = RainIntensity.getStrength();
		if (event.entity instanceof EntityRainFX && (!generateParticle() || strength < random.nextFloat()))
			event.entity.setDead();
		else if (event.entity instanceof EntitySmokeFX && strength > 0.0F && strength < random.nextFloat())
			event.entity.setDead();
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
		if ((ALWAYS_OVERRIDE_SOUND || !RainIntensity.doVanillaRain()) && replaceRainSound(event.name)) {
			final ISound sound = event.sound;
			event.result = new PositionedSoundRecord(RainIntensity.getCurrentRainSound(),
					RainIntensity.getCurrentRainVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(),
					sound.getZPosF());
		}
	}

	/*
	 * Lowest priority. Another mod may have done something with the textures so
	 * we want to override to do what we want.
	 * 
	 * NOTE: This comes from EntityRenderer.addRainParticles(), though it has
	 * been modified to fit the mods needs.
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void tickEvent(final TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START)
			return;

		// For some reason this ticks when the client loads but
		// without a world loaded. Also, if the loaded world
		// is not a surface world return.
		final World world = Minecraft.getMinecraft().theWorld;
		if (world == null)
			return;

		// Set the textures for the current intensity. This
		// occurs now because rendering is about to take place.
		// Also, it has to occur repeatedly because another
		// mod may have done something with the textures and
		// they need to be put back. :)
		RainIntensity.setTextures();

		// If we want to let Vanilla handle, or if conditions don't
		// require particle generation then return.
		if (RainIntensity.doVanillaRain() || !generateParticle())
			return;

		// If for some reason the focus of the client UI
		// is not in game don't do the textures.
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.isSingleplayer() && mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()
				&& !mc.getIntegratedServer().getPublic())
			return;

		// Get the world rain strength. If it is 0 there is
		// nothing to do.
		float worldRainStrengthFactor = world.getRainStrength(1.0F);
		if (worldRainStrengthFactor <= 0.0F)
			return;

		// Cut back on the strength factor if the client
		// is not running with fancy graphics.
		if (!mc.gameSettings.fancyGraphics)
			worldRainStrengthFactor /= 2.0F;

		// Determine the number of particle effects
		// that need to be spawned.
		final int particleCount = (int) (((mc.gameSettings.particleSetting == PARTICLE_SETTING_ALL) ? 100.0F : 50.0F)
				* worldRainStrengthFactor * worldRainStrengthFactor);

		// Bail if no particles are to generate
		if (particleCount == 0)
			return;

		final EntityLivingBase entitylivingbase = mc.renderViewEntity;
		final int playerX = MathHelper.floor_double(entitylivingbase.posX);
		final int playerY = MathHelper.floor_double(entitylivingbase.posY);
		final int playerZ = MathHelper.floor_double(entitylivingbase.posZ);

		for (int i = 0; i < particleCount; i++) {
			final int locX = playerX + random.nextInt(RANGE_FACTOR) - random.nextInt(RANGE_FACTOR);
			final int locZ = playerZ + random.nextInt(RANGE_FACTOR) - random.nextInt(RANGE_FACTOR);
			final BiomeGenBase biomegenbase = world.getBiomeGenForCoords(locX, locZ);

			// If a lightening bolt can't spawn at the location
			// it is not eligible for what we need.
			if (!biomegenbase.canSpawnLightningBolt())
				continue;

			// If it is freezing it is not what we need.
			final int locY = world.getPrecipitationHeight(locX, locZ);
			if (biomegenbase.getFloatTemperature(locX, locY, locZ) < 0.15F)
				continue;

			// If the Y location happens to be outside the range we
			// ignore.
			if ((locY > playerY + RANGE_FACTOR) || (locY < playerY - RANGE_FACTOR))
				continue;

			final Block block = world.getBlock(locX, locY - 1, locZ);

			// If landing block for the particle happens to be air
			// skip. Doesn't happen that often, but could given the
			// contract of getBlock().
			if (block == Blocks.air)
				continue;

			final double spawnX = locX + random.nextFloat();
			final double spawnY = locY + 0.1F - block.getBlockBoundsMinY();
			final double spawnZ = locZ + random.nextFloat();

			EntityFX particle = null;
			if (block.getMaterial() == Material.lava) {
				// Rain hitting lava gives off smoke particle effects.
				particle = new EntitySmokeFX(world, spawnX, spawnY, spawnZ, 0.0D, 0.0D, 0.0D);
			} else {
				// Spawn extra splash particles.
				particle = new EntityRainFX(world, spawnX, spawnY, spawnZ);
			}

			mc.effectRenderer.addEffect(particle);
		}
	}
}
