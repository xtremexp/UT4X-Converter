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

	private int index;

	private int alphaMapIndex;


	/**
	 * Impossible to get more data about TLS with t3d file
	 */
	private String terrainLayerSetupName;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getAlphaMapIndex() {
		return alphaMapIndex;
	}

	public void setAlphaMapIndex(int alphaMapIndex) {
		this.alphaMapIndex = alphaMapIndex;
	}

	public String getTerrainLayerSetupName() {
		return terrainLayerSetupName;
	}

	public void setTerrainLayerSetupName(String terrainLayerSetupName) {
		this.terrainLayerSetupName = terrainLayerSetupName;
	}
}
