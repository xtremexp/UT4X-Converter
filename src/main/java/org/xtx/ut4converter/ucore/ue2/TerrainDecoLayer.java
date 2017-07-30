/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue2;

import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 *
 * @author XtremeXp
 */
public class TerrainDecoLayer implements T3D {

	enum DrawOrder {
		SORT_NoSort, SORT_BackToFront, SORT_FrontToBack
	}

	enum DetailMode {
		DM_Low, DM_High, DM_SuperHigh
	}

	boolean alignToTerrain;

	UPackageRessource colorMap;
	UPackageRessource densityMap;

	double densityMultiplierMin, densityMultiplierMax;

	boolean disregardTerrainLightning;

	double fadeOutRadiusMin, fadeOutRadiusMax;
	int litDirectional;
	int maxPerQuad;
	int randomYaw;

	/**
	 * ?
	 */
	UPackageRessource scaleMap;

	double scaleMultiplierMinX, scaleMultiplierMaxX;
	double scaleMultiplierMinY, scaleMultiplierMaxY;
	double scaleMultiplierMinZ, scaleMultiplierMaxZ;

	int seed;
	boolean showOnInvisibleTerrain;
	boolean showOnTerrain;

	UPackageRessource staticMesh;

	@Override
	public void convert() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void scale(Double newScale) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO handle decolayer
		return false;
		//throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public TerrainDecoLayer() {

	}

	public TerrainDecoLayer(UPackageRessource densityMap, UPackageRessource staticMesh) {
		this.densityMap = densityMap;
		this.staticMesh = staticMesh;
	}

	public void load() {
		// TODO
	}

}
