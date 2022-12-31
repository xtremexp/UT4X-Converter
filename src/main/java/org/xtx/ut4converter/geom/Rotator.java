/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.geom;

import org.xtx.ut4converter.UTGames.UnrealEngine;

import java.util.Objects;

/**
 *
 * @author XtremeXp
 */
public class Rotator {

	/**
	 * If true range of pitch yaw, roll is [-360;+360]
	 * else [-32768:+32768]
	 */
	boolean isUE4Space;

	/**
	 * Rotate using X axis
	 */
	double pitch;
	/**
	 * Rotate using Z axis
	 */
	double yaw;
	/**
	 * Rotate using Y axis
	 */
	double roll;

	/**
	 * Creates a rotator in UE4 range
	 * @param pitch Pitch
	 * @param yaw Yaw
	 * @param roll Roll
	 */
	public Rotator(double pitch, double yaw, double roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public double getYaw() {
		return yaw;
	}

	public double getRoll() {
		return roll;
	}

	/**
	 * Remove/refactor
	 * @param engine Unreal engine
	 * @return Size of circle in specified engine
	 */
	@Deprecated
	public static double getDefaultTwoPi(UnrealEngine engine){
		if(engine == UnrealEngine.UE4){
			return 360d;
		} else {
			return 65536d;
		}
	}

	/**
	 * Switch to UE123 range to UE4 range or vice-versa
	 * @param isUE4Space If true means, it's UE4 space
	 */
	public void switchSpace(boolean isUE4Space) {

		// switch to UE123 space
		if (isUE4Space && !this.isUE4Space) {
			pitch *= 360d / 65536d;
			yaw *= 360d / 65536d;
			roll *= 360d / 65536d;
		}
		// switch to UE4 space
		else if (!isUE4Space && this.isUE4Space) {
			pitch *= 65536d / 360d;
			yaw *= 65536d / 360d;
			roll *= 65536d / 360d;

			// in UE4 space the original "X" axis is rotated by 180°
		}

		this.isUE4Space = isUE4Space;
	}

	public boolean isUE4Space() {
		return isUE4Space;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rotator rotator = (Rotator) o;
		return isUE4Space == rotator.isUE4Space && Double.compare(rotator.pitch, pitch) == 0 && Double.compare(rotator.yaw, yaw) == 0 && Double.compare(rotator.roll, roll) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(isUE4Space, pitch, yaw, roll);
	}

	@Override
	public String toString() {
		return "(Pitch=" + pitch + ",Yaw=" + yaw + ",Roll=" + roll + ")";
	}

	public String toUEString(){
		return "(X=" + pitch + ",Y=" + roll + ",Z=" + yaw + ")";
	}
}
