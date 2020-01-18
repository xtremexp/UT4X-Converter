/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.iface.T3D;

import javax.vecmath.Vector3d;

/**
 *
 * @author XtremeXp
 */
public class LandscapeComponent extends TerrainComponent implements T3D {

	/**
	 * Collision component attached to this component
	 */
	private LandscapeCollisionComponent colisionComponent;

	private int subsectionSizeQuads;

	short collisionMipLevel;

	private Vector3d relativeLocation;

	/**
	 * Hexadecimal values. Much more accurate than collision height map 32768 ->
	 * 80800080x = 2155872384 = 256 + (32768 * 65536)
	 */

	public LandscapeComponent(MapConverter mc, int numComponent, int sizeQuads) {
		super(mc, numComponent, sizeQuads);
	}

	/**
	 * Creates a landscape component from collision component.
	 * Prior to UE4 there were no collision component specificaly so it's easy to create landscape component
	 *
	 * @param mc
	 * @param colComp Collision component
	 */
	public LandscapeComponent(MapConverter mc, LandscapeCollisionComponent colComp) {

		super(mc, colComp.numComponent, colComp.getSizeQuads());

		this.colisionComponent = colComp;
		this.subsectionSizeQuads = colComp.getSizeQuads();

		// resolution for landscape component is much higher then collision component height resolution
		for (final Integer height : colComp.getHeightData()) {
			// HeightMap[Landscape] = HeightMap[Collision]  * 256
			this.getHeightData().add(height * 256);
		}

		// this.heightData = colComp.getHeightData();
		this.sectionBaseX = colComp.getSectionBaseX();
		this.sectionBaseY = colComp.getSectionBaseY();
	}


	public void setColisionComponent(LandscapeCollisionComponent colisionComponent) {
		this.colisionComponent = colisionComponent;
	}

	public void setSubsectionSizeQuads(int subsectionSizeQuads) {
		this.subsectionSizeQuads = subsectionSizeQuads;
	}

	public void setCollisionMipLevel(short collisionMipLevel) {
		this.collisionMipLevel = collisionMipLevel;
	}

	public void setRelativeLocation(Vector3d relativeLocation) {
		this.relativeLocation = relativeLocation;
	}

	@Override
	public void convert() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void scale(Double newScale) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public boolean analyseT3DData(String line) {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {

		String base = "\t\t";

		sb.append(base).append("Begin Object Name=\"").append(getName()).append("\"\n");

		sb.append(base).append("\tSectionBaseX=").append(sectionBaseX).append("\n");
		sb.append(base).append("\tSectionBaseY=").append(sectionBaseY).append("\n");

		sb.append(base).append("\tComponentSizeQuads=").append(sizeQuads).append("\n");
		sb.append(base).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
		short numSubsections = 1;
		sb.append(base).append("\tNumSubsections=").append(numSubsections).append("\n");

		sb.append(base).append("\tCollisionComponent=LandscapeHeightfieldCollisionComponent'").append(colisionComponent.getName()).append("'\n");
		sb.append(base).append("\tAttachParent=RootComponent0\n");

		if (getSectionBaseX() > 0 || getSectionBaseY() > 0) {
			sb.append(base).append("\t").append(getT3dRelativeLocation()).append("\n");
		}

		sb.append(base).append("\tCustomProperties LandscapeHeightData");

		for (final int height : getHeightData()) {
			sb.append(" ").append(Integer.toHexString(height));
		}

		sb.append(" LayerNum=0\n");
		sb.append(base).append("End Object\n");
	}

	@Override
	public String getName() {
		return "LandscapeComponent_" + numComponent;
	}

}
