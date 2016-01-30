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

import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.util.Color;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

/*
 * The shim is used to hook cloud height and color
 * information for cloud rendering.  A lot of work
 * just to tap into Minecraft routines without ASM.
 */
@SideOnly(Side.CLIENT)
public class WorldProviderShim extends WorldProvider {

	protected final WorldProvider provider;

	public WorldProviderShim(final World world, final WorldProvider provider) {
		this.provider = provider;
		this.worldObj = world;
	}

	public IChunkProvider createChunkGenerator() {
		return this.provider.createChunkGenerator();
	}

	public boolean canCoordinateBeSpawn(int x, int z) {
		return this.provider.canCoordinateBeSpawn(x, z);
	}

	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_) {
		return this.provider.calculateCelestialAngle(p_76563_1_, p_76563_3_);
	}

	public int getMoonPhase(long p_76559_1_) {
		return this.provider.getMoonPhase(p_76559_1_);
	}

	public boolean isSurfaceWorld() {
		return this.provider.isSurfaceWorld();
	}

	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
		return this.provider.calcSunriseSunsetColors(celestialAngle, partialTicks);
	}

	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float p_76562_1_, float p_76562_2_) {
		return this.provider.getFogColor(p_76562_1_, p_76562_2_);
	}

	public boolean canRespawnHere() {
		return this.provider.canRespawnHere();
	}

	@SideOnly(Side.CLIENT)
	public float getCloudHeight() {
		return DimensionRegistry.getCloudHeight(this.worldObj);
	}

	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return this.provider.isSkyColored();
	}

	public BlockPos getSpawnCoordinate() {
		return this.provider.getSpawnCoordinate();
	}

	public int getAverageGroundLevel() {
		return this.provider.getAverageGroundLevel();
	}

	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor() {
		return this.provider.getVoidFogYFactor();
	}

	/**
	 * Returns true if the given X,Z coordinate should show environmental fog.
	 */
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		return this.provider.doesXZShowFog(x, z);
	}

	/**
	 * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
	 */
	public String getDimensionName() {
		return this.provider.getDimensionName();
	}

	public String getInternalNameSuffix() {
		return this.provider.getInternalNameSuffix();
	}

	public WorldChunkManager getWorldChunkManager() {
		return this.provider.getWorldChunkManager();
	}

	public boolean doesWaterVaporize() {
		return this.provider.doesWaterVaporize();
	}

	public boolean getHasNoSky() {
		return this.provider.getHasNoSky();
	}

	public float[] getLightBrightnessTable() {
		return this.provider.getLightBrightnessTable();
	}

	public int getDimensionId() {
		return this.provider.getDimensionId();
	}

	public WorldBorder getWorldBorder() {
		return this.provider.getWorldBorder();
	}

	public void setDimension(int dim) {
		this.provider.setDimension(dim);
	}

	public String getSaveFolder() {
		return this.provider.getSaveFolder();
	}

	public String getWelcomeMessage() {
		return this.provider.getWelcomeMessage();
	}

	public String getDepartMessage() {
		return this.provider.getDepartMessage();
	}

	public double getMovementFactor() {
		return this.provider.getMovementFactor();
	}

	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
		return this.provider.getSkyRenderer();
	}

	@SideOnly(Side.CLIENT)
	public void setSkyRenderer(net.minecraftforge.client.IRenderHandler skyRenderer) {
		this.provider.setSkyRenderer(skyRenderer);
	}

	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getCloudRenderer() {
		return this.provider.getCloudRenderer();
	}

	@SideOnly(Side.CLIENT)
	public void setCloudRenderer(net.minecraftforge.client.IRenderHandler renderer) {
		this.provider.setCloudRenderer(renderer);
	}

	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getWeatherRenderer() {
		return this.provider.getWeatherRenderer();
	}

	@SideOnly(Side.CLIENT)
	public void setWeatherRenderer(net.minecraftforge.client.IRenderHandler renderer) {
		this.provider.setWeatherRenderer(renderer);
	}

	public BlockPos getRandomizedSpawnPoint() {
		return this.provider.getRandomizedSpawnPoint();
	}

	public boolean shouldMapSpin(String entity, double x, double y, double z) {
		return this.provider.shouldMapSpin(entity, x, y, z);
	}

	public int getRespawnDimension(net.minecraft.entity.player.EntityPlayerMP player) {
		return this.provider.getRespawnDimension(player);
	}

	public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
		return this.provider.getBiomeGenForCoords(pos);
	}

	public boolean isDaytime() {
		return this.provider.isDaytime();
	}

	public float getSunBrightnessFactor(float par1) {
		return this.provider.getSunBrightnessFactor(par1);
	}

	public float getCurrentMoonPhaseFactor() {
		return this.provider.getCurrentMoonPhaseFactor();
	}

	@SideOnly(Side.CLIENT)
	public Vec3 getSkyColor(net.minecraft.entity.Entity cameraEntity, float partialTicks) {
		return this.provider.getSkyColor(cameraEntity, partialTicks);
	}

	@SideOnly(Side.CLIENT)
	public Vec3 drawClouds(float partialTicks) {
		final Color color = new Color(this.provider.drawClouds(partialTicks));
		final float stormIntensity = this.worldObj.getRainStrength(1.0F);
		if (stormIntensity > 0.0F) {
			// Need to darken the clouds based on intensity
			color.scale((1.0F - stormIntensity) * 0.5F + 0.5F);
		}
		return color.toVec3();
	}

	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float par1) {
		return this.provider.getSunBrightness(par1);
	}

	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1) {
		return this.provider.getStarBrightness(par1);
	}

	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful) {
		this.provider.setAllowedSpawnTypes(allowHostile, allowPeaceful);
	}

	public void calculateInitialWeather() {
		this.provider.calculateInitialWeather();
	}

	public void updateWeather() {
		this.provider.updateWeather();
	}

	public boolean canBlockFreeze(BlockPos pos, boolean byWater) {
		return this.provider.canBlockFreeze(pos, byWater);
	}

	public boolean canSnowAt(BlockPos pos, boolean checkLight) {
		return this.provider.canSnowAt(pos, checkLight);
	}

	public void setWorldTime(long time) {
		this.provider.setWorldTime(time);
	}

	public long getSeed() {
		return this.provider.getSeed();
	}

	public long getWorldTime() {
		return this.provider.getWorldTime();
	}

	public BlockPos getSpawnPoint() {
		return this.provider.getSpawnPoint();
	}

	public void setSpawnPoint(BlockPos pos) {
		this.provider.setSpawnPoint(pos);
	}

	public boolean canMineBlock(net.minecraft.entity.player.EntityPlayer player, BlockPos pos) {
		return this.provider.canMineBlock(player, pos);
	}

	public boolean isBlockHighHumidity(BlockPos pos) {
		return this.provider.isBlockHighHumidity(pos);
	}

	public int getHeight() {
		return this.provider.getHeight();
	}

	public int getActualHeight() {
		return this.provider.getActualHeight();
	}

	public double getHorizon() {
		return this.provider.getHorizon();
	}

	public void resetRainAndThunder() {
		this.provider.resetRainAndThunder();
	}

	public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk) {
		return this.provider.canDoLightning(chunk);
	}

	public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
		return this.provider.canDoRainSnowIce(chunk);
	}
}
