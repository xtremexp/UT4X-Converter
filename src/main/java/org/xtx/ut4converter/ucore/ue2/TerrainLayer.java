/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UCCExporter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.tools.Installation;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * @author XtremeXp
 */
public class TerrainLayer implements T3D {

    
    
    enum TextureMapAxis {
        TEXMAPAXIS_XY,
        TEXMAPAXIS_XS,
        TEXMAPAXIS_YZ,
    }
    
    UPackageRessource alphaMapTexture;
    
    /**
     * List of alpha map values if alphaMapTexture set
     * and values read from texture file
     */
    List<Integer> alphaMap;
    
    Float kFriction;
    Float kRestitution;
    Rotator layerRotation;
    UPackageRessource texture;
    TextureMapAxis textureMapAxis;
    Float textureRotation;
    Float uPan, vPan;
    Float uScale, vScale;
    
    MapConverter mapConverter;

    public TerrainLayer(MapConverter mapConverter){
        this.mapConverter = mapConverter;
        initialise();
    }

    private void initialise(){
        textureMapAxis = TextureMapAxis.TEXMAPAXIS_XY;
        alphaMap = new ArrayList<>();
    }
    
    @Override
    public void convert() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void scale(Double newScale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean analyseT3DData(String line) {
        
        if(line.contains("AlphaMap=")){
            alphaMapTexture = mapConverter.getUPackageRessource(line.split("AlphaMap=")[1].split("\\'")[1], T3DRessource.Type.TEXTURE);
        }
        
        else if(line.contains("Texture=")){
            texture = mapConverter.getUPackageRessource(line.split("Texture=")[1].split("\\'")[1], T3DRessource.Type.TEXTURE);
        }
        
        else if(line.contains("UScale")){
            uScale = Float.valueOf(line.split("UScale=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("VScale")){
            vScale = Float.valueOf(line.split("VScale=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("UPan")){
            uPan = Float.valueOf(line.split("UPan=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("VPan")){
            vPan = Float.valueOf(line.split("VPan=")[1].split("\\,")[0]);
        }
        
        else if(line.contains("TextureRotation")){
            textureRotation = Float.valueOf(line.split("TextureRotation=")[1].split("\\,")[0]);
        }
        
        else {
            return false;
        }
        
        return true;
    }
    
    public void load(){
        
        // TODO extract texture
        if(alphaMap != null){
            
            
        }
        
    }
    
    
    
}
