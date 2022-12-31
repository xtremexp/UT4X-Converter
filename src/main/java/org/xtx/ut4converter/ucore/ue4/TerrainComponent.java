package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUE4Terrain;
import org.xtx.ut4converter.t3d.iface.T3D;

import java.util.LinkedList;
import java.util.List;

public abstract class TerrainComponent extends T3DObject implements T3D {

	int sectionBaseX;
	int sectionBaseY;

	int sizeQuads;

	private final List<Long> heightData = new LinkedList<>();

	int numComponent;

	public TerrainComponent(MapConverter mc, int numComponent, int sizeQuads) {
		super(mc);

		/**
		 * Terrain this landscape component belongs to
		 */
		this.numComponent = numComponent;
		this.sizeQuads = sizeQuads;
	}

	public int getSectionBaseX() {
		return sectionBaseX;
	}

	public void setSectionBaseX(int sectionBaseX) {
		this.sectionBaseX = sectionBaseX;
	}

	public int getSectionBaseY() {
		return sectionBaseY;
	}

	public void setSectionBaseY(int sectionBaseY) {
		this.sectionBaseY = sectionBaseY;
	}

	public List<Long> getHeightData() {
		return heightData;
	}


	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scale(Double newScale) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSizeQuads() {
		return sizeQuads;
	}

	protected String getT3dRelativeLocation() {

		return "RelativeLocation=(X=" + getSectionBaseX()  + ",Y=" + getSectionBaseY()  + ",Z=0.000000)";
	}

}
