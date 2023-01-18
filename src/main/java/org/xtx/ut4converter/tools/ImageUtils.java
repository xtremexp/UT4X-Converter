/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class related to image (such as extracting data from textures, ...)
 * or colors
 * 
 * @author XtremeXp
 */
public class ImageUtils {

	/**
	 * Convert color in HSV (Hue, Saturation, Value) format to RGB (Red, Green,
	 * Blue) format
	 * 
	 * @param hue
	 *            Hue
	 * @param saturation
	 *            Saturation
	 * @param brightness
	 *            Brightness
	 * @param is255Scale
	 *            If true h,s,v are in [0-255] range
	 * @return HSV color converted to RGB
	 */
	public static RGBColor HSVToLinearRGB(float hue, float saturation, float brightness, boolean is255Scale) {
		return HSVToLinearRGB(new HSVColor(hue, saturation, brightness, is255Scale));
	}

	/**
	 * Convert color in HSV (Hue, Saturation, Value) format to RGB (Red, Green,
	 * Blue) format
	 *
	 * @param hsv Color in HSV format either in [0-255] range or [0-1] Range
	 * @return Color in RGB format in [0-1] Range
	 */
	public static RGBColor HSVToLinearRGB(HSVColor hsv) {

		// Transformation needs to be in [0-1] range
		hsv.toRange(false);

		float Hue = hsv.H;
		float Saturation = hsv.S;
		// UE2 can sometimes have brightness > 1 (LightBrightness > 255 in [0-255+ range]
		float Value = Math.min(hsv.V, 1);

		float HDiv60 = Hue / 60.0f;
		float HDiv60_Floor = (float) Math.floor(HDiv60);
		float HDiv60_Fraction = HDiv60 - HDiv60_Floor;

		float[] RGBValues;
		RGBValues = new float[] { Value, Value * (1.0f - Saturation), Value * (1.0f - (HDiv60_Fraction * Saturation)), Value * (1.0f - ((1.0f - HDiv60_Fraction) * Saturation)), };

		int[][] RGBMatrix;

		RGBMatrix = new int[][] { { 0, 3, 1 }, { 2, 0, 1 }, { 1, 0, 3 }, { 1, 2, 0 }, { 3, 1, 0 }, { 0, 1, 2 }, };

		int SwizzleIndex = ((int) HDiv60_Floor) % 6;

		RGBColor rgb = new RGBColor(false);
		rgb.R = RGBValues[RGBMatrix[SwizzleIndex][0]];
		rgb.G = RGBValues[RGBMatrix[SwizzleIndex][1]];
		rgb.B = RGBValues[RGBMatrix[SwizzleIndex][2]];
		rgb.A = 1f;

		return rgb;
	}

	/**
	 * Get texture dimension from file texture
	 *
	 * @param ftex Texture file
	 * @return Texture dimensions
	 * @throws IOException Error reading file texture
	 */
	public static Dimension getTextureDimensions(File ftex) throws IOException {

		if (ftex == null || !ftex.exists() || ftex.isDirectory() || Files.size(ftex.toPath()) == 0) {
			return null;
		}

		BufferedImage bufferedImage = ImageIO.read(ftex);

		return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
	}
}
