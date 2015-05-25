/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue3;

import java.util.List;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * @author XtremeXp
 */
public class TerrainDecoLayer implements T3D {

    List<Decoration> decorations;

    
    class Decoration {
        
        StaticMeshComponentFactory factory;
        
        float minScale;
        float maxScale;
        float density;
        float slopeRotationBlend;
        short randSeed;
        
        Decoration(){
            initialise();
        }
        
        private void initialise(){
            minScale = 1f;
            maxScale = 1f;
            density = 0.01f;
            
        }
    }
    
    class StaticMeshComponentFactory {
        
        UPackageRessource staticmesh;
        List<UPackageRessource> materials;
        boolean collideActors;
        boolean blockActors;
        boolean blockZeroExtent;
        boolean blockNonZeroExtent;
        boolean blockRigidBody;
        boolean hiddenGame;
        boolean hiddenEditor;
        boolean castShadow;
        
        StaticMeshComponentFactory () {
            collideActors = true;
            blockActors = true;
            blockNonZeroExtent = true;
            blockZeroExtent = true;
            blockRigidBody = true;
            castShadow = true;
        }
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
    
    @Override
    public String toT3d(StringBuilder sb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
