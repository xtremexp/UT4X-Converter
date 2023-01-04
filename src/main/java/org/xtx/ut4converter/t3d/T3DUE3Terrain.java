/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.ue3.TerrainLayer;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
	 * Alpha layers
	 */
	private final List<List<Integer>> alphaLayers = new LinkedList<>();

	/**
	 * Terrain layers
	 */
	private final List<TerrainLayer> terrainLayers = new LinkedList<>();


	static class TerrainActorMembers {

		/**
		 * Default num patches X in UE3
		 */
		private int numPatchesX = 1;

		/**
		 * Default num patches Y in UE3
		 */
		private int numPatchesY = 1;

		/**
		 * Default component size in UE3
		 */
		private int maxComponentSize = 16;

		private int staticLightingResolution;

		private boolean bCastShadow;

		private boolean bMorphingEnabled;
		/**
		 * Default tesselation level for unreal engine 3 terrains
		 */
		private int maxTesselationLevel = 4;

		public int getNumPatchesX() {
			return numPatchesX;
		}

		public int getNumPatchesY() {
			return numPatchesY;
		}

		public int getMaxComponentSize() {
			return maxComponentSize;
		}

		public int getMaxTesselationLevel() {
			return maxTesselationLevel;
		}
	}

	/**
	 * Terrain is composed of X components by Y components
	 */
	static class TerrainComponent {
		private int sectionBaseX;
		private int sectionBaseY;
		private int sectionSizeX;
		private int sectionSizeY;
		private int trueSectionSizeX;
		private int trueSectionSizeY;
		private boolean bCastDynamicShadow;
		private boolean bForceDirectLightMap;

		public void setSectionBaseX(int sectionBaseX) {
			this.sectionBaseX = sectionBaseX;
		}

		public void setSectionBaseY(int sectionBaseY) {
			this.sectionBaseY = sectionBaseY;
		}

		public void setSectionSizeX(int sectionSizeX) {
			this.sectionSizeX = sectionSizeX;
		}

		public void setSectionSizeY(int sectionSizeY) {
			this.sectionSizeY = sectionSizeY;
		}

		public void setTrueSectionSizeX(int trueSectionSizeX) {
			this.trueSectionSizeX = trueSectionSizeX;
		}

		public void setTrueSectionSizeY(int trueSectionSizeY) {
			this.trueSectionSizeY = trueSectionSizeY;
		}

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

		public List<Integer> getData() {
			return data;
		}

		public void addValue(final Integer value){
			this.data.add(value);
		}
	}

	static class TerrainHeight {
		/**
		 * Height data count
		 */
		int count;

		/**
		 * Height data count for X
		 */
		short width;

		/**
		 * Height data count for Y
		 */
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

		// Default UE3 terrain scale within editor
		this.scale3d = new Vector3d(256d, 256d, 256d);
	}

	@Override
	public boolean analyseT3DData(String line) {

		// TerrainMap=Texture'myLevel.Package0.TowerHeightMap'
		if (line.startsWith("End " + isReading) || line.startsWith("End Object")) {

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
		/**
		 *             Count=3
		 *             Begin AlphaMap Index=0 Count=441
		 *                219	255	255	255	255	234	121	255
		 */
		else if(line.startsWith("Begin TerrainAlphaMapData")){
			isReading = "TerrainAlphaMapData";
		}
		//  Begin TerrainLayer Index=0 Name=BaseDirtSand
		else if(line.startsWith("Begin TerrainLayer ")){
			isReading = "TerrainLayer";

			final TerrainLayer tl = new TerrainLayer(mapConverter);
			tl.setName(T3DUtils.getString(line, "Name"));
			tl.setIndex(Integer.parseInt(T3DUtils.getString(line, "Index", " ")));
			terrainLayers.add(tl);
		} else if ("TerrainLayer".equals(isReading)) {
			if(line.startsWith("AlphaMapIndex")) {
				terrainLayers.get(terrainLayers.size() - 1).setAlphaMapIndex(T3DUtils.getInteger(line));
			}
			// Begin TerrainLayerSetup Name=UN_Terrain2.Sandstorm.TLS_UN_Terrain_SandStorm_Ground
			else if (line.startsWith("Begin TerrainLayerSetup")) {
				terrainLayers.get(terrainLayers.size() - 1).setTerrainLayerSetupName(line.split("Name=")[1]);
			}
		}
		else if ("TerrainAlphaMapData".equals(isReading)) {

			// Begin AlphaMap Index=0 Count=6561
			if (line.startsWith("Begin")) {
				alphaLayers.add(new LinkedList<>());
			}

			if(!line.startsWith("Count") && !line.startsWith("Begin") && !line.startsWith("End")){
				// 219	255	255	255	255	234	121	255
				final List<Integer> alphaValues = alphaLayers.get(alphaLayers.size() -1);
				alphaValues.addAll(Arrays.stream(line.replaceAll(" ", "").split("\t")).map(Integer::valueOf).collect(Collectors.toList()));
			}
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
			if (!line.startsWith("Count")) {
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
			} else if(line.startsWith("MaxTesselationLevel")){
				this.terrainActorMembers.maxTesselationLevel = T3DUtils.getInteger(line);
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

	public TerrainActorMembers getTerrainActorMembers() {
		return terrainActorMembers;
	}

	@Override
	public void convert() {

		if (mapConverter.isTo(UnrealEngine.UE4)) {
			T3DUE4Terrain ue4Terrain = new T3DUE4Terrain(this);
			replaceWith(ue4Terrain);
		}
	}

	public List<List<Integer>> getAlphaLayers() {
		return alphaLayers;
	}

	public List<TerrainLayer> getTerrainLayers() {
		return terrainLayers;
	}

	@Override
	public boolean isValidConverting() {
		// FIXME converter does not support yet large terrain size
		if (Math.max(this.getTerrainHeight().getHeight(), this.getTerrainHeight().getWidth()) > 255) {
			mapConverter.getLogger().log(Level.SEVERE, "Terrain conversion with size greater than 256X256 not supported");
			return false;
		} else {
			return super.isValidConverting();
		}
	}

	@Override
	public String toT3d() {
		return null;
	}
}
