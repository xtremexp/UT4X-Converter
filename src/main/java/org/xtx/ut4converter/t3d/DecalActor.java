package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

/**
 * Used to project material to surface
 *
 */
public class DecalActor extends T3DActor {

	private UPackageRessource decalMaterial;

	public DecalActor(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// TODO Auto-generated constructor stub
	}

	public boolean analyseT3DData(String line) {

		// UE3
		if (line.startsWith("DecalMaterial")) {
			decalMaterial = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.TEXTURE);
		}

		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	@Override
	public void convert() {
		if (decalMaterial != null) {
			decalMaterial.export(UTPackageExtractor.getExtractor(mapConverter, decalMaterial));
		}
	}

	@Override
	public String toString() {

		if (mapConverter.toUnrealEngine4()) {
			sbf.append(IDT).append("Begin Actor Class=DecalActor Name=").append(name).append("\n");
			sbf.append(IDT).append("Begin Object Class=BillboardComponent Name=\"Sprite\" Archetype=BillboardComponent'Default__DecalActor:Sprite'\n");
			sbf.append(IDT).append("End Object\n");
			sbf.append(IDT).append("Begin Object Class=ArrowComponent Name=\"ArrowComponent0\" Archetype=ArrowComponent'Default__DecalActor:ArrowComponent0'\n");
			sbf.append(IDT).append("End Object\n");
			sbf.append(IDT).append("Begin Object Class=DecalComponent Name=\"NewDecalComponent\" Archetype=DecalComponent'Default__DecalActor:NewDecalComponent'\n");
			sbf.append(IDT).append("End Object\n");
			sbf.append(IDT).append("Begin Object Name=\"Sprite\"\n");
			sbf.append(IDT).append("AttachParent=NewDecalComponent\n");
			sbf.append(IDT).append("End Object\n");
			sbf.append(IDT).append("Begin Object Name=\"ArrowComponent0\"\n");
			sbf.append(IDT).append("AttachParent=NewDecalComponent\n");
			sbf.append(IDT).append("End Object\n");
			sbf.append(IDT).append("Begin Object Name=\"NewDecalComponent\"\n");

			if (decalMaterial != null) {
				sbf.append(IDT).append("DecalMaterial=Material'").append(decalMaterial.getConvertedName(mapConverter)).append("'\n");
			}
			writeLocRotAndScale();
			sbf.append(IDT).append("End Object\n");
			sbf.append(IDT).append("Decal=NewDecalComponent\n");
			sbf.append(IDT).append("ArrowComponent=ArrowComponent0\n");
			sbf.append(IDT).append("SpriteComponent=Sprite\n");
			sbf.append(IDT).append("RootComponent=NewDecalComponent\n");
			writeEndActor();
		}

		return super.toString();
	}

}
