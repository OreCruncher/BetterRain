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

package org.blockartistry.mod.DynSurround.client.footsteps.game.system;

import org.blockartistry.mod.DynSurround.client.footsteps.engine.interfaces.EventType;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.implem.NormalVariator;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IGenerator;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IIsolator;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.ISolver;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IVariator;
import org.blockartistry.mod.DynSurround.client.footsteps.mcpackage.interfaces.IVariatorSettable;
import org.blockartistry.mod.DynSurround.util.MyUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class PFReaderH implements IGenerator, IVariatorSettable {
	// Construct
	final protected IIsolator mod;

	protected NormalVariator VAR;

	// Footsteps
	protected float dmwBase;
	protected float dwmYChange;
	protected double yPosition;

	// Airborne
	protected boolean isFlying;
	protected float fallDistance;

	protected float lastReference;
	protected boolean isImmobile;
	protected long timeImmobile;

	protected boolean isRightFoot;

	protected double xMovec;
	protected double zMovec;
	protected boolean scalStat;
	private boolean stepThisFrame;

	private boolean isMessyFoliage;
	private long brushesTime;

	public PFReaderH(final IIsolator isolator) {
		this.mod = isolator;
		this.VAR = new NormalVariator();
	}

	@Override
	public void setVariator(final IVariator variator) {
		if (variator instanceof NormalVariator) {
			this.VAR = (NormalVariator) variator;
		}
	}

	@Override
	public void generateFootsteps(final EntityPlayer ply) {
		simulateFootsteps(ply);
		simulateAirborne(ply);
		simulateBrushes(ply);
	}

	protected boolean stoppedImmobile(float reference) {
		final float diff = this.lastReference - reference;
		this.lastReference = reference;
		if (!this.isImmobile && diff == 0f) {
			this.timeImmobile = MyUtils.currentTimeMillis();
			this.isImmobile = true;
		} else if (this.isImmobile && diff != 0f) {
			this.isImmobile = false;
			return MyUtils.currentTimeMillis() - this.timeImmobile > this.VAR.IMMOBILE_DURATION;
		}

		return false;
	}

	protected void simulateFootsteps(final EntityPlayer ply) {
		final float distanceReference = ply.distanceWalkedOnStepModified;

		this.stepThisFrame = false;

		if (this.dmwBase > distanceReference) {
			this.dmwBase = 0;
			this.dwmYChange = 0;
		}

		final double movX = ply.motionX;
		final double movZ = ply.motionZ;

		final double scal = movX * this.xMovec + movZ * this.zMovec;
		if (this.scalStat != scal < 0.001f) {
			this.scalStat = !this.scalStat;

			if (this.scalStat && this.VAR.PLAY_WANDER && !this.mod.getSolver().hasSpecialStoppingConditions(ply)) {
				this.mod.getSolver().playAssociation(ply,
						this.mod.getSolver().findAssociationForPlayer(ply, 0d, this.isRightFoot), EventType.WANDER);
			}
		}
		this.xMovec = movX;
		this.zMovec = movZ;

		if (ply.onGround || ply.isInWater() || ply.isOnLadder()) {
			EventType event = null;

			float dwm = distanceReference - this.dmwBase;
			final boolean immobile = stoppedImmobile(distanceReference);
			if (immobile && !ply.isOnLadder()) {
				dwm = 0;
				this.dmwBase = distanceReference;
			}

			float distance = 0f;
			double verticalOffsetAsMinus = 0f;

			if (ply.isOnLadder() && !ply.onGround) {
				distance = this.VAR.DISTANCE_LADDER;
			} else if (!ply.isInWater() && Math.abs(this.yPosition - ply.posY) > 0.4d // &&
																						// Math.abs(this.yPosition
																						// -
																						// ply.posY)
																						// <
																						// 0.7d)
			) {
				// This ensures this does not get recorded as landing, but as a
				// step
				if (this.yPosition < ply.posY) { // Going upstairs
					distance = this.VAR.DISTANCE_STAIR;
					event = speedDisambiguator(ply, EventType.UP, EventType.UP_RUN);
				} else if (!ply.isSneaking()) { // Going downstairs
					distance = -1f;
					verticalOffsetAsMinus = 0f;
					event = speedDisambiguator(ply, EventType.DOWN, EventType.DOWN_RUN);
				}

				this.dwmYChange = distanceReference;

			} else {
				distance = this.VAR.DISTANCE_HUMAN;
			}

			if (event == null) {
				event = speedDisambiguator(ply, EventType.WALK, EventType.RUN);
			}
			distance = reevaluateDistance(event, distance);

			if (dwm > distance) {
				produceStep(ply, event, verticalOffsetAsMinus);
				stepped(ply, event);
				this.dmwBase = distanceReference;
			}
		}

		if (ply.onGround) { // This fixes an issue where the value is evaluated
							// while the player is between two steps in the air
							// while descending stairs
			this.yPosition = ply.posY;
		}
	}

	protected void produceStep(final EntityPlayer ply, final EventType event) {
		produceStep(ply, event, 0d);
	}

	protected void produceStep(final EntityPlayer ply, EventType event, final double verticalOffsetAsMinus) {
		if (!this.mod.getSolver().playSpecialStoppingConditions(ply)) {
			if (event == null)
				event = speedDisambiguator(ply, EventType.WALK, EventType.RUN);
			this.mod.getSolver().playAssociation(ply,
					this.mod.getSolver().findAssociationForPlayer(ply, verticalOffsetAsMinus, this.isRightFoot), event);
			this.isRightFoot = !this.isRightFoot;
		}

		this.stepThisFrame = true;
	}

	protected void stepped(final EntityPlayer ply, final EventType event) {
	}

	protected float reevaluateDistance(final EventType event, final float distance) {
		return distance;
	}

	protected void simulateAirborne(final EntityPlayer ply) {
		if ((ply.onGround || ply.isOnLadder()) == this.isFlying) {
			this.isFlying = !this.isFlying;
			simulateJumpingLanding(ply);
		}

		if (this.isFlying)
			this.fallDistance = ply.fallDistance;
	}

	protected void simulateJumpingLanding(final EntityPlayer ply) {
		if (this.mod.getSolver().hasSpecialStoppingConditions(ply))
			return;

		final boolean isJumping = ply.isJumping;

		if (this.isFlying && isJumping) { // ply.isJumping)
			if (this.VAR.EVENT_ON_JUMP) {
				final double speed = ply.motionX * ply.motionX + ply.motionZ * ply.motionZ;

				if (speed < this.VAR.SPEED_TO_JUMP_AS_MULTIFOOT) { // STILL JUMP
					playMultifoot(ply, 0.4d, EventType.JUMP); // 2 -
																// 0.7531999805212d
																// (magic number
																// for vertical
																// offset?)
				} else {
					playSinglefoot(ply, 0.4d, EventType.JUMP, this.isRightFoot); // RUNNING
					// JUMP
					// Do not toggle foot: After landing sounds, the first foot
					// will be same as the one used to jump.
				}
			}
		} else if (!this.isFlying) {
			if (this.fallDistance > this.VAR.LAND_HARD_DISTANCE_MIN) {
				playMultifoot(ply, 0d, EventType.LAND); // Always assume the
														// player lands on their
														// two feet
				// Do not toggle foot: After landing sounds, the first foot will
				// be same as the one used to jump.
			} else if (!this.stepThisFrame && !ply.isSneaking()) {
				playSinglefoot(ply, 0d, speedDisambiguator(ply, EventType.CLIMB, EventType.CLIMB_RUN),
						this.isRightFoot);
				this.isRightFoot = !this.isRightFoot;
			}

		}
	}

	protected EventType speedDisambiguator(final EntityPlayer ply, final EventType walk, final EventType run) {
		final double velocity = ply.motionX * ply.motionX + ply.motionZ * ply.motionZ;
		return velocity > this.VAR.SPEED_TO_RUN ? run : walk;
	}

	private void simulateBrushes(final EntityPlayer ply) {
		if (this.brushesTime > MyUtils.currentTimeMillis())
			return;

		this.brushesTime = MyUtils.currentTimeMillis() + 100;

		if ((ply.motionX == 0d && ply.motionZ == 0d) || ply.isSneaking())
			return;

		final int yy = MathHelper.floor_double(ply.posY - 0.1d - ply.getYOffset() - (ply.onGround ? 0d : 0.25d));
		final Association assos = this.mod.getSolver().findAssociationForBlock(MathHelper.floor_double(ply.posX), yy,
				MathHelper.floor_double(ply.posZ), "find_messy_foliage");
		if (assos != null) {
			if (!this.isMessyFoliage) {
				this.isMessyFoliage = true;
				this.mod.getSolver().playAssociation(ply, assos, EventType.WALK);
			}
		} else {
			this.isMessyFoliage = false;
		}
	}

	protected void playSinglefoot(final EntityPlayer ply, final double verticalOffsetAsMinus, final EventType eventType,
			final boolean foot) {
		final Association assos = this.mod.getSolver().findAssociationForPlayer(ply, verticalOffsetAsMinus,
				this.isRightFoot);
		this.mod.getSolver().playAssociation(ply, assos, eventType);
	}

	protected void playMultifoot(final EntityPlayer ply, final double verticalOffsetAsMinus,
			final EventType eventType) {
		// STILL JUMP
		final ISolver s = this.mod.getSolver();
		final Association leftFoot = s.findAssociationForPlayer(ply, verticalOffsetAsMinus, false);
		final Association rightFoot = s.findAssociationForPlayer(ply, verticalOffsetAsMinus, true);
		s.playAssociation(ply, leftFoot, eventType);
		s.playAssociation(ply, rightFoot, eventType);
	}

	protected float scalex(final float number, final float min, final float max) {
		return MathHelper.clamp_float((number - min) / (max - min), 0.0F, 1.0F);
	}
}