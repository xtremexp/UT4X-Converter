/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import java.util.List;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class LandscapeHeightfieldCollisionComponent implements T3D {
    
    final String BASE_NAME = "LandscapeHeightfieldCollisionComponent";
    
    LandscapeComponent renderComponent;
    
    short sectionBaseX;
    short sectionBaseY;
    
    int collisionSizeQuads;
    float collisionScale;
    
    List<Integer> collisionHeightData;
    Vector3d relativeLocation;
    
    int numComponent;
    
    public LandscapeHeightfieldCollisionComponent(int num){
        numComponent = 0;
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

    public void setSectionBaseX(short sectionBaseX) {
        this.sectionBaseX = sectionBaseX;
    }

    public void setSectionBaseY(short sectionBaseY) {
        this.sectionBaseY = sectionBaseY;
    }

    public void setCollisionSizeQuads(int collisionSizeQuads) {
        this.collisionSizeQuads = collisionSizeQuads;
    }

    public void setCollisionScale(float collisionScale) {
        this.collisionScale = collisionScale;
    }

    public void setCollisionHeightData(List<Integer> collisionHeightData) {
        this.collisionHeightData = collisionHeightData;
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

    public int getNumComponent() {
        return numComponent;
    }

    public void setNumComponent(int numComponent) {
        this.numComponent = numComponent;
    }
    
    

    @Override
    public String toT3d(StringBuilder sb) {
        
        sb.append("\tBegin Object Name=\"").append(getName()).append("\"\n");
        sb.append("\t\tCollisionSizeQuads=").append(collisionSizeQuads).append("\n");
        sb.append("\t\tAttachParent=RootComponent0\n");
        sb.append("\t\tCustomProperties CollisionHeightData");
        
        for(int height : collisionHeightData){
            sb.append("  ").append(height);
        }
        
        sb.append("\n\tEnd Object\n");
        
        return sb.toString();
    }

    @Override
    public String getName() {
        return BASE_NAME + "_" + numComponent;
    }
    
    
    
}
