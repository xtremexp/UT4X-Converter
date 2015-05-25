/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.ArrayList;
import java.util.List;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
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
    
    int componentSizeQuads;
    int subsectionSizeQuads;
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
        
        if(!ue2Terrain.layers.isEmpty()){
            landscapeMaterial = ue2Terrain.layers.get(0).getTexture();
        }
        
        int numComponent = 0;
        
        this.componentSizeQuads = ue2Terrain.heightMapTextureDimensions.width - 1;
        this.subsectionSizeQuads = this.componentSizeQuads;
        
        LandscapeHeightfieldCollisionComponent collisionComponent = new LandscapeHeightfieldCollisionComponent(numComponent);
        collisionComponent.setCollisionSizeQuads(componentSizeQuads);
        collisionComponent.setCollisionHeightData(ue2Terrain.heightMap);
        
        collisionComponents.add(collisionComponent);
                
        LandscapeComponent landscapeComponent = new LandscapeComponent(numComponent);
        landscapeComponent.setComponentSizeQuads(componentSizeQuads);
        landscapeComponent.setSubsectionSizeQuads(componentSizeQuads);
        
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
        
        return !landscapeComponents.isEmpty();
    }
    
    @Override
    public void convert(){
        
        if(landscapeMaterial != null){
            landscapeMaterial.export(UTPackageExtractor.getExtractor(mapConverter, landscapeMaterial));
        }
        
        if(landscapeHoleMaterial != null){
            landscapeHoleMaterial.export(UTPackageExtractor.getExtractor(mapConverter, landscapeHoleMaterial));
        }
    }
    
    @Override
    public String toString(){
        
        sbf.append(IDT).append("Begin Actor Class=Landscape Name=").append(name).append("\n");
        
        for(LandscapeHeightfieldCollisionComponent collisionComp : collisionComponents){
            sbf.append(IDT).append("\tBegin Object Class=LandscapeHeightfieldCollisionComponent Name=\"").append(collisionComp.getName()).append("\"\n");
            sbf.append(IDT).append("\tEnd Object\n");
        }
        
        for(LandscapeComponent landscape : landscapeComponents){
            sbf.append(IDT).append("\tBegin Object Class=LandscapeComponent Name=\"").append(landscape.getName()).append("\"\n");
            sbf.append(IDT).append("\tEnd Object\n");
        }

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"RootComponent0\"\n");
        sbf.append(IDT).append("\tEnd Object\n");
        
        for(LandscapeHeightfieldCollisionComponent collisionComp : collisionComponents){
            collisionComp.toT3d(sbf);
        }
        
        for(LandscapeComponent landscape : landscapeComponents){
            landscape.toT3d(sbf);
        }
        
        sbf.append(IDT).append("\tBegin Object Name=\"RootComponent0\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");
        
        // needs a guid or else would crash on import
        // TODO guid generator
        sbf.append(IDT).append("\tLandscapeGuid=51DF72704471DE2EA0AA68AE47B62710\n");
        
        if(landscapeMaterial != null){
            sbf.append(IDT).append("\tLandscapeMaterial=Material'").append(landscapeMaterial.getConvertedName(mapConverter)).append("'\n");
        }
        
        int idx = 0;
        
        for(LandscapeComponent landscape : landscapeComponents){
            sbf.append(IDT).append("\tLandscapeComponents(").append(idx).append(")=LandscapeComponent'").append(landscape.getName()).append("'\n");
            idx ++;
        }
        
        idx = 0;
        
        for(LandscapeHeightfieldCollisionComponent collisionComp : collisionComponents){
            sbf.append(IDT).append("\tCollisionComponents(").append(idx).append(")=CollisionComponent'").append(collisionComp.getName()).append("'\n");
            idx ++;
        }
        
        sbf.append(IDT).append("\tComponentSizeQuads=").append(componentSizeQuads).append("\n");
        sbf.append(IDT).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
        sbf.append(IDT).append("\tNumSubsections=").append(numSubsections).append("\n");
        sbf.append(IDT).append("\tRootComponent=RootComponent0\n");
        
        writeEndActor();
        
        return sbf.toString();
    }
    
}
