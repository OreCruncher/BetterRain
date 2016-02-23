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

import java.util.UUID;

import org.blockartistry.mod.DynSurround.ModOptions;
import org.blockartistry.mod.DynSurround.client.EnvironStateHandler.EnvironState;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityCriticalPopOffFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityDamagePopOffFX;
import org.blockartistry.mod.DynSurround.client.fx.particle.EntityHealPopOffFX;
import org.blockartistry.mod.DynSurround.network.Network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.Potion;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class DamageEffectHandler {

	private static final double DISTANCE_THRESHOLD_SQ = 32 * 32;

	public static class HealthData {
		public final UUID entityId;
		public final float posX;
		public final float posY;
		public final float posZ;
		public final boolean isCritical;
		public final int amount;

		public HealthData(final Entity entity, final boolean isCritical, final int amount) {
			this.entityId = entity.getUniqueID();
			this.posX = (float) entity.posX;
			this.posY = (float) entity.posY + entity.height;
			this.posZ = (float) entity.posZ;
			this.isCritical = isCritical;
			this.amount = amount;
		}

		public HealthData(final UUID id, final float x, final float y, final float z, final boolean isCritical,
				final int amount) {
			this.entityId = id;
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			this.isCritical = isCritical;
			this.amount = amount;
		}
	}

	private DamageEffectHandler() {
	}

	public static void initialize() {
		if (ModOptions.enableDamagePopoffs)
			MinecraftForge.EVENT_BUS.register(new DamageEffectHandler());
	}

	// From the Minecraft code for damage
	private static boolean isCritical(final EntityPlayer player, final Entity target) {
		return player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
				&& !player.isPotionActive(Potion.blindness) && player.ridingEntity == null
				&& target instanceof EntityLivingBase;
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onLivingHurt(final LivingHurtEvent event) {
		if (event == null || event.entity == null || event.entity.worldObj == null || event.entity.worldObj.isRemote)
			return;

		// Living heal should handle heals - I think..
		if (event.ammount <= 0 || event.entityLiving == null)
			return;

		// A bit hokey - may work.
		boolean isCrit = false;
		if (event.source instanceof EntityDamageSourceIndirect) {
			final EntityDamageSourceIndirect dmgSource = (EntityDamageSourceIndirect) event.source;
			if (dmgSource.getSourceOfDamage() instanceof EntityArrow) {
				final EntityArrow arrow = (EntityArrow) dmgSource.getSourceOfDamage();
				isCrit = arrow.getIsCritical();
			}
		} else if (event.source instanceof EntityDamageSource) {
			final EntityDamageSource dmgSource = (EntityDamageSource) event.source;
			if (dmgSource.getSourceOfDamage() instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) dmgSource.getSourceOfDamage();
				isCrit = isCritical(player, event.entityLiving);
			}
		}

		final HealthData data = new HealthData(event.entityLiving, isCrit, (int) event.ammount);
		Network.sendHealthUpdate(data, event.entity.worldObj.provider.getDimensionId());
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onLivingHeal(final LivingHealEvent event) {
		if (event == null || event.entity == null || event.entity.worldObj == null || event.entity.worldObj.isRemote)
			return;

		// Just in case
		if (event.amount <= 0 || event.entityLiving == null
				|| event.entityLiving.getHealth() == event.entityLiving.getMaxHealth())
			return;

		final HealthData data = new HealthData(event.entityLiving, false, -(int) event.amount);
		Network.sendHealthUpdate(data, event.entity.worldObj.provider.getDimensionId());
	}

	@SideOnly(Side.CLIENT)
	public static void handleEvent(final HealthData data) {
		if (!ModOptions.enableDamagePopoffs)
			return;

		// Don't show the players pop-offs
		if (EnvironState.isPlayer(data.entityId))
			return;

		// Don't want to display if too far away.
		final double distance = EnvironState.distanceToPlayer(data.posX, data.posY, data.posZ);
		if (distance >= DISTANCE_THRESHOLD_SQ)
			return;

		final World world = EnvironState.getWorld();
		final EffectRenderer renderer = Minecraft.getMinecraft().effectRenderer;
		EntityFX fx;

		if (data.isCritical) {
			fx = new EntityCriticalPopOffFX(world, data.posX, data.posY, data.posZ);
			renderer.addEffect(fx);
		}
		if (data.amount > 0) {
			fx = new EntityDamagePopOffFX(world, data.posX, data.posY, data.posZ, data.amount);
		} else {
			fx = new EntityHealPopOffFX(world, data.posX, data.posY, data.posZ, MathHelper.abs_int(data.amount));
		}
		renderer.addEffect(fx);
	}
}
