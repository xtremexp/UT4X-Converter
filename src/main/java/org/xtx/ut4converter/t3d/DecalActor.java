package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.vecmath.Vector3d;

/**
 * Used to project material to surface
 *
 */
public class DecalActor extends T3DActor {

	private UPackageRessource decalMaterial;
	private Vector3d decalSize;
	private Integer sortOrder;

	public DecalActor(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// default decalsize in UE3
		decalSize = new Vector3d(200d, 300d, 200d);
	}

	public boolean analyseT3DData(String line) {

		// UE3
		if (line.startsWith("DecalMaterial")) {
			decalMaterial = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.TEXTURE);
		} else if (line.startsWith("Width=")) {
			decalSize.y = T3DUtils.getDouble(line);
		} else if (line.startsWith("Height=")) {
			decalSize.z = T3DUtils.getDouble(line);
		} else if (line.startsWith("FarPlane=")) {
			decalSize.x = T3DUtils.getDouble(line);
		} else if (line.startsWith("SortOrder=")) {
			sortOrder = T3DUtils.getInteger(line);
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

		if (decalSize != null) {
			Vector3d decalOffset = new Vector3d(decalSize.x / mapConverter.getScale(), 0d, 0d);
			if (rotation != null) {
				decalOffset = Geometry.rotate(decalOffset, rotation);
			}

			// x offset must not be changed by scale
			// since the scale() operation occurs after convert()
			// have to reduce down decalSize.x
			location.add(decalOffset);
		}

		super.convert();
	}

	public String toT3d() {

		if (mapConverter.isTo(UTGames.UnrealEngine.UE4)) {
			sbf.append(IDT).append("Begin Actor Class=DecalActor Name=").append(name).append("\n");
			sbf.append(IDT).append("\tBegin Object Class=DecalComponent Name=\"NewDecalComponent\" Archetype=DecalComponent'Default__DecalActor:NewDecalComponent'\n");
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tBegin Object Name=\"NewDecalComponent\"\n");

			if (decalMaterial != null) {
				sbf.append(IDT).append("\t\tDecalMaterial=Material'").append(decalMaterial.getConvertedName(mapConverter)).append("'\n");
			}

			if (decalSize != null) {
				sbf.append(IDT).append("\t\tDecalSize=(X=").append(decalSize.x).append(",Y=").append(decalSize.y).append(",Z=").append(decalSize.z).append(")\n");
			}

			if (sortOrder != null) {
				sbf.append(IDT).append("\t\tSortOrder=").append(sortOrder).append("\n");
			}
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");
			sbf.append(IDT).append("\tDecal=NewDecalComponent\n");
			sbf.append(IDT).append("\tRootComponent=NewDecalComponent\n");
			writeEndActor();
		}

		return super.toString();
	}

}
