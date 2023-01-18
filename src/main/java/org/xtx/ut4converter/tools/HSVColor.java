/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;


/**
 * @author XtremeXp
 */
public class HSVColor {

    /**
     * If true the values are in [0-255] range.
     * Used for UE1/UE2/UE3
     */
    public boolean is255Range;

    /**
     * Hue - [0-360] range or [0-255] if is255Range
     */
    public float H;

    /**
     * Saturation - [0-1] range or [0-255] if is255Range
     */
    public float S;

    /**
     * Value - [0-1] range or [0-255] if is255Range
     * Also 'Brightness' from UE1/UE2
     */
    public float V;


    /**
     * Alpha - [0-1] range or [0-255] if is255Range
     */
    public float A = 1.f;

    /**
     * @param H Hue in [0-360] range or [0-255] if is255Range
     * @param S Saturation in [0-1] range or [0-255] if is255Range
     * @param V Brightness in [0-1] range or [0-255] if is255Range
     */
    public HSVColor(float H, float S, float V, boolean is255Range) {
        this.H = H;
        this.S = S;
        this.V = V;

		this.is255Range = is255Range;
    }

    /**
     * Allow switching values from UE1/UE2/UE3 ranges [0-255] to UE4/UE4 ranges [0-1] or [0-360] for Hue
     *
     * @param to255Range If true will switch to 255 range else [0-1]
     */
    public void toRange(boolean to255Range) {
        if (!to255Range && this.is255Range) {
            this.H /= (255f / 360f);
            this.S /= 255f;
            this.V /= 255f;
            this.is255Range = false;
        } else if (to255Range && !this.is255Range) {
            this.H *= (255f / 360f);
            this.S *= 255f;
            this.V *= 255f;
            this.is255Range = true;
        }
    }
}
