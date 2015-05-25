/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue3;

import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class TerrainLayer implements T3D {
    
    String name;
    
    /**
     * Impossible to get more data about TLS with t3d file
     */
    String terrainLayerSetup;
    boolean highlighted;
    boolean wireframeHighlighted;

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

    @Override
    public String toT3d(StringBuilder sb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
