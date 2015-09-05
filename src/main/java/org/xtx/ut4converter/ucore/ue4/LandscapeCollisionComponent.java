/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import java.util.List;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.iface.T3D;

/**
 *
 * @author XtremeXp
 */
public class LandscapeCollisionComponent extends TerrainComponent implements T3D {

	final String BASE_NAME = "LandscapeHeightfieldCollisionComponent";

	LandscapeComponent renderComponent;

	/**
	 * List of boolean for each square of the section saying if it's rendered or
	 * not
	 */
	List<Boolean> visibilityData;

	int sectionBaseX;
	int sectionBaseY;

	float collisionScale;

	Vector3d relativeLocation;

	public LandscapeCollisionComponent(MapConverter mc, int numComponent, int sizeQuads) {

		super(mc, numComponent, sizeQuads);
		collisionScale = 1f;
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

	public void setCollisionScale(float collisionScale) {
		this.collisionScale = collisionScale;
	}

	public void setRelativeLocation(Vector3d relativeLocation) {
		this.relativeLocation = relativeLocation;
	}

	public LandscapeComponent getRenderComponent() {
		return renderComponent;
	}

	public void setRenderComponent(LandscapeComponent renderComponent) {
		this.renderComponent = renderComponent;
	}

	@Override
	public String toT3d(StringBuilder sb, String prefix) {

		String base = "\t\t";

		sb.append(base).append("Begin Object Name=\"").append(getName()).append("\"\n");

		if (sectionBaseX > 0) {
			sb.append(base).append("\t\tSectionBaseX=").append(sectionBaseX).append("\n");
		}

		if (sectionBaseY > 0) {
			sb.append(base).append("\t\tSectionBaseY=").append(sectionBaseY).append("\n");
		}

		sb.append(base).append("\tCollisionSizeQuads=").append(sizeQuads).append("\n");
		sb.append(base).append("\tCollisionScale=").append(collisionScale).append("\n");

		// needed for not crashing TODO GUID generator
		sb.append(base).append("\tHeightfieldGuid=EC3963244F511FA86677C79295D9B2F3\n");

		sb.append(base).append("\tRenderComponent=LandscapeComponent'").append(renderComponent.getName()).append("'\n");
		sb.append(base).append("\tAttachParent=RootComponent0\n");

		if (getSectionBaseX() > 0 || getSectionBaseY() > 0) {
			sb.append(base).append("\t").append(getT3dRelativeLocation()).append("\n");
		}

		sb.append(base).append("\tCustomProperties CollisionHeightData");

		for (int x = 0; x < heightData.length; x++) {

			for (int y = 0; y < heightData[0].length; y++) {
				sb.append(" " + heightData[x][y]);
			}
		}

		sb.append("\n");

		if (visibilityData != null && !visibilityData.isEmpty()) {

			sb.append(base).append("\tCustomProperties DominantLayerData ");

			for (Boolean isVisible : visibilityData) {
				sb.append(isVisible ? "ff" : "00");
			}

			sb.append("\n");
		}

		sb.append(base).append("End Object\n");

		return sb.toString();
	}

	@Override
	public String getName() {
		return BASE_NAME + "_" + numComponent;
	}

	public void setVisibilityData(List<Boolean> visibilityData) {
		this.visibilityData = visibilityData;
	}

	public List<Boolean> getVisibilityData() {
		return visibilityData;
	}

}
