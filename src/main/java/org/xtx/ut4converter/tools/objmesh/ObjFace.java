package org.xtx.ut4converter.tools.objmesh;

public class ObjFace {

    /**
     *
     */
    private ObjMaterial material;

    private int smoothingGroup;

    /**
     *
     */
    private int vertex0Idx, idx1, vertex1Idx, idx3, vertex2Idx, idx5;

    public ObjMaterial getMaterial() {
        return material;
    }

    public int getVertex0Idx() {
        return vertex0Idx;
    }

    public int getIdx1() {
        return idx1;
    }

    public int getVertex1Idx() {
        return vertex1Idx;
    }

    public int getIdx3() {
        return idx3;
    }

    public int getVertex2Idx() {
        return vertex2Idx;
    }

    public int getIdx5() {
        return idx5;
    }

    public void setMaterial(ObjMaterial material) {
        this.material = material;
    }

    public void setVertex0Idx(int vertex0Idx) {
        this.vertex0Idx = vertex0Idx;
    }

    public void setIdx1(int idx1) {
        this.idx1 = idx1;
    }

    public void setVertex1Idx(int vertex1Idx) {
        this.vertex1Idx = vertex1Idx;
    }

    public void setIdx3(int idx3) {
        this.idx3 = idx3;
    }

    public void setVertex2Idx(int vertex2Idx) {
        this.vertex2Idx = vertex2Idx;
    }

    public void setIdx5(int idx5) {
        this.idx5 = idx5;
    }

    public int getSmoothingGroup() {
        return smoothingGroup;
    }

    public void setSmoothingGroup(int smoothingGroup) {
        this.smoothingGroup = smoothingGroup;
    }
}
