/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue3;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;

/**
 *
 * @author XtremeXp
 */
public class TerrainLayer extends T3DObject {

	public TerrainLayer(MapConverter mc) {
		super(mc);
		// TODO Auto-generated constructor stub
	}

	String name;

	/**
	 * Impossible to get more data about TLS with t3d file
	 */
	String terrainLayerSetup;
	boolean highlighted;
	boolean wireframeHighlighted;

}
