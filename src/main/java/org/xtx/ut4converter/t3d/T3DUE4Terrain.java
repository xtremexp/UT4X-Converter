/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.List;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.ue4.LandscapeComponent;
import org.xtx.ut4converter.ucore.ue4.LandscapeHeightfieldCollisionComponent;

/**
 * Very basic implementation of Unreal Engine 4 terrain
 * @author XtremeXp
 */
public class T3DUE4Terrain extends T3DActor {

    UPackageRessource landscapeMaterial;
    
    UPackageRessource landscapeHoleMaterial;
    
    int collisionMipLevel;
    int collisionThickness;
    
    short componentSizeQuads;
    short subsectionSizeQuads;
    short numSubsections;
    boolean bUsedForNavigation;
    short maxPaintedLayersPerComponent;
    
    List<LandscapeHeightfieldCollisionComponent> collisionComponents;
    
    List<LandscapeComponent> landScapeComponents;
    
    public T3DUE4Terrain(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        initialise();
    }
    
    /**
     * Creates t3d ue4 terrain from unreal engine 2 terrain
     * @param ue2Terrain 
     */
    public T3DUE4Terrain(T3DUE2Terrain ue2Terrain){
        super(ue2Terrain.getMapConverter(), ue2Terrain.t3dClass);
        
        initialise();
        // TODO
    }
    
    private void initialise(){
        collisionThickness = 16;
        bUsedForNavigation = true;
    }
    
}
