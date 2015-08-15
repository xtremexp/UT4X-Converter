/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

/**
 *
 * @author XtremeXp
 */
public class FBXLayerElementMaterial extends FBXLayerElement {

	/**
	 * ?
	 */
	int materials;

	public FBXLayerElementMaterial() {
		super(Type.LayerElementMaterial);
	}

	private enum MappingInformationType {
		AllSame
	}

	@Override
	public void writeFBX(StringBuilder sb) {
		sb.append("\t\tLayerElementMaterial: ").append(index).append(" {\n");

		sb.append("\t\t\tVersion: ").append(version).append("\n");
		sb.append("\t\t\tName: \"").append(name).append("\"\n");
		sb.append("\t\t\tMappingInformationType: \"").append(MappingInformationType.AllSame.name()).append("\"\n");
		sb.append("\t\t\tMaterials: \t").append(materials).append("\t\n");

		sb.append("\t\t}\n");
	}

}
