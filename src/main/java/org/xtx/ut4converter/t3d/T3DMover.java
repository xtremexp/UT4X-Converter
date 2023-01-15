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
import org.xtx.ut4converter.ucore.ue1.BrushPolyflag;

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
	 *
	 * @return String value
	 */
	public String toT3d() {

		// no generic mover actor for UE4 but UT4 with Lift blueprint
		// UE3 movers are InterpActors
		if (mapConverter.getOutputGame().getShortName().equals(UTGames.UTGame.UT4.shortName) || mapConverter.isTo(UnrealEngine.UE3)) {

			if (mapConverter.getOutputGame().getShortName().equals(UTGames.UTGame.UT4.shortName)) {
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

			// TODO replace with staticmesh
			// T3DStaticMesh staticMesh = new T3DStaticMesh(this.mapConverter, "T3DMover");


			return sbf.toString();
		}
		else {
			return super.toString();
		}
	}

	@Override
	public void convert() {

		moverProperties.convert();

		// transform permanently, update origin with panU/V
		super.convert();

		// convert brush to .obj staticmesh
		try {
			Files.createDirectories(Paths.get(this.mapConverter.getOutPath() + "/StaticMesh/"));
			// super.convert changes name to "name_tag->event" which is incompatible with a filename
			String baseName = this.name;

			if(baseName.contains("_")){
				baseName = baseName.substring(0, baseName.indexOf("_"));
			}

			if(baseName.contains("-")){
				baseName = baseName.substring(0, baseName.indexOf("-"));
			}

			File mtlFile = new File(this.mapConverter.getOutPath() + "/StaticMesh/" + baseName + ".mtl");
			File objFile = new File(this.mapConverter.getOutPath() + "/StaticMesh/" + baseName + ".obj");
			ObjStaticMesh.writeMtlObjFile(this, mtlFile);
			ObjStaticMesh.writeObj(this, objFile, mtlFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
