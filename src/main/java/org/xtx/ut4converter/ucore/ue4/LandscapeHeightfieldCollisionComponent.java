/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class LandscapeHeightfieldCollisionComponent extends TerrainComponent implements T3D {
    
    final String BASE_NAME = "LandscapeHeightfieldCollisionComponent";
    
    LandscapeComponent renderComponent;
    

    int sectionBaseX;
    int sectionBaseY;
    
    int collisionSizeQuads;
    float collisionScale;
    
    Vector3d relativeLocation;
    
    
    public LandscapeHeightfieldCollisionComponent(int num){
        this.numComponent = 0;
        collisionScale = 1f;
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


    public void setCollisionSizeQuads(int collisionSizeQuads) {
        this.collisionSizeQuads = collisionSizeQuads;
    }

    public void setCollisionScale(float collisionScale) {
        this.collisionScale = collisionScale;
    }

	public void setRelativeLocation(Vector3d relativeLocation) {
        this.relativeLocation = relativeLocation;
    }

    public LandscapeComponent getRenderComponent() {
        return renderComponent;
    }

    public void setRenderComponent(LandscapeComponent renderComponent) {
        this.renderComponent = renderComponent;
    }



    @Override
    public String toT3d(StringBuilder sb) {
        
        sb.append("\tBegin Object Name=\"").append(getName()).append("\"\n");
        
        if(sectionBaseX > 0){
        	sb.append("\t\tSectionBaseX=").append(sectionBaseX).append("\n");
        }
        
        if(sectionBaseY > 0){
        	sb.append("\t\tSectionBaseY=").append(sectionBaseY).append("\n");
        }
        
        sb.append("\t\tCollisionSizeQuads=").append(collisionSizeQuads).append("\n");
        sb.append("\t\tCollisionScale=").append(collisionScale).append("\n");
        
        sb.append("\t\tRenderComponent=LandscapeComponent'").append(renderComponent.getName()).append("'\n");
        sb.append("\t\tAttachParent=RootComponent0\n");
        sb.append("\t\tCustomProperties CollisionHeightData");
        
        for(int x = 0; x < heightData.length; x ++){
            
        	for(int y = 0; y < heightData[0].length; y ++){
        		sb.append(" "+ heightData[x][y]);
        	}
        }
        
        sb.append("\n\tEnd Object\n");
        
        return sb.toString();
    }

    @Override
    public String getName() {
        return BASE_NAME + "_" + numComponent;
    }
    
    
    
}
