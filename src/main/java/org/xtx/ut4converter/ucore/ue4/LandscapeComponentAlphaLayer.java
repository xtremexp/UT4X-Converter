package org.xtx.ut4converter.ucore.ue4;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class LandscapeComponentAlphaLayer {

    final private int layerNum;

    /**
     * Layer info material
     */
    private String layerInfo;

    /**
     * Alpha values within 0-255 range
     */
    private List<Integer> alphaData = new LinkedList<>();


    public LandscapeComponentAlphaLayer(int layerNum) {
        this.layerNum = layerNum;
    }

    public int getLayerNum() {
        return layerNum;
    }

    public List<Integer> getAlphaData() {
        return alphaData;
    }

    public void setAlphaData(List<Integer> alphaData) {
        this.alphaData = alphaData;
    }

    public String getLayerInfo() {
        return layerInfo;
    }

    public void setLayerInfo(String layerInfo) {
        this.layerInfo = layerInfo;
    }
}
