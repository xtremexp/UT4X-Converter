/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;

/**
 * All unconverted actors in map will be replaced by this one so mappers can see
 * what was not converted and find some possible replacements.
 * 
 * @author XtremeXp
 */
public class T3DNote extends T3DActor {

	private String text;

	/**
	 *
	 * @param mc Map Converter
	 */
	public T3DNote(MapConverter mc) {
		super(mc, "Note");
	}

	/**
	 *
	 * @param mc Map converter instance
	 * @param text Text in note
	 */
	public T3DNote(MapConverter mc, String text) {
		super(mc, "Note");
		this.text = text;
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


	public void setText(String text) {
		this.text = text;
	}

	/**
	 *
	 * @return T3d value
	 */
	@Override
	public String toT3d() {

		sbf.append(IDT).append("Begin Actor Class=Note Name=").append(name).append("\n");

		if(isTo(UTGames.UnrealEngine.UE4)) {
			sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"SceneComp\"\n");
			sbf.append(IDT).append("\tEnd Object\n");

			sbf.append(IDT).append("\tBegin Object Name=\"SceneComp\"\n");
			writeLocRotAndScale();
			sbf.append(IDT).append("\tEnd Object\n");
		} else {
			writeLocRotAndScale();
		}

		sbf.append(IDT).append("\tText=\"").append(text).append("\"\n");

		if(isTo(UTGames.UnrealEngine.UE4)) {
			sbf.append(IDT).append("\tRootComponent=SceneComp\n");
		}

		writeEndActor();

		return sbf.toString();
	}

}
