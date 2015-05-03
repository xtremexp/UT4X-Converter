/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.vecmath.Vector3d;

/**
 * 
 * @author XtremeXp
 */
public class T3DPolygon {
    
    /**
     * Original texture or material applied to the polygon
     */
    String texture;
    
    /**
     *
     */
    public Vector3d origin;

    /**
     *
     */
    public Vector3d normal;
    
    /**
     * How points are regrouped for polygon?
     */
    Integer link;
    
    
    /**
     * Some flag about how the polygon should be rendered
     * such as istranslucent, ismirror, is two-sided and so on.
     * This value is a sum of values that are powers of two
     * Flags=217890775
     */
    Integer flag;
    
    /**
     * LightMapScale=64.000000
     * UE1: None
     * UE2: ?
     * UE3: ?
     * UE4: Light Map resolution.
     */
    Double lightMapScale;
    public double pan_u, 

    /**
     *
     */
    pan_v;
    public Vector3d texture_u, 

    /**
     *
     */
    texture_v;

    /**
     *
     */
    public LinkedList<Vector3d> vertices = new LinkedList<>();
    
    /**
     *
     */
    public T3DPolygon(){
        origin = new Vector3d(0d, 0d, 0d);
        normal = new Vector3d(0d, 0d, 0d);
    }
    
    /**
     *
     * @param t3dLine
     */
    public T3DPolygon(String t3dLine){
        // Begin Polygon Texture=Rockwal4 Flags=32768 Link=322
        this.texture = T3DUtils.getString(t3dLine, "Texture");
        this.link = T3DUtils.getInteger(t3dLine, "Link");
        this.flag = T3DUtils.getInteger(t3dLine, "Flags");
    }
    
    /**
     *
     * @param newScale
     */
    public void scale(Double newScale){
        if(newScale != null){
            // TODO
        }
    }
    
    /**
     *
     * @param mainScale
     * @param postScale
     */
    public void transformPermanently(Vector3d mainScale, Vector3d postScale){
        
    }
    
    
    /**
     * 
     * @param sb
     * @param df Default Decimal format (not creating one each time for perf issues)
     * @param prefix 
     * @param numPoly 
     * @param reverseVertexOrder
     */
    public void toT3D(StringBuilder sb, DecimalFormat df, String prefix, int numPoly, boolean reverseVertexOrder){
        
        prefix += "\t\t\t";
        
        sb.append(prefix).append("Begin Polygon Item=Side");
        
        if(texture != null){
            sb.append(" Texture=").append(texture);
        }
        
        if(lightMapScale != null){
            sb.append(" LightMapScale=").append(lightMapScale);
        }
        
        sb.append(" Link=").append(numPoly);
        
        if(lightMapScale != null){
            sb.append(" LightMapScale=").append(lightMapScale);
        }
        
        sb.append("\n");
        
        sb.append(prefix).append("\tOrigin   ").append(T3DUtils.toPolyStringVector3d(origin, df)).append("\n");
        sb.append(prefix).append("\tNormal   ").append(T3DUtils.toPolyStringVector3d(normal, df)).append("\n");
        
        if(pan_u > 0d || pan_v > 0d){
            sb.append(prefix).append("\tPan      ");
            
            if(pan_u > 0d){
                sb.append("U=").append(pan_u);
            }
            
            if(pan_v > 0d){
                sb.append(" V=").append(pan_v);
            }
            
            sb.append("\n");
        } 
        
        sb.append(prefix).append("\tTextureU ").append(T3DUtils.toPolyStringVector3d(texture_u, df)).append("\n");
        sb.append(prefix).append("\tTextureV ").append(T3DUtils.toPolyStringVector3d(texture_v, df)).append("\n");
        
        
        if(!reverseVertexOrder){
            for(Vector3d vertex : vertices){
                sb.append(prefix).append("\tVertex   ").append(T3DUtils.toPolyStringVector3d(vertex, df)).append("\n");
            }
        } else {
            for(int i=(vertices.size()-1);i>=0;i--){
                sb.append(prefix).append("\tVertex   ").append(T3DUtils.toPolyStringVector3d(vertices.get(i), df)).append("\n");
            }
        }
        
        sb.append(prefix).append("End Polygon\n");
        
    }
    
    
    public void addVertex(Vector3d vertex){
        vertices.add(vertex);
    }
    
    public T3DPolygon addVertex(Double x, Double y, Double z){
        vertices.add(new Vector3d(x, y, z));
        return this;
    }
    
    public void setNormal(Vector3d v){
        normal = v;
    } 
    
    public void setNormal(Double x, Double y, Double z){
        normal = new Vector3d(x, y, z);
    } 
    
    public void setTexU(Double x, Double y, Double z){
        texture_u = new Vector3d(x, y, z);
    } 
    
    public void setTexV(Double x, Double y, Double z){
        texture_v = new Vector3d(x, y, z);
    }

    public void setOrigin(Vector3d origin) {
        this.origin = origin;
    }
    
    
}
