/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DUE4Terrain;
import org.xtx.ut4converter.t3d.iface.T3D;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	/**
	 * Alpha layers
	 */
	private List<LandscapeComponentAlphaLayer> alphaLayers = new LinkedList<>();


	/**
	 * Creates a landscape component from collision component.
	 * Prior to UE4 there were no collision component specificaly so it's easy to create landscape component
	 *
	 * @param mc
	 * @param colComp Collision component
	 */
	public LandscapeComponent(final MapConverter mc, final T3DUE4Terrain t3DUE4Terrain, final LandscapeCollisionComponent colComp, final List<LandscapeComponentAlphaLayer> alphaLayers) {

		super(mc, t3DUE4Terrain, colComp.numComponent, colComp.getSizeQuads());

		this.alphaLayers = alphaLayers;
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

		/*
		for (final LandscapeComponentAlphaLayer alphaLayer : alphaLayers) {
			final int layerNum = alphaLayer.getLayerNum();
			alphaLayer.setLayerInfo(t3DUE4Terrain.getLayerNumToLayerInfo().get(layerNum));
		}*/

		initLayerInfoForLayers();
	}

	private void initLayerInfoForLayers() {

		// LayerNum=2 LayerInfo=/Game/RestrictedAssets/Environments/ShellResources/Materials/Loh/Grass_LayerInfo.Grass_LayerInfo 0 0 0 0 ff ff 0 0
		final String LI_1_GRASS = "/Game/RestrictedAssets/Environments/ShellResources/Materials/Loh/Grass_LayerInfo.Grass_LayerInfo";
		final String LI_2_DIRT = "/Game/RestrictedAssets/Environments/ShellResources/Materials/FortRun/FortRun_Dirt_LayerInfo.FortRun_Dirt_LayerInfo";
		final String LI_3_ROCK = "/Game/RestrictedAssets/Environments/Tuba/Landscape/Rock_2_LayerInfo.Rock_2_LayerInfo";
		final String LI_4_SAND1 = "/Game/RestrictedAssets/Environments/Fort/LandscapeLayers/Sand02_LayerInfo.Sand01_LayerInfo";
		final String LI_5_SAND2 = "/Game/RestrictedAssets/Environments/Fort/LandscapeLayers/Sand02_LayerInfo.Sand02_LayerInfo";
		final String LI_6_SAND3 = "/Game/RestrictedAssets/Environments/Fort/LandscapeLayers/Sand02_LayerInfo.Sand03_LayerInfo";

		// TODO add more layer info (some terrain has many alpha layers !)

		// for UT3 we can't guess the material used since it's embedded into either TerrainLayerSetup actor (no info within .t3d file)
		// for UT2004 we can TODO
		// so have to randomly set a layer info
		final Map<Integer, String> layerNumToLayerInfo = new HashMap<>();

		layerNumToLayerInfo.put(1, LI_1_GRASS);
		layerNumToLayerInfo.put(2, LI_2_DIRT);
		layerNumToLayerInfo.put(3, LI_3_ROCK);
		layerNumToLayerInfo.put(4, LI_4_SAND1);
		layerNumToLayerInfo.put(5, LI_5_SAND2);
		layerNumToLayerInfo.put(6, LI_6_SAND3);

		for (final LandscapeComponentAlphaLayer alphaLayer : alphaLayers) {
			final int layerNum = alphaLayer.getLayerNum();

			alphaLayer.setLayerInfo(layerNumToLayerInfo.getOrDefault(layerNum, LI_3_ROCK));
		}
	}


	public void setColisionComponent(LandscapeCollisionComponent colisionComponent) {
		this.colisionComponent = colisionComponent;
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

		// LayerWhitelist(0)=LandscapeLayerInfoObject'/Game/RestrictedAssets/Environments/ShellResources/Materials/Loh/Dirt_LayerInfo.Dirt_LayerInfo'
		int layerIdx = 0;

		for (final LandscapeComponentAlphaLayer alphaLayer : alphaLayers) {
			sb.append(base).append("\tLayerWhitelist(").append(layerIdx).append(")=LandscapeLayerInfoObject'").append(alphaLayer.getLayerInfo()).append("'\n");
			layerIdx++;
		}

		sb.append(base).append("\tAttachParent=RootComponent0\n");

		if (getSectionBaseX() > 0 || getSectionBaseY() > 0) {
			sb.append(base).append("\t").append(getT3dRelativeLocation()).append("\n");
		}


		sb.append(base).append("\tCustomProperties LandscapeHeightData");

		for (final int height : getHeightData()) {
			sb.append(" ").append(Integer.toHexString(height));
		}

		sb.append(" ");

		// write alpha layers data
		if (alphaLayers.isEmpty()) {
			sb.append("LayerNum=0 ");
		} else {
			for (final LandscapeComponentAlphaLayer alphaLayer : alphaLayers) {

				sb.append("LayerNum=").append(alphaLayer.getLayerNum()).append(" LayerInfo=").append(alphaLayer.getLayerInfo()).append(" ");

				// UE4 terrain has values in hexa (range: 0-> 255 - 0 -> FF)
				for (final Integer alphaValue : alphaLayer.getAlphaData()) {
					sb.append(Integer.toHexString(alphaValue)).append(" ");
				}
			}
		}

		sb.append("\n");

		sb.append(base).append("End Object\n");
	}

	@Override
	public String getName() {
		return "LandscapeComponent_" + numComponent;
	}

	public List<LandscapeComponentAlphaLayer> getAlphaLayers() {
		return alphaLayers;
	}
}
