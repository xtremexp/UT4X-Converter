/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

import jogl.DDSImage;

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
	 * @param zeroOneScale
	 *            If true will use 0..1 scale else 0...255 (UE1/UE2 scale)
	 * @return HSV color converted to RGB
	 */
	public static RGBColor HSVToLinearRGB(float hue, float saturation, float brightness, boolean zeroOneScale) {
		return HSVToLinearRGB(new HSVColor(hue, saturation, brightness), zeroOneScale);
	}

	/**
	 * Convert color in HSV (Hue, Saturation, Value) format to RGB (Red, Green,
	 * Blue) format
	 * 
	 * @param hsv
	 *            Color in HSV format
	 * @param zeroOneScale
	 *            If true will use 0..1 scale else 0...255 (UE1/UE2 scale)
	 * @return Color in RGB format
	 */
	public static RGBColor HSVToLinearRGB(HSVColor hsv, boolean zeroOneScale) {
		// Hue normally in 0-360 range

		// In this color, R = H, G = S, B = V
		// UE1/2 'range' to UE3/4 range conversion
		// In Unreal Engine 1/2 to UE3/UE4:
		// 0 - Hue -> 255 ===> 0 - Hue -> 360
		// 0 - Sat -> 255 ===> 0 - Sat -> 1.0
		// 0 - Val -> 255 ===> 0 - Val -> 1.0

		float Hue = hsv.H * 360f / 255f;
		float Saturation = hsv.S / 255f;
		float Value = hsv.V / 255f;

		float HDiv60 = Hue / 60.0f;
		float HDiv60_Floor = (float) Math.floor(HDiv60);
		float HDiv60_Fraction = HDiv60 - HDiv60_Floor;

		float[] RGBValues;
		RGBValues = new float[] { Value, Value * (1.0f - Saturation), Value * (1.0f - (HDiv60_Fraction * Saturation)), Value * (1.0f - ((1.0f - HDiv60_Fraction) * Saturation)), };

		int[][] RGBMatrix;

		RGBMatrix = new int[][] { { 0, 3, 1 }, { 2, 0, 1 }, { 1, 0, 3 }, { 1, 2, 0 }, { 3, 1, 0 }, { 0, 1, 2 }, };

		int SwizzleIndex = ((int) HDiv60_Floor) % 6;

		RGBColor rgb = new RGBColor();
		rgb.R = RGBValues[RGBMatrix[SwizzleIndex][0]];
		rgb.G = RGBValues[RGBMatrix[SwizzleIndex][1]];
		rgb.B = RGBValues[RGBMatrix[SwizzleIndex][2]];
		rgb.A = 1f;
		
		if(!zeroOneScale){
			rgb.R *= 255f;
			rgb.G *= 255f;
			rgb.B *= 255f;
			rgb.A *= 255f;
		}

		return rgb;
	}

	/**
	 * Get texture dimension from texture size
	 * 
	 * @param ftex
	 *            Texture file
	 * @return Texture dimensions
	 * @throws IOException IO exception
	 */
	public static Dimension getTextureDimensions(File ftex) throws IOException {
		if (ftex == null || !ftex.exists() || ftex.isDirectory() || Files.size(ftex.toPath()) == 0) {
			return null;
		}

		if (ftex.getName().endsWith(".dds")) {
			DDSImage ddsImage = DDSImage.read(ftex);
			return new Dimension(ddsImage.getWidth(), ddsImage.getHeight());
		}

		BufferedImage bufferedImage = loadImage(ftex);

		return new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
	}

	private static BufferedImage loadImage(File imageFile) throws IOException {
		return ImageIO.read(imageFile);
	}
}
