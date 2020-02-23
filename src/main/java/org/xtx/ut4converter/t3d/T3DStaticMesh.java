/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.ue1.BrushPolyflag;
import org.xtx.ut4converter.ucore.ue4.BodyInstance;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author XtremeXp
 */
public class T3DStaticMesh extends T3DSound {

	private final static String UT4_SHEET_SM = "/Game/RestrictedAssets/Environments/ShellResources/Meshes/Generic/SM_Sheet_500.SM_Sheet_500";
	private final static String UT4_SHEET_SM_MAT_WATER = "/Game/RestrictedAssets/Environments/ShellResources/Materials/Misc/M_Shell_Glass_E.M_Shell_Glass_E";

	private List<String> overiddeMaterials = new ArrayList<>();

	/**
	 * 
	 */
	private List<UPackageRessource> skins;

	private BodyInstance bodyInstance;

	/**
	 * e.g:
	 * "/Game/RestrictedAssets/Environments/ShellResources/Meshes/Generic/SM_Sheet_500.SM_Sheet_500"
	 * e.g/ StaticMesh=StaticMesh'BarrenHardware-epic.Decos.rec-lift'
	 */
	private UPackageRessource staticMesh;

	/**
	 * Temp hack
	 */
	private String forcedStaticMesh;

	/**
	 * If not null overiddes light map resolution for this static mesh (which is
	 * normally equal to 64 by default) TODO move this to some "Lightning" class
	 */
	private Integer overriddenLightMapRes;

	/**
	 * CastShadow=False TODO move this to some "Lightning" class
	 */
	private Boolean castShadow;
	
	/**
	 * UT3 (UT2004 as well) property
	 * Do not exists in UE4
	 */
	private Vector3d prePivot;

	/**
	 * Property seen in UT2004
	 */
	private UV2Mode uv2Mode = UV2Mode.UVM_Skin;

	/**
	 * UV2Texture
	 */
	private UPackageRessource uv2Texture;

	/**
	 * Unreal Engine 2: bCollideActors
	 * Unreal Engine 3: CollideActors
	 */
	private Boolean collideActors;

	/**
	 * Unreal Engine 3
	 * Default collision type for static meshes in ut3/ue3
	 */
	private UE3CollisionType collisionType = UE3CollisionType.COLLIDE_BlockAll;

	public enum UE3CollisionType {
		COLLIDE_CustomDefault,
		COLLIDE_NoCollision,
		COLLIDE_BlockAll,
		COLLIDE_BlockWeapons,
		COLLIDE_TouchAll,
		COLLIDE_TouchWeapons,
		COLLIDE_BlockAllButWeapons,
		COLLIDE_TouchAllButWeapons
	}

	public enum UV2Mode {
		UVM_MacroTexture,
		UVM_LightMap,
		UVM_Skin
	}

	/**
	 * 
	 * @param mc Map converter instance
	 * @param t3dClass T3d actor class
	 */
	public T3DStaticMesh(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		initialize();
	}

	/**
	 * Set scale to 1 so will be correctly scaled up after conversion
	 */
	private void initialize() {
		scale3d = new Vector3d(new double[] { 1d, 1d, 1d });
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.contains("StaticMesh=")) {
			staticMesh = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.STATICMESH);
		}
		// PrePivot=(X=280.000000,Y=0.000000,Z=0.000000)
		else if(line.startsWith("PrePivot=")){
			prePivot = T3DUtils.getVector3d(line, 0d);
		}
		else if(line.startsWith("UV2Texture=")){
			this.uv2Texture = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.TEXTURE);
		}
		else if(line.startsWith("UV2Mode=")){
			this.uv2Mode = UV2Mode.valueOf(T3DUtils.getString(line));
		}
		else if(line.startsWith("bCollideActors=") || line.startsWith("CollideActors")){
			this.collideActors = T3DUtils.getBoolean(line);
		}
		// UE3
		else if(line.startsWith("CollisionType=")){
			this.collisionType = UE3CollisionType.valueOf(T3DUtils.getString(line));
		}
		// UT2003/4 - Skins(0)=Texture'ArboreaTerrain.ground.flr02ar'
		// UT3      - Materials(0)=MaterialInstanceConstant'HU_Deck.SM.Materials.M_HU_Deck_SM_BioPot_Goo'
		else if (line.startsWith("Skins(") || line.startsWith("Materials(")) {

			if (skins == null) {
				skins = new ArrayList<>();
			}
			// can be Materials(0)=None as seen in VCTF-Kargo
			if (line.contains("\\'")) {
				skins.add(mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.TEXTURE));
			} else {
				skins.add(null); // simulating 'None'
			}
		} else {
			super.analyseT3DData(line);
		}

		return true;
	}
	

	/**
	 * Create t3d static mesh from t3d sheet brush using existing UT4 sheet sm
	 * brush Temp trick until we convert brushes to .fbx
	 * 
	 * @param mc
	 *            Map Converter
	 * @param sheetBrush
	 *            Sheet Brush (brush with only 2 polygons)
	 */
	T3DStaticMesh(MapConverter mc, T3DBrush sheetBrush) {
		super(mc, "StaticMesh");

		this.parent = sheetBrush;
		this.location = sheetBrush.location;
		// at this stage actor not yet converted so rotation should be in range
		// of the input engine
		this.rotation = Geometry.getRotation(sheetBrush.getPolyList().get(0).normal, mc.getInputGame().engine);
		this.tag = sheetBrush.tag + "_SM";
		this.name = sheetBrush.name + "_SM";
		this.forcedStaticMesh = UT4_SHEET_SM;

		// TODO get good scale from brush poly size
		// force small scale because rotation not good yet (so converted map
		// looks less 'weird')
		this.scale3d = new Vector3d(0.2d, 0.2d, 0.2d);
		this.overriddenLightMapRes = 128;
		this.castShadow = Boolean.FALSE;

		bodyInstance = new BodyInstance();
		bodyInstance.scale3D = this.scale3d;

		// set material from original brush
		//final T3DPolygon polyWithMat = sheetBrush.getPolyList().stream().filter(e -> e.getTexture() != null).findAny().orElse(null);

		/*
		if (polyWithMat != null) {
			this.overiddeMaterials.add(polyWithMat.getTexture().getConvertedName(this.mapConverter));
		}
		// none found, use default glass material
		else {*/
			this.overiddeMaterials.add(UT4_SHEET_SM_MAT_WATER);
		//}

		// Set no colission if originally not a solid brush
		if (BrushPolyflag.isNonSolid(sheetBrush.getPolyflags())) {
			bodyInstance.setCollisionEnabled(BodyInstance.CollisionEnabled.NoCollision);
		}
	}

	protected void writeStaticMeshComponent(){

		sbf.append(IDT).append("\tBegin Object Class=StaticMeshComponent Name=\"StaticMeshComponent0\"\n");
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tBegin Object Name=\"StaticMeshComponent0\"\n");

		// backward compatibility for created sheet SM from brushes
		if (forcedStaticMesh != null) {
			sbf.append(IDT).append("\t\tStaticMesh=StaticMesh'").append(forcedStaticMesh).append("'\n");
		} else if (staticMesh != null) {
			sbf.append(IDT).append("\t\tStaticMesh=StaticMesh'").append(staticMesh.getConvertedName(mapConverter)).append("'\n");
		}

		if (overriddenLightMapRes != null) {
			sbf.append(IDT).append("\t\tbOverrideLightMapRes=True\n");
			sbf.append(IDT).append("\t\tOverriddenLightMapRes=").append(overriddenLightMapRes).append("\n");
		}

		// TODO handle other collisionType
		if (UE3CollisionType.COLLIDE_NoCollision == collisionType || Boolean.FALSE == collideActors) {
			sbf.append(IDT).append("\t\tbUseDefaultCollision=False\n");
			sbf.append(IDT).append("\t\tBodyInstance=(CollisionProfileName=\"NoCollision\",CollisionEnabled=NoCollision)\n");
		}

		// TODO REFACTOR (was originally for quick set texture for sheet
		// brushes)
		if (!overiddeMaterials.isEmpty()) {
			for (int idx = 0; idx < overiddeMaterials.size(); idx++) {
				sbf.append(IDT).append("\t\tOverrideMaterials(").append(idx).append(")=Material'").append(overiddeMaterials.get(idx)).append("'\n");
			}
		}

		if (skins != null && !skins.isEmpty()) {
			int idx = 0;

			for (UPackageRessource skin : skins) {
				if (skin != null) {
					sbf.append(IDT).append("\t\tOverrideMaterials(").append(idx).append(")=Material'").append(skin.getConvertedName(mapConverter)).append("'\n");
				} else {
					sbf.append(IDT).append("\t\tOverrideMaterials(").append(idx).append(")=None\n");
				}

				idx++;
			}
		}

		if (castShadow != null) {
			sbf.append(IDT).append("\t\tCastShadow=").append(castShadow).append("\n");
		}

		writeLocRotAndScale();

		if (bodyInstance != null) {
			sbf.append(IDT).append("\t\t");
			bodyInstance.toT3d(sbf);
			sbf.append("\n");
		}

		sbf.append(IDT).append("\t\tCustomProperties\n"); // ?
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tStaticMeshComponent=StaticMeshComponent0\n");
		sbf.append(IDT).append("\tRootComponent=StaticMeshComponent0\n");
	}

	@Override
	public String toString() {

		sbf.append(IDT).append("Begin Actor Class=StaticMeshActor").append(" Name=").append(name).append("\n");


		writeStaticMeshComponent();

		writeEndActor();

		return sbf.toString();
	}

	@Override
	public void convert() {
		
		// UE4 does not support pre-pivot for staticmeshes unlike UE3/UDK
		if (prePivot != null) {

			if (this.scale3d != null) {
				prePivot.x *= scale3d.x;
				prePivot.y *= scale3d.y;
				prePivot.z *= scale3d.z;
			}

			prePivot = Geometry.rotate(prePivot, rotation);

			if (location != null) {
				location.sub(prePivot);
			} else {
				prePivot.negate();
				location = prePivot;
			}

		}

		if (staticMesh != null && mapConverter.convertStaticMeshes()) {
			staticMesh.export(UTPackageExtractor.getExtractor(mapConverter, staticMesh));
		}

		if(mapConverter.convertTextures()) {
			if (this.uv2Texture != null && this.uv2Mode == UV2Mode.UVM_Skin) {
				if(this.skins == null){
					this.skins = new ArrayList<>();
				}
				this.skins.clear();
				this.skins.add(this.uv2Texture);
			}

			if (this.skins != null) {
				for (final UPackageRessource matSkin : skins) {
					if (matSkin != null) {
						matSkin.export(UTPackageExtractor.getExtractor(mapConverter, matSkin));
					}
				}
			}
		}

		super.convert();
	}

	@Override
	public boolean isValidWriting() {
		// TODO remove test instancof decoration for testing purpose only
		return this instanceof T3DDecoration || staticMesh != null || forcedStaticMesh != null;
	}

	public UPackageRessource getStaticMesh() {
		return staticMesh;
	}
}
