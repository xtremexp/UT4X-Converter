/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class LandscapeComponent implements T3D {
    
    short sectionBaseX;
    
    short sectionBaseY;
    
    short componentSizeQuads;
    
    short subsectionSizeQuads;
    
    Vector4d weightmapScaleBias;
    
    float weightmapSubsectionOffset;
    
    Vector4d heightmapScaleBias;
    
    short collisionMipLevel;
    
    Vector3d relativeLocation;

    public LandscapeComponent() {
        initialise();
    }
    
    private void initialise(){
        weightmapSubsectionOffset = 1;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
