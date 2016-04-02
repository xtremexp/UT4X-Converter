/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * 
 * @author XtremeXp
 */
public class T3DPickup extends T3DSound {

	String convertedPickupClass;

	/**
	 *
	 * @param mc
	 * @param t3dClass
	 */
	public T3DPickup(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {

		if (convertedPickupClass == null) {
			return "";
		}

		if (mapConverter.toUnrealEngine4()) {

			sbf.append(IDT).append("Begin Actor Class=").append(convertedPickupClass).append(" Name=").append(name).append("\n");

			T3DMatch.UE4_RCType rootType;

			// TODO refactoring
			if (match.convertProperties.containsKey(T3DMatch.UE4_RCType.SCENE_COMP.name)) {
				rootType = T3DMatch.UE4_RCType.SCENE_COMP;
			} else if (match.convertProperties.containsKey(T3DMatch.UE4_RCType.ICON.name)) {
				rootType = T3DMatch.UE4_RCType.ICON;
			} else {
				rootType = T3DMatch.UE4_RCType.CAPSULE;
			}

			if (!rootType.equals(T3DMatch.UE4_RCType.ICON)) {
				sbf.append(IDT).append("\tBegin Object Class=").append(rootType.name).append(" Name=\"").append(rootType.alias).append("\" \n");
				sbf.append(IDT).append("\tEnd Object\n");
			}

			sbf.append(IDT).append("\tBegin Object Name=\"").append(rootType.alias).append("\"\n");
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");

			if (match != null) {
				for (String property : match.properties.keySet()) {
					sbf.append(IDT).append("\t").append(property).append("=");
					sbf.append(match.properties.get(property)).append("\n");
				}
			}

			// sbf.append(IDT).append("\tCollision=").append(rootType.alias).append("\n");
			sbf.append(IDT).append("\tRootComponent=").append(rootType.alias).append("\n");

			writeEndActor();
		}

		return super.toString();
	}

	/**
     * 
     */
	@Override
	public void convert() {

		if (t3dClass != null) {

			match = mapConverter.getMatchFor(t3dClass, false, properties);

			if (match != null) {
				convertedPickupClass = match.actorClass.get(0);

				// TODO refactor
				if (match.convertProperties.containsKey(T3DMatch.Z_OFFSET)) {
					offsetZLocation = (Double) match.convertProperties.get(T3DMatch.Z_OFFSET);
				}
			} else {
				validWriting = false;
				return;
			}

		}

		super.convert();
	}

}
