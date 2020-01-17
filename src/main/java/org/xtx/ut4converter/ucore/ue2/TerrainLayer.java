/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue2;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author XtremeXp
 */
public class TerrainLayer implements T3D {

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	enum TextureMapAxis {
		TEXMAPAXIS_XY, TEXMAPAXIS_XS, TEXMAPAXIS_YZ,
	}

	UPackageRessource alphaMapTexture;

	/**
	 * List of alpha map values if alphaMapTexture set and values read from
	 * texture file
	 */
	private final List<Integer> alphaMap = new LinkedList<>();

	UPackageRessource texture;
	private TextureMapAxis textureMapAxis = TextureMapAxis.TEXMAPAXIS_XY;
	Float textureRotation;
	Float uPan, vPan;
	Float uScale, vScale;

	MapConverter mapConverter;

	public TerrainLayer(MapConverter mapConverter) {
		this.mapConverter = mapConverter;
	}


	@Override
	public void convert() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void scale(Double newScale) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public boolean analyseT3DData(String line) {
		// all data in same line
		if (line.contains("AlphaMap=")) {
			alphaMapTexture = mapConverter.getUPackageRessource(line.split("AlphaMap=")[1].split("\\'")[1], T3DRessource.Type.TEXTURE);
		}

		if (line.contains("Texture=")) {
			texture = mapConverter.getUPackageRessource(line.split("Texture=")[1].split("\\'")[1], T3DRessource.Type.TEXTURE);
		}

		if (line.contains("UScale")) {
			uScale = Float.valueOf(line.split("UScale=")[1].split("\\,")[0]);
		}

		if (line.contains("VScale")) {
			vScale = Float.valueOf(line.split("VScale=")[1].split("\\,")[0]);
		}

		if (line.contains("UPan")) {
			uPan = Float.valueOf(line.split("UPan=")[1].split("\\,")[0]);
		}

		if (line.contains("VPan")) {
			vPan = Float.valueOf(line.split("VPan=")[1].split("\\,")[0]);
		}

		if (line.contains("TextureRotation")) {
			textureRotation = Float.valueOf(line.split("TextureRotation=")[1].split("\\,")[0]);
		}

		return true;
	}

	public void load() throws IOException {

		if (alphaMapTexture != null) {
			loadAlphaTextureMap();
		}

		if (texture != null) {
			texture.export(UTPackageExtractor.getExtractor(mapConverter, texture));
		}

	}

	/**
	 * Extract alpha texture map and load values. Code refactored from UT3
	 * converter
	 */
	private void loadAlphaTextureMap() throws IOException {

		// Export heightmap texture to .tga
		UCCExporter uccExporter = new UCCExporter(mapConverter);
		uccExporter.setForcedUccOption(UCCExporter.UccOptions.TEXTURE_TGA);
		File exportFolder = new File(mapConverter.getTempExportFolder() + File.separator + "Terrain" + File.separator);
		exportFolder.mkdirs();
		uccExporter.setForcedExportFolder(exportFolder);
		uccExporter.setForceSetNotExported(true);
		alphaMapTexture.export(uccExporter, true);

		int alphaValue;
		final BufferedImage img = ImageIO.read(alphaMapTexture.getExportInfo().getFirstExportedFile());

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; x < img.getWidth(); x++) {

				alphaValue = new Color(img.getRGB(x, y), true).getAlpha();
				alphaMap.add(alphaValue);
			}
		}
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public UPackageRessource getTexture() {
		return texture;
	}

}
