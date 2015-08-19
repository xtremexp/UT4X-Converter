/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

/**
 * A mover is a brush that moves in level.
 * 
 * @author XtremeXp
 */
public class T3DMover extends T3DBrush {

	/**
	 * Common properties of basic mover
	 */
	MoverProperties moverProperties;

	/**
	 *
	 * @param mc
	 * @param t3dClass
	 */
	public T3DMover(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		moverProperties = new MoverProperties(this);
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (moverProperties.analyseT3DData(line)) {

		} else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	@Override
	public void scale(Double newScale) {

		moverProperties.scale(newScale);

		super.scale(newScale);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {

		if (mapConverter.getOutputGame() == UTGames.UTGame.UT4) {

			sbf.append(moverProperties.toString(sbf));

			// TODO for UT4 make converter from brush to .fbx Autodesk file and
			// transform into StaticMesh
			// TODO for UT3 make converter from brush to .ase file and transform
			// into StaticMesh

			// Write the mover as brush as well so we can convert it in
			// staticmesh in UE4 Editor ...
			String originalName = this.name;
			this.brushClass = BrushClass.Brush;
			this.name += "Brush";
			String x = super.toString();
			// put back original name (might be used later for linked actors .
			// e.g: liftexit)
			this.name = originalName;

			return x;
		}
		// TODO write mover UT UE<=3
		else {
			return super.toString();
		}
	}

	@Override
	public void convert() {

		if (mapConverter.convertSounds()) {
			moverProperties.convert();
		}

		super.convert();
	}

	public MoverProperties getMoverProperties() {
		return moverProperties;
	}

	
}
