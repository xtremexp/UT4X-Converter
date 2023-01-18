/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

/**
 * RGB Color
 *
 * @author XtremeXp
 */
public class RGBColor {

    boolean is255Range;

    /**
     * Red, Green, Blue, Alpha color values
     * Range is 0-255 (integer) for UE1/2/3
     * Range is 0-1 (float) for UE1/2/3
     */
    public float R;

    /**
     * Green - Range [0-1] or [0-255] range if is255Range true
     */
    public float G;

    /**
     * Blue - Range [0-1] or [0-255] range if is255Range true
     */
    public float B;

    /**
     * Alpha - Range [0-1] or [0-255] range if is255Range true
     */
    public float A;

    /**
     *
     */
    public RGBColor(boolean is255Range) {
        this.is255Range = is255Range;
    }

    /**
     * @param r Red value in [0,1] range or [0-255] range if is255Range true
     * @param g Green value in [0,1] range or [0-255] range if is255Range true
     * @param b Blue value in [0,1] range or [0-255] range if is255Range true
     * @param a Alpha value in [0,1] range or [0-255] range if is255Range true
     */
    public RGBColor(float r, float g, float b, float a, boolean is255Range) {
        this.R = r;
        this.G = g;
        this.B = b;
        this.A = a;
        this.is255Range = is255Range;
    }

    /**
     * Return Color as t3d.
     * Sometimes 0-255 range is wanted (e.g: UE4 light color), sometimes 0-1 range (for UE4 ambient light of postprocessvolumes)
     * @param use255Range If true will write with 0-255 range
     * @return T3D
     */
    public String toT3D(boolean use255Range) {
        toRange(use255Range);
        return "(B=" + this.B + ",G=" + this.G + ",R=" + this.R + ",A=" + this.A + ")";
    }

    /**
     * Allow switching values from UE1/UE2/UE3 ranges [0-255] to UE4/UE4 ranges [0-1] or [0-360] for Hue
     *
     * @param to255Range If true will switch to 255 range else [0-1]
     */
    public void toRange(boolean to255Range) {
        if (!to255Range && this.is255Range) {
            this.R /= 255f;
            this.G /= 255f;
            this.B /= 255f;
            this.A /= 255f;
            this.is255Range = false;
        } else if (to255Range && !this.is255Range) {
            this.R *= 255f;
            this.G *= 255f;
            this.B *= 255f;
            this.A *= 255f;
            this.is255Range = true;
        }
    }
}