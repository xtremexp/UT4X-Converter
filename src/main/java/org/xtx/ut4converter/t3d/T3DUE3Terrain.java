/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author XtremeXp
 */
public class T3DUE3Terrain extends T3DActor {


	/**
	 * Both collision and landscape height data
	 */
	private TerrainHeight terrainHeight;

	/**
	 * Seems to be related to visibility data
	 * (if data = 0 then terrain part is invisible)
	 */
	private TerrainInfoData terrainInfoData;

	private TerrainActorMembers terrainActorMembers;

	/**
	 * Seems similar to landscape components
	 */
	private final List<TerrainComponent> terrainComponents = new LinkedList<>();


	/**
	 *             MaxTesselationLevel=4
	 *             TesselationDistanceScale=4.000000
	 *             CollisionTesselationLevel=4
	 *             NumPatchesX=128
	 *             NumPatchesY=128
	 *             MaxComponentSize=6
	 *             StaticLightingResolution=2
	 *             bIsOverridingLightResolution=0
	 *             bCastShadow=1
	 *             bForceDirectLightMap=1
	 *             bCastDynamicShadow=0
	 *             bBlockRigidBody=1
	 *             bAcceptsDynamicLights=1
	 *             LightingChannels=5
	 */
	static class TerrainActorMembers {
		private int numPatchesX;
		private int numPatchesY;
		private int maxComponentSize;
		private int staticLightingResolution;
		private boolean bCastShadow;
	}

	static class TerrainComponent {
		private int sectionBaseX;
		private int sectionBaseY;
		private int sectionSizeX;
		private int sectionSizeY;
		private int trueSectionSizeX;
		private int trueSectionSizeY;
		private boolean bCastDynamicShadow;
		private boolean bForceDirectLightMap;

		public int getSectionBaseX() {
			return sectionBaseX;
		}

		public int getSectionBaseY() {
			return sectionBaseY;
		}

		public int getSectionSizeX() {
			return sectionSizeX;
		}

		public int getSectionSizeY() {
			return sectionSizeY;
		}

		public int getTrueSectionSizeX() {
			return trueSectionSizeX;
		}

		public int getTrueSectionSizeY() {
			return trueSectionSizeY;
		}

		public boolean isbCastDynamicShadow() {
			return bCastDynamicShadow;
		}

		public boolean isbForceDirectLightMap() {
			return bForceDirectLightMap;
		}
	}

	static class TerrainInfoData {
		private final List<Integer> data = new ArrayList<>();

		public void addValue(final Integer value){
			this.data.add(value);
		}
	}

	static class TerrainHeight {
		int count;
		short width;
		short height;

		private final List<Integer> heightMap = new ArrayList<>();

		public void addValue(final Integer value){
			this.heightMap.add(value);
		}

		public List<Integer> getHeightMap() {
			return heightMap;
		}

		public int getCount() {
			return count;
		}

		public short getWidth() {
			return width;
		}

		public short getHeight() {
			return height;
		}
	}

	private String isReading;

	/**
	 * Terrain component being parsed
	 */
	private TerrainComponent terrainComponent;

	public T3DUE3Terrain(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	@Override
	public boolean analyseT3DData(String line) {

		// TerrainMap=Texture'myLevel.Package0.TowerHeightMap'
		if (line.startsWith("End")) {

			if ("TerrainComponent".equals(isReading)) {
				this.terrainComponents.add(terrainComponent);
			}

			isReading = null;
		} else if (line.startsWith("Begin TerrainHeight")) {
			this.terrainHeight = new TerrainHeight();
			isReading = "TerrainHeight";
			//heightMapTexture = mapConverter.getUPackageRessource(line.split("\\'")[1], T3DRessource.Type.TEXTURE);
		} else if (line.startsWith("Begin TerrainInfoData")) {
			this.terrainInfoData = new TerrainInfoData();
			isReading = "TerrainInfoData";
		} else if(line.startsWith("Begin Object Class=TerrainComponent")){
			terrainComponent = new TerrainComponent();
			isReading = "TerrainComponent";
		}  else if(line.startsWith("Begin TerrainActorMembers")){
			terrainActorMembers = new TerrainActorMembers();
			isReading = "TerrainActorMembers";
		}
		else if ("TerrainHeight".equals(isReading)) {

			//             Count=29469	Width=209	Height=141
			if (line.startsWith("Count")) {
				final String[] tmp = line.split("\t");
				terrainHeight.count = Integer.parseInt(tmp[0].split("=")[1]);
				terrainHeight.width = Short.parseShort(tmp[1].split("=")[1]);
				terrainHeight.height = Short.parseShort(tmp[2].split("=")[1]);
			} else {
				// parse terrain height values
				//             32768	32768	32768	32768	32768	32768	32768	32768
				final String[] vals = line.split("\t");
				for (final String val : vals) {
					terrainHeight.heightMap.add(Integer.valueOf(val));
				}
			}


		} else if ("TerrainComponent".equals(isReading)) {

			if (line.startsWith("SectionBaseX")) {
				this.terrainComponent.sectionBaseX = T3DUtils.getInteger(line);
			} else if (line.startsWith("SectionBaseY")) {
				this.terrainComponent.sectionBaseY = T3DUtils.getInteger(line);
			} else if (line.startsWith("SectionSizeX")) {
				this.terrainComponent.sectionSizeX = T3DUtils.getInteger(line);
			} else if (line.startsWith("SectionSizeY")) {
				this.terrainComponent.sectionSizeY = T3DUtils.getInteger(line);
			} else if (line.startsWith("TrueSectionSizeX")) {
				this.terrainComponent.trueSectionSizeX = T3DUtils.getInteger(line);
			} else if (line.startsWith("TrueSectionSizeY")) {
				this.terrainComponent.trueSectionSizeY = T3DUtils.getInteger(line);
			} else if (line.startsWith("bCastDynamicShadow")) {
				this.terrainComponent.bCastDynamicShadow = T3DUtils.getBoolean(line);
			} else if (line.startsWith("bForceDirectLightMap")) {
				this.terrainComponent.bForceDirectLightMap = T3DUtils.getBoolean(line);
			}

		} else if ("TerrainInfoData".equals(isReading)) {
			if (line.startsWith("Count")) {

			} else {
				//                   0	  0	  0	  0	  1	  1	  1	  1
				final String[] vals = line.replaceAll(" ", "").split("\t");
				for (final String val : vals) {
					terrainInfoData.data.add(Integer.valueOf(val));
				}
			}
		} else if ("TerrainActorMembers".equals(isReading)) {
			/**
			 * 		private int numPatchesX;
			 * 		private int numPatchesY;
			 * 		private int maxComponentSize;
			 * 		private int staticLightingResolution;
			 * 		private int bCastShadow;
			 */
			if (line.startsWith("NumPatchesX")) {
				this.terrainActorMembers.numPatchesX = T3DUtils.getInteger(line);
			} else if (line.startsWith("NumPatchesY")) {
				this.terrainActorMembers.numPatchesY = T3DUtils.getInteger(line);
			} else if (line.startsWith("MaxComponentSize")) {
				this.terrainActorMembers.maxComponentSize = T3DUtils.getInteger(line);
			} else if (line.startsWith("StaticLightingResolution")) {
				this.terrainActorMembers.staticLightingResolution = T3DUtils.getInteger(line);
			} else if (line.startsWith("bCastShadow")) {
				this.terrainActorMembers.bCastShadow = T3DUtils.getBoolean(line);
			}
		}  else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	public TerrainHeight getTerrainHeight() {
		return terrainHeight;
	}

	public TerrainInfoData getTerrainInfoData() {
		return terrainInfoData;
	}

	public List<TerrainComponent> getTerrainComponents() {
		return terrainComponents;
	}
}
