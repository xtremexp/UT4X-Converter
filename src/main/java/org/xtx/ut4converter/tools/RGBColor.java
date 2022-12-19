/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

/**
 * RGB Color
 * @author XtremeXp
 */
public class RGBColor {
	/**
	 * Red, Green, Blue, Alpha color values
	 * Range is 0-255 (integer) for UE1/2/3
	 * Range is 0-1 (float) for UE1/2/3
	 */
	public float R, G, B, A;

	/**
    *
    */
	public RGBColor() {

	}

	/**
	 * Change values from 0-255(UE1/2/3) range to 0-1 (for 4)
	 */
	public void toOneRange() {
		R /= 255;
		G /= 255;
		B /= 255;
		A /= 255;
	}

	public void toT3D(StringBuilder sbf) {
		sbf.append("(B=").append(this.B).append(",G=").append(this.G).append(",R=").append(this.R).append(",A=").append(this.A).append(")");
	}
}
