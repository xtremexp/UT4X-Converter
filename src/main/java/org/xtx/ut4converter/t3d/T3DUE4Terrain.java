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
		int compQuadSize = ue3Terrain.getTerrainActorMembers().getMaxComponentSize() * ue3Terrain.getTerrainActorMembers().getMaxTesselationLevel();

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

		int compIdx = 0;

		for (int compIdxX = 0; compIdxX < nbCompX; compIdxX++) {

			for (int compIdxY = 0; compIdxY < nbCompY; compIdxY++) {

				// create component
				final LandscapeCollisionComponent lcc = new LandscapeCollisionComponent(mapConverter, compIdx, compQuadSize);

				lcc.setSectionBaseX(compIdxX * compQuadSize);
				lcc.setSectionBaseY(compIdxY * compQuadSize);

				int heightSizeX = compQuadSize + 1;
				int heightSizeY = compQuadSize + 1;

				lcc.setHeightData(new int[heightSizeX][heightSizeY]);

				// fill up heighdata for this component
				for (int hmIdxX = 0; hmIdxX < heightSizeX; hmIdxX++) {
					for (int hmIdxY = 0; hmIdxY < heightSizeY; hmIdxY++) {
						// initialize with default value
						lcc.getHeightData()[hmIdxX][hmIdxY] = 32768;
					}
				}

				collisionComponents[compIdxX][compIdxY] = lcc;
				final LandscapeComponent lc = new LandscapeComponent(mapConverter, lcc, false);
				lc.setName("LC_" + compIdx);
				landscapeComponents[compIdxX][compIdxY] = lc;

				lcc.setRenderComponent(lc);

				compIdx ++;
			}
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

		return new Point2d(globalIndex % width, (int) Math.floor(globalIndex / width));
	}

	public static LandscapeCollisionComponent getCollisionComponentFromHeightMapIndex(final int hmIdx, final T3DUE3Terrain.TerrainHeight heightInfo, final List<LandscapeCollisionComponent> collisionComponentList){

		// imagine we have  a 51(width) X 81(height) heightmap (4131 values)
		// let's say we are looking at collisioncompoent which has the 1465th heightmap value (index)


		// global coordinates of this heightmap
		final Point2d globalCoord = getCoordinatesForIndexInSquareSize(hmIdx, heightInfo.getWidth(), heightInfo.getHeight());

		// so we are at row 1465/81 = 18
		int hmCoordX = hmIdx / heightInfo.getHeight();

		//and column 1465%81 = 7
		int hmCoordY = hmIdx%heightInfo.getHeight();

		return null;
	}

	/**
	 * Creates t3d ue4 terrain from unreal engine 2 terrain
	 * 
	 * @param ue2Terrain
	 */
	public T3DUE4Terrain(T3DUE2Terrain ue2Terrain) {
		super(ue2Terrain.getMapConverter(), ue2Terrain.t3dClass);

		initialise();

		this.name = ue2Terrain.name;
		this.location = ue2Terrain.location;
		this.scale3d = ue2Terrain.terrainScale;

		if (!ue2Terrain.layers.isEmpty()) {
			landscapeMaterial = ue2Terrain.layers.get(0).getTexture();
		}

		int numComponent = 0;

		this.componentSizeQuads = Math.min(Math.min(ue2Terrain.heightMapTextureDimensions.width, ue2Terrain.heightMapTextureDimensions.height - 1), maxComponentSize);
		this.subsectionSizeQuads = this.componentSizeQuads;

		int nbCompX = ue2Terrain.heightMapTextureDimensions.width / (componentSizeQuads + 1);
		int nbCompY = ue2Terrain.heightMapTextureDimensions.height / (componentSizeQuads + 1);

		LandscapeCollisionComponent collisionComponent = null;
		collisionComponents = new LandscapeCollisionComponent[nbCompX][nbCompY];
		landscapeComponents = new LandscapeComponent[nbCompX][nbCompY];

		int[][] localHeightCollisionData;

		// Local HeightMap idx in component
		int localHmXIdx = 0;
		int localHmYIdx = 0;

		// Index of component
		int compIdxX = 0;
		int compIdxY = 0;

		int[][] heightMap = new int[ue2Terrain.getHeightMap().length][ue2Terrain.getHeightMap()[0].length];

		// flip x/y
		// all these loops quite crappy but don't want rework the big loop yet
		for (int hmXIdx = 0; hmXIdx < ue2Terrain.getHeightMap().length; hmXIdx++) {

			for (int hmYIdx = 0; hmYIdx < ue2Terrain.getHeightMap()[0].length; hmYIdx++) {
				heightMap[hmXIdx][hmYIdx] = ue2Terrain.getHeightMap()[hmYIdx][hmXIdx];
			}
		}

		ue2Terrain.setHeightMap(heightMap);

		for (int hmXIdx = 0; hmXIdx < ue2Terrain.getHeightMap().length; hmXIdx++) {

			compIdxY = 0;

			for (int hmYIdx = 0; hmYIdx < ue2Terrain.getHeightMap()[0].length; hmYIdx++) {

				if (hmXIdx % componentSizeQuads == 0 && hmYIdx % componentSizeQuads == 0 && hmXIdx < componentSizeQuads && hmYIdx < componentSizeQuads) {

					localHmXIdx = 0;
					localHmYIdx = 0;

					collisionComponent = new LandscapeCollisionComponent(mapConverter, numComponent, componentSizeQuads);
					localHeightCollisionData = new int[componentSizeQuads + 1][componentSizeQuads + 1];

					collisionComponent.setSectionBaseX(compIdxX);
					collisionComponent.setSectionBaseY(compIdxY);

					collisionComponent.setHeightData(localHeightCollisionData);
					collisionComponents[compIdxX][compIdxY] = collisionComponent;
				}

				collisionComponent = collisionComponents[compIdxX][compIdxY];

				int heightMapVal = 0;

				if (hmXIdx % (componentSizeQuads + 1) == 0 && hmXIdx > 0) {
					heightMapVal = ue2Terrain.getHeightMap()[hmXIdx - 1][hmYIdx];
				} else if (hmYIdx % (componentSizeQuads + 1) == 0 && hmYIdx > 0) {
					heightMapVal = ue2Terrain.getHeightMap()[hmXIdx][hmYIdx - 1];
				} else {
					heightMapVal = ue2Terrain.getHeightMap()[hmXIdx][hmYIdx];
				}

				collisionComponent.getHeightData()[localHmXIdx][localHmYIdx] = heightMapVal / 2;

				if (hmYIdx % componentSizeQuads == 0) {
					if (hmYIdx > 0) {
						compIdxY++;
					}
					localHmYIdx = 0;
				}

				localHmYIdx++;

			}

			if (hmXIdx % componentSizeQuads == 0) {
				if (hmXIdx > 0) {
					compIdxX++;
				}
				localHmXIdx = 0;
			}

			localHmXIdx++;
		}

		// convert visibility data
		List<Boolean> visibilityData = convertUe2Visibility(ue2Terrain);

		// TODO put visibility data to right component if multiple ones
		collisionComponents[0][0].setVisibilityData(visibilityData);

		// create default landscape components from heightmap components
		for (int x = 0; x < collisionComponents.length; x++) {

			for (int y = 0; y < collisionComponents[0].length; y++) {
				LandscapeCollisionComponent colComponent = collisionComponents[x][y];
				landscapeComponents[x][y] = new LandscapeComponent(mapConverter, colComponent, true);

				colComponent.setRenderComponent(landscapeComponents[x][y]);
				landscapeComponents[x][y].setColisionComponent(colComponent);
			}
		}

		// In Unreal Engine 2, terrain pivot is "centered"
		// unlike UE3/4, so need update location
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
