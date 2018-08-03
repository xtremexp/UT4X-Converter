/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * All unconverted actors in map will be replaced by this one so mappers can see
 * what was not converted and find some possible replacements.
 * 
 * @author XtremeXp
 */
public class T3DNote extends T3DActor {

	String text;

	/**
	 * If true means this actor provides info about some unconverted actor so
	 * logger should display actor info
	 */
	boolean isUnconvertedInfo;

	/**
	 *
	 * @param mc
	 */
	public T3DNote(MapConverter mc) {
		super(mc, "Note");
	}
	
	public T3DNote(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	/**
	 *
	 * @param mc
	 * @param text
	 * @param isUnconvertedInfo
	 */
	public T3DNote(MapConverter mc, String text, boolean isUnconvertedInfo) {
		super(mc, "Note");
		this.text = text;
		this.isUnconvertedInfo = isUnconvertedInfo;
	}

	@Override
	public boolean analyseT3DData(String line) {

		if (line.startsWith("Text=")) {
			text = T3DUtils.getString(line);
		} else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {

		if (isUnconvertedInfo) {
			logger.warning("Unconverted " + name);
		}

		sbf.append(IDT).append("Begin Actor Class=Note Name=").append(name).append("\n");
		sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"SceneComp\"\n");
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tBegin Object Name=\"SceneComp\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");
		sbf.append(IDT).append("\tText=\"").append(text).append("\"\n");
		sbf.append(IDT).append("\tRootComponent=SceneComp\n");

		writeEndActor();

		return sbf.toString();
	}

}
