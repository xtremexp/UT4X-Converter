/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;
import org.xtx.ut4converter.ucore.UnrealEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Unreal Engine 1 only
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
	 * Staticmesh reference when this brush has been converted to staticmesh
	 * E.G: '/Game/Converted/Passage/StaticMeshes/Mover48.Mover48'
	 */
	private String staticMeshReference;

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
	public void scale(double newScale) {

		moverProperties.scale(newScale);

		super.scale(newScale);
	}

	/**
	 * Convert to t3d this mover
	 * @return String value
	 */
	public String toT3d() {

		// UT4 - Convert to Lift actor
		if (mapConverter.getOutputGame().getShortName().equals(UTGames.UTGame.UT4.shortName)) {
			moverProperties.writeUT4MoverActor(sbf);
		}
		// UE4/UE5 - TODO Convert to generic StaticMesh actor (No UT4 Lift actor)
		//else if (mapConverter.isTo(UnrealEngine.UE4, UnrealEngine.UE5)) {
		//}
		// UE3 - Convert to InterpActor
		else if (mapConverter.isTo(UnrealEngine.UE3)) {
			moverProperties.writeUE3MoverActor(sbf);
		}

		return sbf.toString();
	}

	@Override
	public void convert() {

		moverProperties.convert();

		// transform permanently, update origin with panU/V
		super.convert();


		// TODO for UE3 make brush to .t3d or .ase conversion (.obj not supported)
		// convert brush to .obj staticmesh
		if (mapConverter.isTo(UnrealEngine.UE4, UnrealEngine.UE5)) {
			try {
				Files.createDirectories(Paths.get(this.mapConverter.getOutPath() + "/StaticMesh/"));
				// super.convert changes name to "name_tag->event" which is incompatible with a filename
				// so need to use the original name
				String baseName = this.originalName;

				// e.g: '/Game/Converted/Unreal1/Movers/Passage/Mover0.Mover0'
				// TODO create intermediate folders and copy .uasset file for folder visibility in UE4
				this.staticMeshReference = mapConverter.getUt4ReferenceBaseFolder() + "/Movers/" + mapConverter.getOutMapName() + "/" + baseName + "." + baseName;

				File mtlFile = new File(this.mapConverter.getOutPath() + "/Movers/" + baseName + ".mtl");
				File objFile = new File(this.mapConverter.getOutPath() + "/Movers/" + baseName + ".obj");
				ObjStaticMesh.writeMtlObjFile(this, mtlFile, mapConverter.isTestMode);
				ObjStaticMesh.writeObj(this, objFile, mtlFile);

				// IN T3DLevelConvertorClass, convert() is called before scale()
				// So need to scale up the staticmesh else it will be always 1x (too small in most maps where conversion ratio is about 2/2.6X
				// scaled 3d is null, init to 1X so the upcoming super.scale() function will scale it correctly
				// Seems all is fliped by Y axis
				this.scale3d = new Vector3d(1, -1, 1);

				for( Vector3d position : this.moverProperties.getPositions().values() ) {
					position.y = -position.y;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String getStaticMeshReference() {
		return staticMeshReference;
	}
}
