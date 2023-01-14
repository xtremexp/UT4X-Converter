/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.ue2.TerrainDecoLayer;
import org.xtx.ut4converter.ucore.ue2.TerrainLayer;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author XtremeXp
 */
public class T3DUE2Terrain extends T3DActor {


	/**
	 * Decolayers - Not used since UE4 do not have such thing
	 */
	private final List<TerrainDecoLayer> decoLayers = new LinkedList<>();

	private final List<TerrainLayer> layers = new LinkedList<>();

    /**
	 * TerrainMap
	 */
	private UPackageRessource heightMapTexture;

	/**
	 * Dimensions of heightmap texture
	 */
	private Dimension heightMapTextureDimensions;

	/**
	 * Heightmap values of terrain
	 */
	private final List<Integer> heightMap = new LinkedList<>();

	/**
	 * Default terrain scale in UE2
	 */
	private Vector3d terrainScale = new Vector3d(64d, 64d, 64d);

	/**
	 * Invisible pieces of terrain
	 */
	private final Map<Integer, Long> quadVisibilityBitmaps = new HashMap<>();

	public T3DUE2Terrain(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	@Override
	public boolean analyseT3DData(String line) {

		// TerrainMap=Texture'myLevel.Package0.TowerHeightMap'
		if (line.startsWith("TerrainMap=")) {
			heightMapTexture = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.TEXTURE);
		}
		// TerrainScale=(X=15.000000,Y=15.000000,Z=2.000000)
		else if (line.startsWith("TerrainScale")) {
			terrainScale = T3DUtils.getVector3d(line, 64d);
		}

		else if (line.startsWith("Layers(") && line.contains("AlphaMap=")) {
			TerrainLayer terrainLayer = new TerrainLayer(mapConverter);
			terrainLayer.analyseT3DData(line);

			layers.add(terrainLayer);
		}

		else if (line.startsWith("DecoLayers(")) {
			TerrainDecoLayer decoLayer = new TerrainDecoLayer();
			decoLayer.analyseT3DData(line);

			decoLayers.add(decoLayer);
		}

		// QuadVisibilityBitmap(0)=-65540
		else if (line.startsWith("QuadVisibilityBitmap")) {
			Long val = Long.valueOf(line.split("=")[1]);
			int key = Integer.parseInt(line.split("\\(")[1].split("\\)")[0]);

			quadVisibilityBitmaps.put(key, val);
		} else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	@Override
	public void convert() {

		try {
			if(this.getMapConverter().convertTextures()) {
				loadTerrainData();

				if (mapConverter.isTo(UnrealEngine.UE4)) {
					T3DUE4Terrain ue4Terrain = new T3DUE4Terrain(this);
					replaceWith(ue4Terrain);
				}
			}
		} catch (InterruptedException | IOException ex) {
			mapConverter.getLogger().log(Level.SEVERE, "Could not load terrain data", ex);
		}
	}

	public List<Integer> getHeightMap() {
		return heightMap;
	}

	/**
	 * Loads terrain heightmap and layers data from textures
	 */
	public void loadTerrainData() throws InterruptedException, IOException {

		for (TerrainLayer layer : layers) {
			layer.load();
		}

		for (TerrainDecoLayer decoLayer : decoLayers) {
			decoLayer.load();
		}

		loadHeightMap();
	}

	/**
	 * Loads heightmap data from heightmap texture. Code adapted refactored from
	 * good old ut3 converter
	 * 
	 * @throws InterruptedException Exception thrown
	 * @throws IOException Exception thrown
	 */
	private void loadHeightMap() throws InterruptedException, IOException {

		// extract texture
		if (heightMapTexture != null && heightMapTextureDimensions == null) {

			// Export heightmap texture to .bmp
			final UCCExporter uccExporter = new UCCExporter(mapConverter);
			uccExporter.setForcedUccOption(UCCExporter.UccOptions.TEXTURE_BMP);

			final File exportFolder = new File(mapConverter.getTempExportFolder() + File.separator + "Terrain" + File.separator + heightMapTexture.getUnrealPackage().getName() + File.separator);
			uccExporter.setForcedExportFolder(exportFolder);
			uccExporter.setForceSetNotExported(true);
			heightMapTexture.export(uccExporter, true, true);

			// Convert heightmap texture to .tiff
			List<String> logs = new ArrayList<>();
			File bmpHeightMap = heightMapTexture.getExportInfo().getExportedFileByExtension("bmp");
			
			if(bmpHeightMap != null){
				File tiffHeightMap = new File(exportFolder + File.separator + bmpHeightMap.getName().split("\\.")[0] + ".tiff");

				String command = "\"" + Installation.getG16ConvertFile() + "\" \"" + bmpHeightMap + "\" \"" + tiffHeightMap + "\"";

				mapConverter.getLogger().log(Level.INFO, "Converting " + bmpHeightMap.getName() + " to " + tiffHeightMap.getName() + " terrain texture");

				Installation.executeProcess(command, logs);

				// reads heightmap data from image file
				final BufferedImage image = ImageIO.read(tiffHeightMap);
				heightMapTextureDimensions = new Dimension(image.getWidth(), image.getHeight());
				Raster rs = image.getTile(0, 0);

				int[] a = null;

				for (int y = 0; y < rs.getWidth(); y++) {
					for (int x = 0; x < rs.getHeight(); x++) {
						heightMap.add(rs.getPixel(x, y, a)[0]);
					}
				}
			} else {
				mapConverter.getLogger().log(Level.INFO, "Could not find terrain heightmap texture for resource " + heightMapTexture.getFullName(true));
			}

		}
	}

	/**
     * 
     */
	@Override
	public void scale(double newScale) {
		if (this.terrainScale != null) {
			this.terrainScale.setX(this.terrainScale.getX() * newScale);
			this.terrainScale.setY(this.terrainScale.getY() * newScale);
		}
	}

	public Map<Integer, Long> getQuadVisibilityBitmaps() {
		return quadVisibilityBitmaps;
	}

	/**
	 * Returns the total number of squares this terrain has
	 * 
	 * @return Number of squares
	 */
	public int getTotalSquares() {
		return heightMapTextureDimensions.height * heightMapTextureDimensions.width;
	}

	public Dimension getHeightMapTextureDimensions() {
		return heightMapTextureDimensions;
	}

	public List<TerrainLayer> getLayers() {
		return layers;
	}

	public Vector3d getTerrainScale() {
		return terrainScale;
	}

	/**
	 * Converter does not support yet terrain sizes > 256X256
	 *
	 * @return <code>true</code> if terrain is valid for conversion
	 */
	@Override
	public boolean isValidConverting() {

		try {
			// extract heightmap texture to get terrain size
			loadHeightMap();
		} catch (InterruptedException | IOException e) {
			mapConverter.getLogger().log(Level.SEVERE, "Error while extracting heightmap", e);
		}

		if (heightMapTextureDimensions == null) {
			return false;
		} else if (Math.max(heightMapTextureDimensions.getHeight(), heightMapTextureDimensions.getWidth()) > 256) {
			mapConverter.getLogger().log(Level.SEVERE, "Terrain conversion with size greater than 256X256 not supported");
			return false;
		} else {
			return super.isValidConverting();
		}
	}

	public String toT3d(){
		throw new UnsupportedOperationException();
	}
}
