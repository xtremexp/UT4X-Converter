package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.iface.T3D;

import java.util.LinkedList;
import java.util.List;

public abstract class TerrainComponent extends T3DObject implements T3D {

	int sectionBaseX;
	int sectionBaseY;

	int sizeQuads;

	private List<Integer> heightData = new LinkedList<>();

	int numComponent;

	public TerrainComponent(MapConverter mc, int numComponent, int sizeQuads) {
		super(mc);

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

	public List<Integer> getHeightData() {
		return heightData;
	}

	public void setHeightData(List<Integer> heightData) {
		this.heightData = heightData;
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

	public int getNumComponent() {
		return numComponent;
	}

	public void setNumComponent(int numComponent) {
		this.numComponent = numComponent;
	}

	public int getSizeQuads() {
		return sizeQuads;
	}

	public void setSizeQuads(int sizeQuads) {
		this.sizeQuads = sizeQuads;
	}

	protected String getT3dRelativeLocation() {

		return "RelativeLocation=(X=" + getSectionBaseX()  + ",Y=" + getSectionBaseY()  + ",Z=0.000000)";
	}

}
