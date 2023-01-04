/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UnrealEngine;

/**
 *
 * @author XtremeXp
 */
public class T3DEmitter extends T3DSound {

	/**
	 * For replacing corana light from UT99/U1/UT2003/UT2004
	 */
	private static final String TEMPLATE_LENSFLARE = "/Game/RestrictedAssets/Effects/Atmos/Flares/Particles/P_LensFlare.P_LensFlare";

	private String template;

	public T3DEmitter(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	/**
	 * Create emitter with some basic default lens flare from corona light
	 * 
	 * @param mc
	 *            Map converter
	 * @param light
	 *            Light with lens flare
	 * @return Lens flare emitter
	 */
	public static T3DEmitter createLensFlare(MapConverter mc, T3DLight light) {

		T3DEmitter emitter = new T3DEmitter(mc, light.t3dClass);
		emitter.location = light.location;
		emitter.drawScale = light.drawScale;
		emitter.scale3d = light.scale3d;
		emitter.name = light.name + "_LF";
		emitter.template = TEMPLATE_LENSFLARE;
		// TODO handle skin

		return emitter;
	}

	public String toT3d() {

		sbf.append(IDT).append("Begin Actor Class=Emitter Name=").append(name).append("\n");
		sbf.append(IDT).append("\tBegin Object Name=\"ParticleSystemComponent0\"\n");
		sbf.append(IDT).append("\t\tTemplate=ParticleSystem'").append(template).append("'\n");

		writeLocRotAndScale();

		sbf.append(IDT).append("\tEnd Object\n");
		sbf.append(IDT).append("\tParticleSystemComponent=ParticleSystemComponent0\n");
		sbf.append(IDT).append("\tRootComponent=ParticleSystemComponent0\n");

		writeEndActor();

		return sbf.toString();
	}


	/**
	 * TODO map emitter work for UT3
	 * @return <code>true</code> if it's valid writting else <code>false</code>
	 */
	@Override
	public boolean isValidWriting() {
		return this.mapConverter.isTo(UnrealEngine.UE4);
	}
}
