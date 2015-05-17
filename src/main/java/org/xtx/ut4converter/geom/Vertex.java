/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.geom;

import javax.vecmath.Vector3d;

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
    Integer polyIdx;
    
    /**
     * Vertex index in brush
     */
    Integer brushIdx;

    public Vertex(Vector3d coordinates) {
        this.coordinates = coordinates;
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

    public Integer getBrushIdx() {
        return brushIdx;
    }

    public void setIdx(Integer idx) {
        this.brushIdx = idx;
    }
    
    
    
    
}
