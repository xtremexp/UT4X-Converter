/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.ConversionSettings;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.tools.objmesh.ObjStaticMesh;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.ue1.BrushPolyflag;

import javax.vecmath.Vector3d;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
	 * Disabled until mesh is good
	 * Convert to t3d this mover
	 * @return String value
	 */
	public String toT3d() {

		// UT4 - Convert to Lift actor
		if (mapConverter.getOutputGame().getShortName().equals(UTGames.UTGame.UT4.shortName)) {
			moverProperties.writeUT4MoverActor(sbf);
		}
		// UE3, movers not yet converted to staticmesh
		// writting the original brush as well
		else if (mapConverter.isTo(UnrealEngine.UE3)) {
			moverProperties.writeUE3MoverActor(sbf);

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

		return sbf.toString();
	}

	@Override
	public void convert() {

		moverProperties.convert();

		// transform permanently, update origin with panU/V
		super.convert();


		// TODO UE3 make brush to .t3d or .ase conversion (.obj not supported by editor)
		// convert brush to .obj staticmesh
		if (mapConverter.isTo(UnrealEngine.UE4, UnrealEngine.UE5)) {
			try {
				File smMoversFolder;
				String inMapAsPackageName = MapConverter.getInMapAsPackageName(mapConverter.getConversionSettings().getInputMapFile());

				// E.G: \UT4X-Converter\Converted\Passage\StaticMesh
				if (mapConverter.getExportOption() == ConversionSettings.ExportOption.BY_TYPE) {
					smMoversFolder = new File(this.mapConverter.getOutPath() + "/StaticMesh/");
				}
				// E.G: \UT4X-Converter\Converted\Passage\Passage[MapPackageName]
				else {
					smMoversFolder = new File(this.mapConverter.getOutPath() + File.separator + inMapAsPackageName + "/");
				}

				if (!smMoversFolder.exists()) {
					Files.createDirectories(smMoversFolder.toPath());
				}

				// super.convert changes name to "name_tag->event" which is incompatible with a filename
				// so need to use the original name
				String baseName = this.originalName;


				// E.G: /Game/Converted/Passage-U1/Mover0.Mover0
				if (mapConverter.getExportOption() == ConversionSettings.ExportOption.BY_TYPE) {
					this.staticMeshReference = mapConverter.getUt4ReferenceBaseFolder() + "/" + baseName + "." + baseName;
				}
				// Export by package
				else {
					// E.G: /Game/Converted/Passage-U1/Passage/Mover0.Mover0
					this.staticMeshReference = mapConverter.getUt4ReferenceBaseFolder() + "/" + inMapAsPackageName + "/" + baseName + "." + baseName;

					// E.G: /UnrealTournamentEditor/Content/Converted/Passage-U1/Passage/Mover0.Mover0
					File dummyUAssetFileForPackage = new File(mapConverter.getUt4ReferenceBaseFolderFile() + "/" + inMapAsPackageName + "/" + mapConverter.getDummyUAssetFile().getName());

					if (!dummyUAssetFileForPackage.exists()) {
						Files.createDirectories(dummyUAssetFileForPackage.toPath().getParent());
						Files.createFile(dummyUAssetFileForPackage.toPath());

						Files.copy(mapConverter.getDummyUAssetFile().toPath(), dummyUAssetFileForPackage.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
				}

				final File mtlFile = new File(smMoversFolder + "/" + baseName + ".mtl");
				final File objFile = new File(smMoversFolder + "/" + baseName + ".obj");

				ObjStaticMesh.writeMtlObjFile(this, mtlFile, mapConverter.isTestMode);
				ObjStaticMesh.writeObj(this, objFile, mtlFile);

				// IN T3DLevelConvertorClass, convert() is called before scale()
				// So need to scale up the staticmesh else it will be always 1x (too small in most maps where conversion ratio is about 2/2.6X
				// scaled 3d is null, init to 1X so the upcoming super.scale() function will scale it correctly
				// Seems all is fliped by Y axis
				this.scale3d = new Vector3d(1, -1, 1);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String getStaticMeshReference() {
		return staticMeshReference;
	}

	public MoverProperties getMoverProperties() {
		return moverProperties;
	}
}
