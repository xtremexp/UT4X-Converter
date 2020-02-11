/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.geom;

import org.xtx.ut4converter.UTGames.UnrealEngine;

import javax.vecmath.Vector3d;

/**
 *
 * @author XtremeXp
 */
public class Rotator {

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

	public Rotator(double pitch, double yaw, double roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	public Rotator(Vector3d rotator) {
		this.pitch = rotator.y;
		this.yaw = rotator.z;
		this.roll = rotator.x;
	}
	
	public static double getDefaultTwoPi(UnrealEngine engine){
		if(engine == UnrealEngine.UE4){
			return 360d;
		} else {
			return 65536d;
		}
	}
}
