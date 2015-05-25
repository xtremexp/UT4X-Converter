/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.MapConverter;
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
     * Terrain heightmap
     */
    UPackageRessource terrainMap;
    
    Vector3d terrainScale;
    short terrainSectorSize;
    
    UPackageRessource vertexLightMap;
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
            terrainMap = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.TEXTURE);
        } 
        // TerrainScale=(X=15.000000,Y=15.000000,Z=2.000000)
        else if(line.startsWith("TerrainScale")){
            terrainScale = T3DUtils.getVector3d(line, 1d);
        }
        
        else if(line.startsWith("Layers(")){
            TerrainLayer terrainLayer = new TerrainLayer();
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
        
        // TODO load heightmap data from terrain heightmap texture
        
        // TODO convert to UT4
    }
    
}
