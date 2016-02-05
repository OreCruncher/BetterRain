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
import java.util.UUID;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;
import org.blockartistry.mod.DynSurround.data.DimensionRegistry;
import org.blockartistry.mod.DynSurround.event.DiagnosticEvent;
import org.blockartistry.mod.DynSurround.util.PlayerUtils;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class EnvironStateHandler implements IClientEffectHandler {

	// Diagnostic strings to display in the debug HUD
	private static List<String> diagnostics = new ArrayList<String>();

	public static List<String> getDiagnostics() {
		return diagnostics;
	}

	public static class EnvironState {
		// State that is gathered from the various sources
		// to avoid requery. Used during the tick.
		private static String conditions = "";
		private static String biomeName = "";
		private static BiomeGenBase playerBiome = null;
		private static int dimensionId;
		private static String dimensionName;
		private static EntityPlayer player;

		private static int tickCounter;

		public static String getConditions() {
			return conditions;
		}

		public static BiomeGenBase getPlayerBiome() {
			return playerBiome;
		}

		public static String getBiomeName() {
			return biomeName;
		}

		public static int getDimensionId() {
			return dimensionId;
		}

		public static String getDimensionName() {
			return dimensionName;
		}

		public static EntityPlayer getPlayer() {
			if(player == null)
				player = Minecraft.getMinecraft().thePlayer;
			return player;
		}

		public static boolean isPlayer(final Entity entity) {
			if (entity instanceof EntityPlayer) {
				final EntityPlayer ep = (EntityPlayer) entity;
				return ep.getUniqueID().equals(getPlayer().getUniqueID());
			}
			return false;
		}

		public static boolean isPlayer(final UUID id) {
			return getPlayer().getUniqueID().equals(id);
		}

		public static boolean isPlayerHurt() {
			return (getPlayer().getHealth() / getPlayer().getMaxHealth()) < 0.40F;
		}

		public static boolean isPlayerHungry() {
			return (getPlayer().getFoodStats().getFoodLevel() / 20.0F) < 0.40F;
		}

		public static boolean isPlayerBurning() {
			return getPlayer().isBurning();
		}

		public static boolean isPlayerSuffocating() {
			return getPlayer().getAir() <= 0;
		}

		public static boolean isPlayerFlying() {
			return getPlayer().capabilities.isFlying;
		}

		public static boolean isPlayerSprinting() {
			return getPlayer().isSprinting();
		}

		public static boolean isPlayerInLava() {
			return getPlayer().worldObj.isMaterialInBB(
					getPlayer().boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D),
					Material.lava);
		}

		public static boolean isPlayerInvisible() {
			return getPlayer().isInvisible();
		}

		public static boolean isPlayerBlind() {
			return getPlayer().isPotionActive(Potion.blindness);
		}

		public static boolean isPlayerInWater() {
			return getPlayer().isInWater();
		}

		public static boolean isPlayerRiding() {
			return getPlayer().isRiding();
		}

		public static boolean isPlayerOnGround() {
			return getPlayer().onGround;
		}

		public static boolean isPlayerMoving() {
			return getPlayer().distanceWalkedModified != player.prevDistanceWalkedModified;
		}

		public static World getWorld() {
			return getPlayer().worldObj;
		}

		public static int getTickCounter() {
			return tickCounter;
		}

		public static double distanceToPlayer(final double x, final double y, final double z) {
			if (player == null)
				return Double.MAX_VALUE;
			return player.getDistanceSq(x, y, z);
		}
	}

	private static final String CONDITION_TOKEN_HURT = "hurt";
	private static final String CONDITION_TOKEN_HUNGRY = "hungry";
	private static final String CONDITION_TOKEN_BURNING = "burning";
	private static final String CONDITION_TOKEN_NOAIR = "noair";
	private static final String CONDITION_TOKEN_FLYING = "flying";
	private static final String CONDITION_TOKEN_SPRINTING = "sprinting";
	private static final String CONDITION_TOKEN_INLAVA = "inlava";
	private static final String CONDITION_TOKEN_INWATER = "inwater";
	private static final String CONDITION_TOKEN_INVISIBLE = "invisible";
	private static final String CONDITION_TOKEN_BLIND = "blind";
	private static final String CONDITION_TOKEN_MINECART = "ridingminecart";
	private static final String CONDITION_TOKEN_HORSE = "ridinghorse";
	private static final String CONDITION_TOKEN_BOAT = "ridingboat";
	private static final String CONDITION_TOKEN_PIG = "ridingpig";
	private static final String CONDITION_TOKEN_RIDING = "riding";
	private static final char CONDITION_SEPARATOR = '#';

	private static String getPlayerConditions(final EntityPlayer player) {
		final StringBuilder builder = new StringBuilder();
		if (EnvironState.isPlayerHurt())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_HURT);
		if (EnvironState.isPlayerHungry())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_HUNGRY);
		if (EnvironState.isPlayerBurning())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_BURNING);
		if (EnvironState.isPlayerSuffocating())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_NOAIR);
		if (EnvironState.isPlayerFlying())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_FLYING);
		if (EnvironState.isPlayerSprinting())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_SPRINTING);
		if (EnvironState.isPlayerInLava())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_INLAVA);
		if (EnvironState.isPlayerInvisible())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_INVISIBLE);
		if (EnvironState.isPlayerBlind())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_BLIND);
		if (EnvironState.isPlayerInWater())
			builder.append(CONDITION_SEPARATOR).append(CONDITION_TOKEN_INWATER);
		if (EnvironState.isPlayerRiding()) {
			builder.append(CONDITION_SEPARATOR);
			if (player.ridingEntity instanceof EntityMinecart)
				builder.append(CONDITION_TOKEN_MINECART);
			else if (player.ridingEntity instanceof EntityHorse)
				builder.append(CONDITION_TOKEN_HORSE);
			else if (player.ridingEntity instanceof EntityBoat)
				builder.append(CONDITION_TOKEN_BOAT);
			else if (player.ridingEntity instanceof EntityPig)
				builder.append(CONDITION_TOKEN_PIG);
			else
				builder.append(CONDITION_TOKEN_RIDING);
		}
		builder.append(CONDITION_SEPARATOR);
		return builder.toString();
	}

	@Override
	public void process(final World world, final EntityPlayer player) {
		EnvironState.player = player;
		EnvironState.conditions = DimensionRegistry.getConditions(world) + getPlayerConditions(player);
		EnvironState.playerBiome = PlayerUtils.getPlayerBiome(player, false);
		EnvironState.biomeName = BiomeRegistry.resolveName(EnvironState.playerBiome);
		EnvironState.dimensionId = world.provider.dimensionId;
		EnvironState.dimensionName = world.provider.getDimensionName();

		if (!Minecraft.getMinecraft().isGamePaused())
			EnvironState.tickCounter++;

		// Gather diagnostics if needed
		if (ModOptions.getEnableDebugLogging()) {
			final DiagnosticEvent.Gather gather = new DiagnosticEvent.Gather(world, player);
			MinecraftForge.EVENT_BUS.post(gather);
			diagnostics = gather.output;
		}
	}

	@Override
	public boolean hasEvents() {
		return true;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void diagnostics(final DiagnosticEvent.Gather event) {
		event.output.add("Dim: " + EnvironState.getDimensionId() + "/" + EnvironState.getDimensionName());
		event.output.add("Biome: " + EnvironState.getBiomeName());
		event.output.add("Conditions: " + EnvironState.getConditions());
	}

}
