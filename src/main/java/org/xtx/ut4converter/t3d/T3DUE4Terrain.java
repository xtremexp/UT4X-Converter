/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
    
    /**
     * Max component size
     */
    final int maxComponentSize = 255;
    
    int componentSizeQuads;
    int subsectionSizeQuads;
    short numSubsections;
    boolean bUsedForNavigation;
    short maxPaintedLayersPerComponent;
    
    LandscapeHeightfieldCollisionComponent collisionComponents[][];
    
    LandscapeComponent landscapeComponents[][];
    
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
        this.scale3d = ue2Terrain.terrainScale;
        
        if(!ue2Terrain.layers.isEmpty()){
            landscapeMaterial = ue2Terrain.layers.get(0).getTexture();
        }
        
        int numComponent = 0;
        
        this.componentSizeQuads = Math.min(Math.min(ue2Terrain.heightMapTextureDimensions.width, ue2Terrain.heightMapTextureDimensions.height - 1), maxComponentSize);
        this.subsectionSizeQuads = this.componentSizeQuads;
        
        int nbCompX = ue2Terrain.heightMapTextureDimensions.width / (componentSizeQuads + 1);
        int nbCompY = ue2Terrain.heightMapTextureDimensions.height / (componentSizeQuads + 1);
        
        LandscapeHeightfieldCollisionComponent collisionComponent = null;
        collisionComponents = new LandscapeHeightfieldCollisionComponent [nbCompX][nbCompY];
        landscapeComponents = new LandscapeComponent [nbCompX][nbCompY];
        
        int localHeightCollisionData[][];
        
        
        // Local HeightMap idx in component
        int localHmXIdx = 0;
        int localHmYIdx = 0;
        
        // Index of component
        int compIdxX = 0;
        int compIdxY = 0;
        
        int heightMap[][] = new int[ue2Terrain.getHeightMap().length][ue2Terrain.getHeightMap()[0].length];
        
        // flip x/y
        // all these loops quite crappy but don't want rework the big loop yet
        for(int hmXIdx = 0; hmXIdx < ue2Terrain.getHeightMap().length; hmXIdx ++){
        	
        	for(int hmYIdx = 0; hmYIdx < ue2Terrain.getHeightMap()[0].length; hmYIdx ++){
        		heightMap[hmXIdx][hmYIdx] = ue2Terrain.getHeightMap()[hmYIdx][hmXIdx];
        	}
        }
        
        ue2Terrain.setHeightMap(heightMap);

        for(int hmXIdx = 0; hmXIdx < ue2Terrain.getHeightMap().length; hmXIdx ++){
        	
        	compIdxY = 0;
        	
        	for(int hmYIdx = 0; hmYIdx < ue2Terrain.getHeightMap()[0].length; hmYIdx ++){
        		

        		if(hmXIdx % componentSizeQuads == 0 && hmYIdx % componentSizeQuads == 0 
        				&& hmXIdx < componentSizeQuads 
        					&& hmYIdx < componentSizeQuads){
        			
        			localHmXIdx = 0;
        			localHmYIdx = 0;	
        			
        			collisionComponent = new LandscapeHeightfieldCollisionComponent(numComponent, componentSizeQuads);
        			localHeightCollisionData = new int[componentSizeQuads + 1][componentSizeQuads + 1];
        			
        			collisionComponent.setSectionBaseX(compIdxX);
        			collisionComponent.setSectionBaseY(compIdxY);
        			
        			collisionComponent.setHeightData(localHeightCollisionData);
        			collisionComponents[compIdxX][compIdxY] = collisionComponent;
        		} 
        		
        		collisionComponent = collisionComponents[compIdxX][compIdxY];
        		
        		int heightMapVal = 0;
        		
        		if(hmXIdx % (componentSizeQuads + 1) == 0 && hmXIdx > 0){
        			heightMapVal = ue2Terrain.getHeightMap()[hmXIdx - 1][hmYIdx];
        		}
        		else if(hmYIdx % (componentSizeQuads + 1) == 0 && hmYIdx > 0){
        			heightMapVal = ue2Terrain.getHeightMap()[hmXIdx][hmYIdx - 1];
        		}
        		else {
        			heightMapVal = ue2Terrain.getHeightMap()[hmXIdx][hmYIdx];
        		}
        			
        		collisionComponent.getHeightData()[localHmXIdx][localHmYIdx] = heightMapVal / 2;
        		
        		
        		if(hmYIdx % componentSizeQuads == 0){
        			if(hmYIdx > 0){
        				compIdxY ++;
        			}
        			localHmYIdx = 0;
        		}
        		
    			localHmYIdx ++;
        		
        	}
        	
        	if(hmXIdx % componentSizeQuads == 0){
        		if(hmXIdx > 0){
        			compIdxX ++;
        		}
    			localHmXIdx = 0;
    		}
        	
        	localHmXIdx ++;
        }

        // create default landscape components from heightmap components
        for(int x = 0; x < collisionComponents.length; x ++){
        	
        	for(int y = 0; y < collisionComponents[0].length ; y ++){
        		LandscapeHeightfieldCollisionComponent colComponent = collisionComponents[x][y];
        		landscapeComponents[x][y] = new LandscapeComponent(colComponent, true);
        		
        		colComponent.setRenderComponent(landscapeComponents[x][y]);
        		landscapeComponents[x][y].setColisionComponent(colComponent);
        	}
        }
                
        // In Unreal Engine 2, terrain pivot is "centered"
        // unlike UE3/4, so need update location
        if(this.location != null && this.scale3d != null){
        	
        	double offsetX = (nbCompX * this.scale3d.x * this.componentSizeQuads) / 2;
        	double offsetY = (nbCompY * this.scale3d.y * this.componentSizeQuads) / 2;
        	
        	if(mapConverter.getScale() != null){
        		offsetX *= mapConverter.getScale();
        		offsetY *= mapConverter.getScale();
        	}
        	
        	this.location.x -= (offsetX + 100);
        	this.location.y -= (offsetY + 100);
        }
    }
    

    private void initialise(){
        collisionThickness = 16;
        numSubsections = 1;
        bUsedForNavigation = true;
    }
    
    
    @Override
    public boolean isValidWriting(){
        
        return landscapeComponents.length > 0;
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
        
        for(int x = 0; x < collisionComponents.length; x ++){
        	for(int y = 0; y < collisionComponents[0].length ; y ++){
	            sbf.append(IDT).append("\tBegin Object Class=LandscapeHeightfieldCollisionComponent Name=\"").append(collisionComponents[x][y].getName()).append("\"\n");
	            sbf.append(IDT).append("\tEnd Object\n");
        	}
        }
        
        for(int x = 0; x < landscapeComponents.length; x ++){
        	for(int y = 0; y < landscapeComponents[0].length ; y ++){
	            sbf.append(IDT).append("\tBegin Object Class=LandscapeComponent Name=\"").append(landscapeComponents[x][y].getName()).append("\"\n");
	            sbf.append(IDT).append("\tEnd Object\n");
        	}
        }

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"RootComponent0\"\n");
        sbf.append(IDT).append("\tEnd Object\n");
        
        for(int x = 0; x < collisionComponents.length; x ++){
        	for(int y = 0; y < collisionComponents[0].length ; y ++){
        		collisionComponents[x][y].toT3d(sbf);
        	}
        }
        
        for(int x = 0; x < landscapeComponents.length; x ++){
        	for(int y = 0; y < landscapeComponents[0].length ; y ++){
        		landscapeComponents[x][y].toT3d(sbf);
        	}
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
        
        for(int x = 0; x < landscapeComponents.length; x ++){
        	for(int y = 0; y < landscapeComponents[0].length ; y ++){
	            sbf.append(IDT).append("\tLandscapeComponents(").append(idx).append(")=LandscapeComponent'").append(landscapeComponents[x][y].getName()).append("'\n");
	            idx ++;
        	}
        }
        
        idx = 0;
        
        for(int x = 0; x < collisionComponents.length; x ++){
        	for(int y = 0; y < collisionComponents[0].length ; y ++){
	            sbf.append(IDT).append("\tCollisionComponents(").append(idx).append(")=CollisionComponent'").append(collisionComponents[x][y].getName()).append("'\n");
	            idx ++;
        	}
        }
        
        sbf.append(IDT).append("\tComponentSizeQuads=").append(componentSizeQuads).append("\n");
        sbf.append(IDT).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
        sbf.append(IDT).append("\tNumSubsections=").append(numSubsections).append("\n");
        sbf.append(IDT).append("\tRootComponent=RootComponent0\n");
        
        writeEndActor();
        
        return sbf.toString();
    }
    
}
