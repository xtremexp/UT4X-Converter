/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.ArrayList;
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
    
    List<LandscapeComponent> landscapeComponents;
    
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
        
        this.name = ue2Terrain.name;
        this.location = ue2Terrain.location;
        this.scale3d = ue2Terrain.scale3d;
        
        int numComponent = 0;
        
        LandscapeHeightfieldCollisionComponent collisionComponent = new LandscapeHeightfieldCollisionComponent(numComponent);
        collisionComponent.setCollisionSizeQuads(ue2Terrain.heightMapTextureDimensions.width);
        collisionComponent.setCollisionHeightData(ue2Terrain.heightMap);
        
        collisionComponents.add(collisionComponent);
                
        LandscapeComponent landscapeComponent = new LandscapeComponent(numComponent);
        landscapeComponent.setComponentSizeQuads(componentSizeQuads);
        
        landscapeComponent.getLandscapeHeightData().addAll(ue2Terrain.heightMap);
        // converting to much more accurate heightmap data (e.g: 32768 -> 80800080x = 2155872384)
        landscapeComponent.convertUe2ToUe4HeightMap();
        
        landscapeComponent.setColisionComponent(collisionComponent);
        collisionComponent.setRenderComponent(landscapeComponent);
        
        landscapeComponents.add(landscapeComponent);
    }
    

    private void initialise(){
        collisionThickness = 16;
        numSubsections = 1;
        bUsedForNavigation = true;
        collisionComponents = new ArrayList<>();
        landscapeComponents = new ArrayList<>();
    }
    
    
    @Override
    public boolean isValidWriting(){
        
        return false; // disabled until working (crashing for the moment)
        //return !landscapeComponents.isEmpty();
    }
    
    @Override
    public String toString(){
        
        sbf.append(IDT).append("Begin Actor Class=Landscape Name=").append(name).append("\n");
        
        for(LandscapeHeightfieldCollisionComponent collisionComp : collisionComponents){
            collisionComp.toT3d(sbf);
        }
        
        for(LandscapeComponent landscape : landscapeComponents){
            landscape.toT3d(sbf);
        }
        
        sbf.append(IDT).append("\tBegin Object Name=\"RootComponent0\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");
        
        if(landscapeMaterial != null){
            sbf.append(IDT).append("LandscapeMaterial='").append(landscapeMaterial.getConvertedName(mapConverter)).append("'\n");
        }
        
        int idx = 0;
        
        for(LandscapeComponent landscape : landscapeComponents){
            sbf.append(IDT).append("\tLandscapeComponents(").append(idx).append(")=LandscapeComponent'").append(landscape.getName()).append("'\n");
        }
        
        idx = 0;
        
        for(LandscapeHeightfieldCollisionComponent collisionComp : collisionComponents){
            sbf.append(IDT).append("\tCollisionComponents(").append(idx).append(")=CollisionComponent'").append(collisionComp.getName()).append("'\n");
        }
        
        sbf.append(IDT).append("\tComponentSizeQuads=").append(componentSizeQuads).append("\n");
        sbf.append(IDT).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
        sbf.append(IDT).append("\tNumSubsections=").append(numSubsections).append("\n");
        sbf.append(IDT).append("\tRootComponent=RootComponent0\n");
        
        writeEndActor();
        
        return sbf.toString();
    }
    
}
