/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.ue2.TerrainDecoLayer;
import org.xtx.ut4converter.ucore.ue2.TerrainLayer;

/**
 *
 * @author XtremeXp
 */
public class T3DUE2Terrain extends T3DActor {

    boolean bKCollisionHalfRes;
    double decoLayerOffset;
    
    List<TerrainDecoLayer> decoLayers;
    boolean inverted;
    
    List<TerrainLayer> layers;
    
    /**
     * TerrainMap
     */
    UPackageRessource heightMapTexture;
    Dimension heightMapTextureDimensions;
    
    int[][] heightMap;
    
    Vector3d terrainScale;
    short terrainSectorSize;
    
    UPackageRessource vertexLightMap;
    
    /**
     * Invisible pieces of terrain
     */
    List<Integer> quadVisibilityBitmaps;
    
    
    public T3DUE2Terrain(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        
        initialise();
    }
    
    private void initialise(){
        decoLayers = new ArrayList<>();
        layers = new ArrayList<>();
        quadVisibilityBitmaps = new ArrayList<>();
    }
    
    @Override
    public boolean analyseT3DData(String line){
        
        // TerrainMap=Texture'myLevel.Package0.TowerHeightMap'
        if(line.startsWith("TerrainMap=")){
            heightMapTexture = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.TEXTURE);
        } 
        // TerrainScale=(X=15.000000,Y=15.000000,Z=2.000000)
        else if(line.startsWith("TerrainScale")){
            terrainScale = T3DUtils.getVector3d(line, 1d);
        }
        
        else if(line.startsWith("Layers(") && line.contains("AlphaMap=")){
            TerrainLayer terrainLayer = new TerrainLayer(mapConverter);
            terrainLayer.analyseT3DData(line);
            
            layers.add(terrainLayer);
        }
        
        else if(line.startsWith("DecoLayers(")){
            TerrainDecoLayer decoLayer = new TerrainDecoLayer();
            decoLayer.analyseT3DData(line);
            
            decoLayers.add(decoLayer);
        }
        
        else if(line.startsWith("QuadVisibilityBitmap")){
            quadVisibilityBitmaps.add(Integer.valueOf(line.split("\\=")[1]));
        }
        else {
            return super.analyseT3DData(line);
        }
        
        return true;
    }
    
    @Override
    public void convert(){
        
        try {
            // TODO terrain data from heightmap, layer/decolayer textures
            loadTerrainData();
            
            // TODO convert to UT4
            if(mapConverter.isTo(UnrealEngine.UE4)){
                T3DUE4Terrain ue4Terrain = new T3DUE4Terrain(this);
                replaceWith(ue4Terrain);
            }
        } catch (InterruptedException | IOException ex) {
            mapConverter.getLogger().log(Level.SEVERE, "Could not load terrain data", ex);
        }
    }
    
    
    
    public int[][] getHeightMap() {
		return heightMap;
	}

	/**
     * Loads terrain heightmap and layers data from textures
     */
    private void loadTerrainData() throws InterruptedException, IOException{
        
        for(TerrainLayer layer : layers){
            layer.load();
        }
        
        for(TerrainDecoLayer decoLayer : decoLayers){
            decoLayer.load();
        }
        
        loadHeightMap();
    }
    
    /**
     * Loads heightmap data from heightmap texture.
     * Code adapted refactored from good old ut3 converter
     * @throws InterruptedException
     * @throws IOException 
     */
    private void loadHeightMap() throws InterruptedException, IOException{
        
        // extract texture
        if(heightMapTexture != null){
            
            // Export heightmap texture to .bmp
            UCCExporter uccExporter = new UCCExporter(mapConverter);
            uccExporter.setForcedUccOption(UCCExporter.UccOptions.TEXTURE_BMP);
            File exportFolder = new File(mapConverter.getTempExportFolder() + File.separator + "Terrain" + File.separator + heightMapTexture.getUnrealPackage().getName() + File.separator);
            uccExporter.setForcedExportFolder(exportFolder);
            
            heightMapTexture.export(uccExporter, true);
            
            // Convert heightmap texture to .tiff
            List<String> logs = new ArrayList<>();
            File bmpHeightMap = heightMapTexture.getExportedFile();
            File tiffHeightMap = new File(exportFolder + bmpHeightMap.getName().split("\\.")[0] + ".tiff");
            
            String command = Installation.getG16ConvertFile() + " " + bmpHeightMap + " " + tiffHeightMap;
            
            mapConverter.getLogger().log(Level.INFO, "Converting " + bmpHeightMap.getName() + " to " + tiffHeightMap.getName() + " terrain texture");
            
            Installation.executeProcess(command, logs);
            
            BufferedImage image = ImageIO.read(tiffHeightMap);
            heightMapTextureDimensions = new Dimension(image.getWidth(), image.getHeight());
            Raster rs = image.getTile(0, 0);
            
            int a[] = null;
            heightMap = new int[image.getWidth()][image.getHeight()];
            
            for(int y=0; y < rs.getWidth(); y++)
            {
                for(int x=0; x < rs.getHeight();x++)
                {
                	heightMap[x][y] = rs.getPixel(x, y, a)[0];
                	
                	if(heightMap[x][y] == 0){
                		System.out.println("X:"+x +"Y:"+y);
                	}
                }
            }
            
        }
    }
    
    /**
     * 
     */
    public void scale(Double newScale){
    	if(this.terrainScale != null){
    		this.terrainScale.scale(newScale);
    	}
    }

	public void setHeightMap(int[][] heightMap) {
		this.heightMap = heightMap;
	}
    
    
}
