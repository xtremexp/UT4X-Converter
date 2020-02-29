/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

/**
 *
 * @author XtremeXp
 */
public class RGBColor {
	/**
	 * Red, Green, Blue, Alpha
	 */
	public float R, G, B, A;

	/**
    *
    */
	public RGBColor() {

	}

	/**
	 * Change values from 0-255(UE1/2) range to 0-1 (for UE3/4)
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
