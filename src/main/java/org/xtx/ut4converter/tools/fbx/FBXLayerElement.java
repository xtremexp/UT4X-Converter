/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author XtremeXp
 */
public abstract class FBXLayerElement implements FBXWriter {

	Type type;
	short version;
	int index = 0;
	String name = "";
	public static DecimalFormat df = new DecimalFormat("#0.000000", new DecimalFormatSymbols(Locale.US));

	enum ReferenceInformationType {
		Direct, IndexToDirect
	}

	public static enum Type {
		LayerElementNormal, LayerElementTexture, LayerElementMaterial
	}

	public FBXLayerElement(Type type) {
		this.type = type;
	}
}
