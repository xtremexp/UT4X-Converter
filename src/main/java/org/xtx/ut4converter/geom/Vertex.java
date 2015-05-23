/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.geom;

import javax.vecmath.Vector3d;
import org.xtx.ut4converter.t3d.T3DPolygon;

/**
 *
 * @author XtremeXp
 */
public class Vertex {
    
    /**
     * 
     */
    Vector3d coordinates;
    
    /**
     * Texture U
     */
    float u;
    
    /**
     * Texture v
     */
    float v;
    
    
    /**
     * Vertex index in polygon this point belongs to
     */
    Integer vertexPolyIdx;

    
    
    /**
     * Reference to the polygon this vertex belongs too
     * Can be null
     */
    T3DPolygon polygon;


    
    /**
     * 
     * @param coordinates Vector coordinates
     * @param polygon Polygon this vertex belongs too
     */
    public Vertex(Vector3d coordinates, T3DPolygon polygon) {
        
        this.coordinates = coordinates;
        this.polygon = polygon;
        
        calcPolyIdx();
    }
    
    /**
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param polygon
     */
    public Vertex(Double x, Double y, Double z, T3DPolygon polygon) {
        this.coordinates = new Vector3d(x, y, z);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param z
     * @param u
     * @param v 
     * @param polygon 
     */
    public Vertex(Double x, Double y, Double z, float u, float v, T3DPolygon polygon) {
        this.coordinates = new Vector3d(x, y, z);
        this.u = u;
        this.v = v;
    }
    
    
    
    private void calcPolyIdx(){
        
        if(vertexPolyIdx != null){
            return;
        }
        
        // TODO
    }

    public void setPolygon(T3DPolygon polygon) {
        this.polygon = polygon;
        calcPolyIdx();
    }

    public Double getX(){
        return coordinates.x;
    }
    
    public Double getY(){
        return coordinates.y;
    }
    
    public Double getZ(){
        return coordinates.z;
    }

    public Vector3d getCoordinates() {
        return coordinates;
    }

    
    
    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public Integer getVertexPolyIdx() {
        return vertexPolyIdx;
    }

    public void setVertexPolyIdx(Integer vertexPolyIdx) {
        this.vertexPolyIdx = vertexPolyIdx;
    }

    
    
    /**
     * Scales this vertex
     * @param newScale Scale factor
     */
    public void scale(Double newScale){
        
        coordinates.scale(newScale);
        u /= newScale;
        v /= newScale;
    }
    
    
    
}
