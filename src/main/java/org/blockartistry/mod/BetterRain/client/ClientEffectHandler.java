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

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.Set;

import org.blockartistry.mod.BetterRain.ModLog;
import org.blockartistry.mod.BetterRain.ModOptions;
import org.blockartistry.mod.BetterRain.data.AuroraData;
import org.blockartistry.mod.BetterRain.data.EffectType;
import org.blockartistry.mod.BetterRain.util.PlayerUtils;
import org.blockartistry.mod.BetterRain.util.WorldUtils;
import org.blockartistry.mod.BetterRain.util.XorShiftRandom;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
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
public final class ClientEffectHandler {

	private static final XorShiftRandom random = XorShiftRandom.shared;

	private static final int PARTICLE_SETTING_ALL = 0;
	@SuppressWarnings("unused")
	private static final int PARTICLE_SETTING_DECREASED = 1;
	private static final int PARTICLE_SETTING_MINIMAL = 2;

	private static final float MIN_STRENGTH_FOR_PARTICLE_SPAWN = 0.1F;
	private static final int RANGE_FACTOR = 10;

	private static final boolean ALWAYS_OVERRIDE_SOUND = ModOptions.getAlwaysOverrideSound();

	// Aurora information
	private static final boolean AURORA_ENABLE = ModOptions.getAuroraEnable();
	private static int auroraDimension = 0;
	private static final Set<AuroraData> auroras = new HashSet<AuroraData>();
	public static Aurora currentAurora;

	public static void addAurora(final AuroraData data) {
		if (!AURORA_ENABLE)
			return;

		if (auroraDimension != data.dimensionId || PlayerUtils.getClientPlayerDimension() != data.dimensionId) {
			auroras.clear();
			currentAurora = null;
			auroraDimension = data.dimensionId;
		}
		auroras.add(data);
	}

	private ClientEffectHandler() {
	}

	public static void initialize() {
		final ClientEffectHandler handler = new ClientEffectHandler();
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

	private Aurora getClosestAurora(final TickEvent.ClientTickEvent event) {
		if(auroraDimension != PlayerUtils.getClientPlayerDimension()) { 
			auroras.clear();
			currentAurora = null;
		}
		
		if (auroras.size() == 0) {
			currentAurora = null;
			return null;
		}

		final EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		final int playerX = (int) player.posX;
		final int playerZ = (int) player.posZ;
		boolean started = false;
		int distanceSq = 0;
		AuroraData ad = null;
		for (final AuroraData data : auroras) {
			final int deltaX = data.posX - playerX;
			final int deltaZ = data.posZ - playerZ;
			final int d = deltaX * deltaX + deltaZ * deltaZ;
			if (!started || distanceSq > d) {
				started = true;
				distanceSq = d;
				ad = data;
			}
		}

		if (ad == null) {
			currentAurora = null;
		} else if (currentAurora == null || (currentAurora.posX != ad.posX && currentAurora.posZ != ad.posZ)) {
			ModLog.info("New aurora: " + ad.toString());
			currentAurora = new Aurora(ad);
		}

		return currentAurora;
	}

	private static boolean auroraTimeToDie(final long time) {
		return time >= 22220L && time < 23500L; 
	}
	
	protected void processAurora(final TickEvent.ClientTickEvent event) {
		final World world = FMLClientHandler.instance().getClient().theWorld;
		if (world != null && auroras.size() > 0) {
			final long time = WorldUtils.getWorldTime(world);
			if (WorldUtils.isDaytime(time)) {
				auroras.clear();
				currentAurora = null;
			} else {
				final Aurora aurora = getClosestAurora(event);
				aurora.update();
				if (aurora.isAlive() && auroraTimeToDie(time)) {
					ModLog.info("Aurora fade...");
					aurora.die();
				}
			}
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
		if (event.phase == Phase.END) {
			if (AURORA_ENABLE)
				processAurora(event);
			return;
		}

		// For some reason this ticks when the client loads but
		// without a world loaded. Also, if the loaded world
		// is not a surface world return.
		final World world = Minecraft.getMinecraft().theWorld;
		if (world == null)
			return;

		// Set the textures for the currentAurora intensity. This
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
			final BiomeGenBase biome = world.getBiomeGenForCoords(locX, locZ);

			// If a lightening bolt can't spawn at the location
			// it is not eligible for what we need.
			if (!EffectType.hasPrecipitation(biome))
				continue;

			// If it is freezing it is not what we need.
			final int locY = world.getPrecipitationHeight(locX, locZ);
			if (biome.getFloatTemperature(locX, locY, locZ) < 0.15F)
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
