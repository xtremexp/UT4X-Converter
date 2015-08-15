/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import java.util.List;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.ue3.TerrainDecoLayer;
import org.xtx.ut4converter.ucore.ue3.TerrainLayer;

/**
 *
 * @author XtremeXp
 */
public class T3DUE3Terrain extends T3DActor {

	boolean bMorphingEnabled;
	boolean bMorphingGradientEnabled;
	boolean bShowWireFrame;

	short maxComponentSize;
	short maxTesselationLevel;
	short minTesselationLevel;
	short normalMapLayer;

	short numPatchesX;
	short numPatchesY;

	float tesselationDistanceScale;
	float tesselationCheckDistance;

	List<TerrainDecoLayer> decoLayers;
	List<TerrainLayer> layers;

	TerrainHeight terrainHeight;

	class TerrainHeight {
		int count;
		short width;
		short height;

		List<Integer> heightMap;
	}

	public T3DUE3Terrain(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

}
