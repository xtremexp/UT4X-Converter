package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class SpecialEvent extends T3DSound {

	private Boolean bBroadcast;

	private Boolean bPlayerViewRot;

	private Float damage;

	private String damageString;

	private UPackageRessource sound;

	public SpecialEvent(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean analyseT3DData(String line) {

		// UE1 -> "URL="hepburn2"
		if (line.startsWith("DamageString=")) {
			damageString = T3DUtils.getString(line);
		} else if (line.startsWith("bBroadcast=")) {
			bBroadcast = T3DUtils.getBoolean(line);
		} else if (line.startsWith("bPlayerViewRot=")) {
			bPlayerViewRot = T3DUtils.getBoolean(line);
		} else if (line.startsWith("damage=")) {
			damage = T3DUtils.getFloat(line);
		} else if (line.startsWith("Sound=")) {
			sound = mapConverter.getUPackageRessource(line.split("'")[1], T3DRessource.Type.SOUND);
		} else {
			return super.analyseT3DData(line);
		}
		
		return false;
	}

	@Override
	public void convert() {

		if (sound != null && this.mapConverter.convertSounds()) {
			sound.export(UTPackageExtractor.getExtractor(mapConverter, sound));
		}
		
		super.convert();
	}

	public String toT3d() {
		sbf.append(IDT).append("Begin Actor Class=U1SpecialEvent_C Name=").append(name).append("\n");

		sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("DefaultSceneRoot=DefaultSceneRoot\n");

		if (damageString != null) {
			sbf.append(IDT).append("DamageString=\"").append(damageString).append("\"\n");
		}

		if (bBroadcast != null) {
			sbf.append(IDT).append("bBroadcast=").append(bBroadcast).append("\n");
		}

		if (bPlayerViewRot != null) {
			sbf.append(IDT).append("bBroadcast=").append(bPlayerViewRot).append("\n");
		}

		if (damage != null) {
			sbf.append(IDT).append("Damage=\"").append(damage).append("\"\n");
		}

		if (damageString != null) {
			sbf.append(IDT).append("DamageString=\"").append(damageString).append("\"\n");
		}

		if (damageString != null) {
			sbf.append(IDT).append("DamageString=\"").append(damageString).append("\"\n");
		}

		if (sound != null) {
			sbf.append(IDT).append("\tSound=SoundCue'").append(sound.getConvertedName()).append("'\n");
		}
		
		writeEndActor();

		return sbf.toString();
	}

}
