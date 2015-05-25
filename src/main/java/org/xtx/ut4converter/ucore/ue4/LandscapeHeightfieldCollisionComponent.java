/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import java.util.List;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class LandscapeHeightfieldCollisionComponent implements T3D {
    
    short sectionBaseX;
    short sectionBaseY;
    
    short collisionSizeQuads;
    float collisionScale;
    
    List<Integer> collisionHeightData;
    Vector3d relativeLocation;

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
