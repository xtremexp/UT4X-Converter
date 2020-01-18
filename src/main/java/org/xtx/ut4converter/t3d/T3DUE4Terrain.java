/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.ue4.LandscapeCollisionComponent;
import org.xtx.ut4converter.ucore.ue4.LandscapeComponent;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Very basic implementation of Unreal Engine 4 terrain
 * 
 * @author XtremeXp
 */
public class T3DUE4Terrain extends T3DActor {

	private final static String DEFAULT_LANDSCAPE_HOLE_MATERIAL = "/Game/RestrictedAssets/Maps/WIP/CTF-Maul-UT2004/Terrain_Vis_Mat.Terrain_Vis_Mat";

	UPackageRessource landscapeMaterial;

	UPackageRessource landscapeHoleMaterial;

	int collisionMipLevel;
	int collisionThickness;

	/**
	 * Max component size
	 */
	final int maxComponentSize = 255;

	int componentSizeQuads;
	int subsectionSizeQuads;
	short numSubsections;
	boolean bUsedForNavigation;
	short maxPaintedLayersPerComponent;

	private LandscapeCollisionComponent[][] collisionComponents;

	private LandscapeComponent[][] landscapeComponents;

	public T3DUE4Terrain(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		initialise();
	}

	/**
	 * Creates an Unreal Engine 4 terrain from Unreal Engine 3 terrain
	 *
	 * @param ue3Terrain Unreal Engine 4 terrain
	 */
	public T3DUE4Terrain(final T3DUE3Terrain ue3Terrain) {
		super(ue3Terrain.getMapConverter(), ue3Terrain.t3dClass);

		initialise();

		this.name = ue3Terrain.name;
		this.location = ue3Terrain.location;
		this.scale3d = ue3Terrain.scale3d;


		// compute the number of collision components from terrain size and max component size
		/**
		 * In our example:
		 * NumPatchesX=20
		 * NumPatchesY=20
		 * MaxComponentSize=4
		 *
		 * In UE3 a component size could have height and width different (SectionSizeX and SectionSizeY properties)
		 * in UE4 these properties have been replaced by ComponentSizeQuads so it's always a square so we need to compute the number of components needed for UE4 terrain
		 */
		// in UE4 compQuadSize is always either 7x7 or 15x15 or 31x31 or 63x63 or 127*127 or 255*255
		//int compQuadSize = ue3Terrain.getTerrainActorMembers().getMaxComponentSize() * ue3Terrain.getTerrainActorMembers().getMaxTesselationLevel();
		// FIXME global, UE3Terrain conversion buggy with multi component so as a temp fix we do only one big component that fits the whole square
		int compQuadSize = computeQuadSize(ue3Terrain.getTerrainHeight().getHeight(), ue3Terrain.getTerrainHeight().getWidth());

		// since collision data and landscape data are the same compSize and subSectionSize are the same
		this.componentSizeQuads = compQuadSize;
		this.subsectionSizeQuads = compQuadSize;
		this.numSubsections = 1;

		// Ceil(20 / (4*4)) = Ceil(1.25) = 2
		int nbCompX = (int) Math.ceil(ue3Terrain.getTerrainActorMembers().getNumPatchesX() * 1f / compQuadSize);

		// Ceil(20 / (4*4)) = Ceil(1.25) = 2
		int nbCompY = (int) Math.ceil(ue3Terrain.getTerrainActorMembers().getNumPatchesY() * 1f / compQuadSize);

		collisionComponents = new LandscapeCollisionComponent[nbCompX][nbCompY];
		landscapeComponents = new LandscapeComponent[nbCompX][nbCompY];

		final List<Integer> heightMap = ue3Terrain.getTerrainHeight().getHeightMap();
		final short terrainWidth = ue3Terrain.getTerrainHeight().getWidth();
		final short terrainHeight = ue3Terrain.getTerrainHeight().getHeight();

		buildLandscapeAndCollisionComponents(compQuadSize, nbCompX, nbCompY, heightMap, terrainWidth, terrainHeight);
	}

	/**
	 * Compute quadsize of UE4 terrain.
	 * in UE4 compQuadSize is always either 7x7 or 15x15 or 31x31 or 63x63 or 127*127 or 255*255
	 *
	 * @param terrainHeight
	 * @param terrainWidth
	 * @return
	 */
	private int computeQuadSize(final int terrainHeight, final int terrainWidth) {
		int compQuadSize = Math.max(terrainHeight, terrainWidth);

		// we fit to the best UE4 quadSize
		if (compQuadSize <= 7) {
			compQuadSize = 7;
		} else if (compQuadSize <= 15) {
			compQuadSize = 15;
		} else if (compQuadSize <= 31) {
			compQuadSize = 31;
		} else if (compQuadSize <= 127) {
			compQuadSize = 127;
		} else if (compQuadSize <= 255) {
			compQuadSize = 255;
		} else {
			// not supported
			compQuadSize = 255;
		}

		return compQuadSize;
	}


	private void buildLandscapeAndCollisionComponents(int compQuadSize, int nbCompX, int nbCompY, final List<Integer> heightMap, short terrainWidth, short terrainHeight) {

		// size of heightmap values for components
		int compHeightDataSize = (compQuadSize + 1) * (compQuadSize + 1);

		int compIdx = 0;
		// min terrain height will become the default value
		int minTerrainHeight = heightMap.stream().mapToInt(a -> a).min().orElse(32768);

		for (int compIdxX = 0; compIdxX < nbCompX; compIdxX++) {

			for (int compIdxY = 0; compIdxY < nbCompY; compIdxY++) {

				// create component
				final LandscapeCollisionComponent lcc = new LandscapeCollisionComponent(mapConverter, compIdx, compQuadSize);

				lcc.setSectionBaseX(compIdxX * compQuadSize);
				lcc.setSectionBaseY(compIdxY * compQuadSize);

				// fill up heighdata for this component
				for (int i = 0; i < compHeightDataSize; i++) {

					final Integer heightMatch = getHeightForComponentHeightIndex(i, heightMap, compQuadSize, compIdxX, compIdxY, terrainWidth, terrainHeight, minTerrainHeight);

					if (heightMatch != null) {
						lcc.getHeightData().add(heightMatch);
					} else {
						lcc.getHeightData().add(32768);
					}
				}

				collisionComponents[compIdxX][compIdxY] = lcc;
				final LandscapeComponent lc = new LandscapeComponent(mapConverter, lcc);
				lc.setName("LC_" + compIdx);
				landscapeComponents[compIdxX][compIdxY] = lc;

				lcc.setRenderComponent(lc);

				compIdx ++;
			}
		}
	}

	/**
	 * Given a component height index and the height data of original UE3 terrain, return the height
	 * related to this index within the UE4 terrain
	 *
	 * @param compHeightIdx Component height index
	 * @param ue3GlobalHeightData Global height data from UE3 terrain
	 * @param compQuadSize Quad Size for UE4 terrain
	 * @param compIdxX UE4 terrain component X coordinate
	 * @param compIdxY UE4 terrain component Y coordinate
	 * @param ue3GlobalWidth UE3 terrain width (might be different from the new UE4 terrain width due to QuadSize list restrictions)
	 * @param defaultTerrainHeight Default terrain height to set
	 * @return
	 */
	private Integer getHeightForComponentHeightIndex(int compHeightIdx, final List<Integer> ue3GlobalHeightData, int compQuadSize, int compIdxX, int compIdxY, int ue3GlobalWidth, int ue3GlobalHeight, int defaultTerrainHeight) {

		// TODO FIXME not working since the component size might be different from UE3 one
		// let's say in UE3 our terrain was a 21x21 square (ue3GlobalWidth)
		// and our UE4 converted terrain a 31*31 square
		// some heightvalues will be out of the UE3 terrain and will be needed to be set to default value (32768)

		// local coordinates within the UE4 component of height index
		final Point2d compHeightIdxCoord = getCoordinatesForIndexInSquareSize(compHeightIdx, (compQuadSize + 1), 0);

		// global coordinates within the UE4 terrain of this height index
		final Point2d compHeightIdxGlobalCoord = new Point2d(compIdxX * (compQuadSize + 1) + compHeightIdxCoord.x, compIdxY * (compQuadSize + 1) + compHeightIdxCoord.y);

		// this point is outside the original UE3 square, set height map value to default terrain height
		if (compHeightIdxGlobalCoord.x > (ue3GlobalWidth - 1) || compHeightIdxGlobalCoord.y > (ue3GlobalHeight - 1)) {
			return defaultTerrainHeight;
		} else {
			int globalHeightDataIdx = compIdxX * (compQuadSize + 1) + compIdxY * (compQuadSize + 1) + compHeightIdx - (int)(compHeightIdxGlobalCoord.y * (compQuadSize - ue3GlobalWidth + 1));
			return ue3GlobalHeightData.get(globalHeightDataIdx);
		}
	}

	/**
	 * Given a square with width size, compute from a global index it's coordinates
	 * E.G: a square with widh = 10
	 * Coord(28) = (8, 2)
	 * // --- X --->
	 *  // - - - - - - - - - - (9)
	 *  // - - - - - - - - - - (19)
	 *  // - - - - - - - - * - (29)  (x = 8, y = 2)
	 *  // - - - - - - - - - - (39)
	 *  // - - - - - - - - - - (49)
	 *
	 * @param globalIndex
	 * @param width
	 * @param height
	 * @return
	 */
	public static Point2d getCoordinatesForIndexInSquareSize(final int globalIndex, final int width, final int height) {

		return new Point2d(globalIndex % width, (int) Math.floor((globalIndex * 1f) / width));
	}

	/**
	 * Creates t3d ue4 terrain from unreal engine 2 terrain
	 * 
	 * @param ue2Terrain
	 */
	public T3DUE4Terrain(final T3DUE2Terrain ue2Terrain) {
		super(ue2Terrain.getMapConverter(), ue2Terrain.t3dClass);

		initialise();

		this.name = ue2Terrain.name;
		this.location = ue2Terrain.location;
		this.scale3d = ue2Terrain.getTerrainScale();

		if (!ue2Terrain.getLayers().isEmpty()) {
			landscapeMaterial = ue2Terrain.getLayers().get(0).getTexture();
		}

		short ue2TerrainWidth = (short) (ue2Terrain.getHeightMapTextureDimensions().width);
		short ue2TerrainHeight = (short) (ue2Terrain.getHeightMapTextureDimensions().height);

		// E.G: 128X128 -> 127
		int compQuadSize = computeQuadSize(ue2TerrainHeight -1, ue2TerrainWidth - 1);
		this.componentSizeQuads = compQuadSize;
		this.subsectionSizeQuads = this.componentSizeQuads;
		this.numSubsections = 1;

		// E.G: 128 / (127 + 1) = 1
		int nbCompX = ue2Terrain.getHeightMapTextureDimensions().width / (componentSizeQuads + 1);
		int nbCompY = ue2Terrain.getHeightMapTextureDimensions().height / (componentSizeQuads + 1);

		collisionComponents = new LandscapeCollisionComponent[nbCompX][nbCompY];
		landscapeComponents = new LandscapeComponent[nbCompX][nbCompY];

		buildLandscapeAndCollisionComponents(compQuadSize, nbCompX, nbCompY, ue2Terrain.getHeightMap(), ue2TerrainWidth, ue2TerrainHeight);

		// TODO alpha maps
		// TODO visibility data

		// In Unreal Engine 2, terrain pivot is "centered"
		// unlike UE3/4, so need update location
		// FIXME location too high !
		if (this.location != null && this.scale3d != null) {

			double offsetX = (nbCompX * this.scale3d.x * this.componentSizeQuads) / 2;
			double offsetY = (nbCompY * this.scale3d.y * this.componentSizeQuads) / 2;
			double offsetZ = this.scale3d.z * 128;

			if (mapConverter.getScale() != null) {
				offsetX *= mapConverter.getScale();
				offsetY *= mapConverter.getScale();
				offsetZ *= mapConverter.getScale();
			}

			this.location.x -= (offsetX + 100);
			this.location.y -= (offsetY + 100);
			this.location.z += offsetZ;
		}
	}

	/**
	 * Convert visibility data from unreal engine 2 terrain. Adapted code from
	 * UT3 converter
	 * 
	 * e.g (UE2):
	 * "QuadVisibilityBitmap(0)=-65540, QuadVisibilityBitmap(0)=-1, ..." e.g:
	 * (UE4): "CustomProperties DominantLayerData fffffffffff...."
	 * 
	 * @param ue2Terrain
	 *            Terrain from unreal engine 2 ut game (ut2003, ut2004 or unreal
	 *            2)
	 */
	private List<Boolean> convertUe2Visibility(T3DUE2Terrain ue2Terrain) {

		List<Boolean> globalVisibility = new ArrayList<>();

		if (ue2Terrain.getQuadVisibilityBitmaps() == null || !ue2Terrain.getQuadVisibilityBitmaps().isEmpty()) {

			StringBuilder tmpRadix;
			Map<Integer, Long> visMap = ue2Terrain.getQuadVisibilityBitmaps();

			for (int i = 0; i < ue2Terrain.getTotalSquares() / 32; i++) {

				if (visMap != null && visMap.containsKey(i)) {

					Long visibility = visMap.get(i);

					tmpRadix = new StringBuilder();

					// -1 means 32 squares are rendered
					if (visibility == -1) {
						for (int j = 0; j < 32; j++) {
							globalVisibility.add(Boolean.TRUE);
						}
					} else {
						// e.g:
						// "-134217728"
						visibility++;

						// "-134217727" -> --111111111111111111111111111
						// (rendered squares in "reverse" order)
						String radix = Long.toString(visibility, 2);
						radix = radix.replaceAll("-", "");

						for (int k = 0; k < (32 - radix.length()); k++) {
							tmpRadix.append("0");
						}

						radix = tmpRadix + radix;

						for (int j = 31; j >= 0; j--) {
							globalVisibility.add(radix.charAt(j) == '1');
						}
					}

				} else {
					for (int j = 0; j < 32; j++) {
						globalVisibility.add(Boolean.TRUE);
					}
				}
			}
		}

		return globalVisibility;
	}

	private void initialise() {
		collisionThickness = 16;
		numSubsections = 1;
		bUsedForNavigation = true;
	}

	@Override
	public boolean isValidWriting() {

		return landscapeComponents.length > 0;
	}

	@Override
	public void convert() {

		if (landscapeMaterial != null) {
			landscapeMaterial.export(UTPackageExtractor.getExtractor(mapConverter, landscapeMaterial));
		}

		if (landscapeHoleMaterial != null) {
			landscapeHoleMaterial.export(UTPackageExtractor.getExtractor(mapConverter, landscapeHoleMaterial));
		}
	}

	@Override
	public String toString() {
		return toT3d();
	}

	public String toT3d() {

		sbf.append(IDT).append("Begin Actor Class=Landscape Name=").append(name).append("\n");

		for (int x = 0; x < collisionComponents.length; x++) {
			for (int y = 0; y < collisionComponents[0].length; y++) {
				sbf.append(IDT).append("\tBegin Object Class=LandscapeHeightfieldCollisionComponent Name=\"").append(collisionComponents[x][y].getName()).append("\"\n");
				sbf.append(IDT).append("\tEnd Object\n");
			}
		}

		for (int x = 0; x < landscapeComponents.length; x++) {
			for (int y = 0; y < landscapeComponents[0].length; y++) {
				sbf.append(IDT).append("\tBegin Object Class=LandscapeComponent Name=\"").append(landscapeComponents[x][y].getName()).append("\"\n");
				sbf.append(IDT).append("\tEnd Object\n");
			}
		}

		sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"RootComponent0\"\n");
		sbf.append(IDT).append("\tEnd Object\n");

		for (int x = 0; x < collisionComponents.length; x++) {
			for (int y = 0; y < collisionComponents[0].length; y++) {
				collisionComponents[x][y].toT3d(sbf, null);
			}
		}

		for (int x = 0; x < landscapeComponents.length; x++) {
			for (int y = 0; y < landscapeComponents[0].length; y++) {
				landscapeComponents[x][y].toT3d(sbf, null);
			}
		}

		sbf.append(IDT).append("\tBegin Object Name=\"RootComponent0\"\n");

		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		// needs a guid or else would crash on import
		sbf.append(IDT).append("\tLandscapeGuid=").append(T3DUtils.randomGuid()).append("\n");

		if (landscapeMaterial != null) {
			sbf.append(IDT).append("\tLandscapeMaterial=Material'").append(landscapeMaterial.getConvertedName(mapConverter)).append("'\n");
		}

		// TODO test for all col components at least one with vis data
		if (collisionComponents[0][0].getVisibilityData() != null) {
			// sbf.append(IDT).append("\tLandscapeHoleMaterial=Material'").append(landscapeMaterial.getConvertedName(mapConverter)).append("'\n");
			// TEMP thingy
			sbf.append(IDT).append("\tLandscapeHoleMaterial=Material'").append(DEFAULT_LANDSCAPE_HOLE_MATERIAL).append("'\n");
		}

		int idx = 0;

		for (int x = 0; x < landscapeComponents.length; x++) {
			for (int y = 0; y < landscapeComponents[0].length; y++) {
				sbf.append(IDT).append("\tLandscapeComponents(").append(idx).append(")=LandscapeComponent'").append(landscapeComponents[x][y].getName()).append("'\n");
				idx++;
			}
		}

		idx = 0;

		for (int x = 0; x < collisionComponents.length; x++) {
			for (int y = 0; y < collisionComponents[0].length; y++) {
				sbf.append(IDT).append("\tCollisionComponents(").append(idx).append(")=CollisionComponent'").append(collisionComponents[x][y].getName()).append("'\n");
				idx++;
			}
		}

		sbf.append(IDT).append("\tComponentSizeQuads=").append(componentSizeQuads).append("\n");
		sbf.append(IDT).append("\tSubsectionSizeQuads=").append(subsectionSizeQuads).append("\n");
		sbf.append(IDT).append("\tNumSubsections=").append(numSubsections).append("\n");
		sbf.append(IDT).append("\tRootComponent=RootComponent0\n");

		writeEndActor();

		return sbf.toString();
	}


	public LandscapeCollisionComponent[][] getCollisionComponents() {
		return collisionComponents;
	}

	public LandscapeComponent[][] getLandscapeComponents() {
		return landscapeComponents;
	}
}
