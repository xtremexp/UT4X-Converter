/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Vector3d;
import org.xtx.ut4converter.t3d.T3DPolygon;

/**
 *
 * @author XtremeXp
 */
public class FBXModelObject extends FBXObject {

    final short MODEL_VERSION = 232;
    List<Double> vertices;
    
    public FBXModelObject(LinkedList<T3DPolygon> polygons) {
        super(FBXObjectType.Model);
        version = MODEL_VERSION;
    }

    private void load(LinkedList<T3DPolygon> polygons){
        
        for(T3DPolygon p : polygons){
            for(Vector3d v : p.vertices){
                vertices.add(v.x);
                vertices.add(v.y);
                vertices.add(v.z);
            }
        }
    }

    @Override
    public void writeFBX(StringBuilder sb) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
    
}
