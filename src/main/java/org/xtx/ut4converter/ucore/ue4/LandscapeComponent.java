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
public class LandscapeComponent extends TerrainComponent implements T3D {
    
    LandscapeCollisionComponent colisionComponent;
    

    int subsectionSizeQuads;
    
    Vector4d weightmapScaleBias;
    
    float weightmapSubsectionOffset;
    
    Vector4d heightmapScaleBias;
    
    short collisionMipLevel;
    
    short numSubsections;
    
    Vector3d relativeLocation;

    /**
     * Hexadecimal values.
     * Much more accurate than collision height map
     * 32768 -> 80800080x = 2155872384 = 256 + (32768 * 65536)
     */
    


    public LandscapeComponent(int numComponent, int sizeQuads) {
        super(numComponent, sizeQuads);
        initialise();
    }
    
    public LandscapeComponent(LandscapeCollisionComponent colComp, boolean isNotUe4Scale) {
    	
    	super(colComp.numComponent, colComp.getSizeQuads());
    	
    	this.subsectionSizeQuads = colComp.getSizeQuads();
    	this.heightData = new int[colComp.getHeightData().length][colComp.getHeightData()[0].length];
    	
    	for(int x = 0; x < heightData.length; x ++ ){
    		
    		for(int y = 0; y < heightData[0].length; y ++){
    			this.heightData[x][y] = new Integer(colComp.getHeightData()[x][y]);
    		}
    	}
    	
        //this.heightData = colComp.getHeightData();
        this.sectionBaseX = colComp.getSectionBaseX();
        this.sectionBaseY = colComp.getSectionBaseY();

        initialise();
        
        if(isNotUe4Scale){
        	convertUe2ToUe4HeightMap();
        }
    }
    
    private void initialise(){
        numSubsections = 1;
        weightmapSubsectionOffset = 1;
    }

    public void setColisionComponent(LandscapeCollisionComponent colisionComponent) {
        this.colisionComponent = colisionComponent;
    }
    


    public void setSubsectionSizeQuads(int subsectionSizeQuads) {
        this.subsectionSizeQuads = subsectionSizeQuads;
    }

    public void setWeightmapScaleBias(Vector4d weightmapScaleBias) {
        this.weightmapScaleBias = weightmapScaleBias;
    }

    public void setWeightmapSubsectionOffset(float weightmapSubsectionOffset) {
        this.weightmapSubsectionOffset = weightmapSubsectionOffset;
    }

    public void setHeightmapScaleBias(Vector4d heightmapScaleBias) {
        this.heightmapScaleBias = heightmapScaleBias;
    }

    public void setCollisionMipLevel(short collisionMipLevel) {
        this.collisionMipLevel = collisionMipLevel;
    }

    public void setRelativeLocation(Vector3d relativeLocation) {
        this.relativeLocation = relativeLocation;
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
        
        String base = "\t\t";
        
        sb.append(base).append("Begin Object Name=\"").append(getName()).append("\"\n");
        
        if(sectionBaseX > 0){
        	sb.append(base).append("\t\tSectionBaseX=").append(sectionBaseX).append("\n");
        }
        
        if(sectionBaseY > 0){
        	sb.append(base).append("\t\tSectionBaseY=").append(sectionBaseY).append("\n");
        }
        
        sb.append(base).append("\tComponentSizeQuads=").append(sizeQuads).append("\n");
        sb.append(base).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
        sb.append(base).append("\tNumSubsections=").append(numSubsections).append("\n");
        
        sb.append(base).append("\tCollisionComponent=LandscapeHeightfieldCollisionComponent'").append(colisionComponent.getName()).append("'\n");
        sb.append(base).append("\tAttachParent=RootComponent0\n");
        
        if(getSectionBaseX() > 0 || getSectionBaseY() > 0){
        	sb.append(base).append("\t").append(getT3dRelativeLocation()).append("\n");
        }
        
        sb.append(base).append("\tCustomProperties LandscapeHeightData");
        

        for(int x = 0; x < heightData.length; x ++){
            
        	for(int y = 0; y < heightData[0].length; y ++){
        		sb.append(" "+ Integer.toHexString(heightData[x][y]));
        	}
        }
        
        
        sb.append(" LayerNum=0\n");
        sb.append(base).append("End Object\n");
        
        return sb.toString();
    }

    @Override
    public String getName() {
        return "LandscapeComponent_" + numComponent;
    }
    
    public static int convertToUe4Height(int height){
    	return height * 256;
    }
    
    /**
     * Convert heightmap ue2 data to ue4
     */
    public void convertUe2ToUe4HeightMap(){
        
        //for(Integer height : landscapeHeightData){
        for(int x = 0; x < heightData.length; x ++){
            
        	for(int y = 0; y < heightData[0].length ; y++){
        		heightData[x][y] = convertToUe4Height(heightData[x][y]);
        	}
        }
        
    }
    
}
