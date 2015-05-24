/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue2;

import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * @author XtremeXp
 */
public class TerrainLayer implements T3D {

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    enum TextureMapAxis {
        TEXMAPAXIS_XY,
        TEXMAPAXIS_XS,
        TEXMAPAXIS_YZ,
    }
    
    UPackageRessource alphaMap;
    Float kFriction;
    Float kRestitution;
    Rotator layerRotation;
    UPackageRessource texture;
    TextureMapAxis textureMapAxis;
    Double textureRotation;
    Double uPan, vPan;
    Double uScale, vScale;

    public TerrainLayer(){
        
    }
    
    
    public TerrainLayer(UPackageRessource alphaMap, UPackageRessource texture) {
        this.alphaMap = alphaMap;
        this.texture = texture;
    }
    
    
    
    
    
}
