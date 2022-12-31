/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.export.UTPackageExtractor;

/**
 * StaticMesh movers. Mover class for Unreal Engine >= 2 Unreal Engine 1 is
 * using mover brush (see T3DMover class)
 * 
 * @author XtremeXp
 */
public class T3DMoverSM extends T3DStaticMesh {

	private final MoverProperties moverProperties;

	public T3DMoverSM(MapConverter mc, String t3dClass) {
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

		super.scale(newScale);
	}

	/**
	 *
	 * @return T3d
	 */
	public String toT3d() {

		if (mapConverter.isTo(UTGames.UnrealEngine.UE3)) {
			moverProperties.writeUE3MoverActor(sbf);
		} else if (mapConverter.getOutputGame() == UTGames.UTGame.UT4) {
			moverProperties.writeUT4MoverActor(sbf);
		}

		return sbf.toString();
	}

	@Override
	public void convert() {

		if (mapConverter.convertSounds()) {
			moverProperties.convert();
		}

		if (getStaticMesh() != null && mapConverter.convertStaticMeshes()) {
			getStaticMesh().export(UTPackageExtractor.getExtractor(mapConverter, getStaticMesh()));
		}
		super.convert();
	}

}
