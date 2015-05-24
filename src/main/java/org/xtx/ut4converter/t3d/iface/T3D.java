/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d.iface;

/**
 *
 * @author XtremeXp
 */
public interface T3D {
    
    public abstract void convert();
    
    public abstract void scale(Double newScale);
    
    public boolean analyseT3DData(String line);
}
