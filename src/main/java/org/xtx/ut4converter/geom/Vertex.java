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
	private Vector3d coordinates;

	/**
	 * Texture U
	 */
	private float u;

	/**
	 * Texture v
	 */
	private float v;

	/**
	 * 
	 * @param coordinates
	 *            Vector coordinates
	 */
	public Vertex(Vector3d coordinates) {
		this.coordinates = coordinates;
	}


	/**
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @param u U tex align
	 * @param v V tex align
	 */
	public Vertex(Double x, Double y, Double z, float u, float v) {
		this.coordinates = new Vector3d(x, y, z);
		this.u = u;
		this.v = v;
	}

	public Double getX() {
		return coordinates.x;
	}

	public Double getY() {
		return coordinates.y;
	}

	public Double getZ() {
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


	/**
	 * Scales this vertex
	 * 
	 * @param newScale
	 *            Scale factor
	 */
	public void scale(Double newScale) {

		coordinates.scale(newScale);
		u /= newScale;
		v /= newScale;
	}

}
