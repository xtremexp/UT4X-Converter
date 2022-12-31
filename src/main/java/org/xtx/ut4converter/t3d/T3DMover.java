/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.ucore.ue1.BrushPolyflag;

/**
 * A mover is a brush that moves in level.
 *
 * @author XtremeXp
 */
public class T3DMover extends T3DBrush {

	/**
	 * Common properties of basic mover
	 */
	private final MoverProperties moverProperties;

	/**
	 *
	 * @param mc Map converter instance
	 * @param t3dClass t3dClass
	 */
	public T3DMover(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		moverProperties = new MoverProperties(this, mc);
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (!moverProperties.analyseT3DData(line)) {
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
	 * @return String value
	 */
	public String toT3d() {

		// no generic mover actor for UE4 but UT4 with Lift blueprint
		// UE3 movers are InterpActors
		if (mapConverter.getOutputGame() == UTGames.UTGame.UT4 || mapConverter.isTo(UTGames.UnrealEngine.UE3)) {

			if (mapConverter.getOutputGame() == UTGames.UTGame.UT4) {
				moverProperties.writeUT4MoverActor(sbf);
			}
			// UE3
			else {
				moverProperties.writeUE3MoverActor(sbf);
			}

			// TODO for UT4 make converter from brush to .fbx Autodesk file and
			// transform into StaticMesh
			// TODO for UT3 make converter from brush to .ase file and transform
			// into StaticMesh

			// Write the mover as brush as well so we can convert it in
			// staticmesh in UE4 Editor ...
			String originalName = this.name;
			this.brushClass = BrushClass.Brush;
			this.name += "_Brush";

			// force mover brush to be non-solid so won't cause possible bsp holes around
			// does not need to be solid since it's going to be transformed to staticmesh anyway in UE4 editor
			getPolyflags().clear();
			getPolyflags().add(BrushPolyflag.NON_SOLID);

			// TODO refactor, the way it's been coded is really messy/confusing
			super.toT3d();
			// put back original name (might be used later for linked actors .
			// e.g: liftexit)
			this.name = originalName;

			return sbf.toString();
		}
		else {
			return super.toString();
		}
	}

	@Override
	public void convert() {

		moverProperties.convert();

		super.convert();
	}


}
