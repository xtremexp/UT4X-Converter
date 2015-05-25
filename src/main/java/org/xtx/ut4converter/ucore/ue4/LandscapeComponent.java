/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class LandscapeComponent implements T3D {
    
    LandscapeHeightfieldCollisionComponent colisionComponent;
    
    short sectionBaseX;
    
    short sectionBaseY;
    
    int componentSizeQuads;
    
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
    List<Integer> landscapeHeightData;
    
    int num;
    
    public LandscapeComponent(int numComponent) {
        this.num = numComponent;
        initialise();
    }
    
    private void initialise(){
        numSubsections = 1;
        weightmapSubsectionOffset = 1;
        landscapeHeightData = new ArrayList<>();
    }

    public void setColisionComponent(LandscapeHeightfieldCollisionComponent colisionComponent) {
        this.colisionComponent = colisionComponent;
    }
    
    public void setSectionBaseX(short sectionBaseX) {
        this.sectionBaseX = sectionBaseX;
    }

    public void setSectionBaseY(short sectionBaseY) {
        this.sectionBaseY = sectionBaseY;
    }

    public void setComponentSizeQuads(int componentSizeQuads) {
        this.componentSizeQuads = componentSizeQuads;
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

    public void setLandscapeHeightData(List<Integer> landscapeHeightData) {
        this.landscapeHeightData = landscapeHeightData;
    }

    public List<Integer> getLandscapeHeightData() {
        return landscapeHeightData;
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
        sb.append(base).append("\tComponentSizeQuads=").append(componentSizeQuads).append("\n");
        sb.append(base).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
        sb.append(base).append("\tNumSubsections=").append(numSubsections).append("\n");
        
        sb.append(base).append("\tCollisionComponent=LandscapeHeightfieldCollisionComponent'").append(colisionComponent.getName()).append("'\n");
        sb.append(base).append("\tAttachParent=RootComponent0\n");
        sb.append(base).append("\tCustomProperties LandscapeHeightData");
        
        int idx = 0;
        
        for(int height : landscapeHeightData){
            
            if(idx > 0 && idx % 16 == 0){
                sb.append("\n\t\t\t ");
            } else {
                sb.append(" ");
            }
            
            sb.append(Integer.toHexString(height));
            
            idx ++;
        }
        
        sb.append(" NumLayer=0\n");
        sb.append("\tEnd Object\n");
        
        return sb.toString();
    }

    @Override
    public String getName() {
        return "LandscapeComponent_" + num;
    }
    
    /**
     * Convert heightmap ue2 data to ue4
     */
    public void convertUe2ToUe4HeightMap(){
        
        //for(Integer height : landscapeHeightData){
        for(int idx = 0; idx < landscapeHeightData.size(); idx ++){
            
            int newHeight = 256 + (landscapeHeightData.get(idx) * 65536);
            landscapeHeightData.set(idx, newHeight);
        }
        
    }
    
}
