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

import javax.vecmath.Vector3d;
import java.util.LinkedList;
import java.util.List;

import static org.xtx.ut4converter.ucore.UnrealEngine.*;

/**
 *
 * @author XtremeXp
 */
public class T3DStaticMesh extends T3DSound {


	/**
	 * List of overriding staticmesh materials
	 */
	private final List<UPackageRessource> skins = new LinkedList<>();


	/**
	 * e.g:
	 * "/Game/RestrictedAssets/Environments/ShellResources/Meshes/Generic/SM_Sheet_500.SM_Sheet_500"
	 * e.g/ StaticMesh=StaticMesh'BarrenHardware-epic.Decos.rec-lift'
	 */
	private UPackageRessource staticMesh;


	/**
	 * Min distance until staticmesh is rendered.
	 * UE2/UE3 - default 0 - 'CullDistance'
	 * UE4 - default 0 - 'MinDrawDistance'
	 */
	private float cullDistance;

	/**
	 * If not null overiddes light map resolution for this static mesh (which is
	 * normally equal to 64 by default)
	 */
	private Integer overriddenLightMapRes;

	/**
	 * CastShadow=False
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
		else if (line.startsWith("CollideActors")) {
			this.collideActors = T3DUtils.getBoolean(line);
		}
		else if(line.startsWith("CullDistance")){
			this.cullDistance = T3DUtils.getFloat(line);
		}
		// UE3
		else if(line.startsWith("CollisionType=")){
			this.collisionType = UE3CollisionType.valueOf(T3DUtils.getString(line));
		}
		// UT2003/4 - Skins(0)=Texture'ArboreaTerrain.ground.flr02ar'
		// UT3      - Materials(0)=MaterialInstanceConstant'HU_Deck.SM.Materials.M_HU_Deck_SM_BioPot_Goo'
		else if (line.startsWith("Skins(") || line.startsWith("Materials(")) {

			// can be Materials(0)=None as seen in VCTF-Kargo
			if (line.contains("'")) {
				skins.add(mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.TEXTURE));
			} else {
				skins.add(null); // simulating 'None'
			}
		} else {
			super.analyseT3DData(line);
		}

		return true;
	}

	public String toT3d() {

		final Component smComp = new Component("StaticMeshComponent", "StaticMeshComponent0", this);
		this.addComponent(smComp);

		if (staticMesh != null) {
			smComp.addProp("StaticMesh", "StaticMesh'" + staticMesh.getConvertedName() + "'");
		}

		if (this.cullDistance > 0) {
			smComp.addProp(isTo(UE3) ? "CullDistance" : "MinDrawDistance", this.cullDistance);
		}

		if (overriddenLightMapRes != null) {
			smComp.addProp("bOverrideLightMapRes", "True");
			smComp.addProp(isTo(UE3) ? "OverriddenLightMapResolution" : "OverriddenLightMapRes", overriddenLightMapRes);
		}

		// Collision
		if (UE3CollisionType.COLLIDE_NoCollision == collisionType || Boolean.FALSE == collideActors) {
			if (isTo(UE4)) {
				smComp.addProp("bUseDefaultCollision", "True");
				smComp.addProp("BodyInstance", "(CollisionProfileName=\"NoCollision\",CollisionEnabled=NoCollision)");
			} else if (isTo(UE3)) {
				this.addConvProperty("CollisionType", UE3CollisionType.COLLIDE_NoCollision.name());
			}
		}

		// Material override
		int idx = 0;
		for (UPackageRessource skin : skins) {
			if (isTo(UE4)) {
				smComp.addProp("OverrideMaterials(" + idx + ")", skin != null ? "Material'" + skin.getConvertedName() + "'" : "None");
			} else if (isTo(UE3)) {
				smComp.addProp("Materials(" + idx + ")", skin != null ? "Material'" + skin.getConvertedName() + "'" : "None");
			}
			idx++;
		}

		if (castShadow != null) {
			smComp.addProp("CastShadow", castShadow);
		}

		// UE3 collide complex staticmesh by default
		// UE4 no property on actor, have to set it in the content browser
		if (isTo(UE3)) {
			this.addConvProperty("bCollideComplex", true);
			this.addConvProperty("CollisionComponent", "StaticMeshComponent'" + smComp.getObjName() + "'");
		}

		sbf.append(super.toT3dNew());

		// for possible embedded sound
		return super.toT3d();
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
				this.skins.clear();
				this.skins.add(this.uv2Texture);
			}

			for (final UPackageRessource matSkin : skins) {
				if (matSkin != null) {
					matSkin.export(UTPackageExtractor.getExtractor(mapConverter, matSkin));
				}
			}
		}

		super.convert();
	}

	@Override
	public boolean isValidWriting() {
		// TODO remove test instancof decoration for testing purpose only
		return this instanceof T3DDecoration || staticMesh != null;
	}

	public UPackageRessource getStaticMesh() {
		return staticMesh;
	}
}
