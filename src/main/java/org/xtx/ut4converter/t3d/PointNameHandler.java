package org.xtx.ut4converter.t3d;

public class PointNameHandler implements LineDataHandler{
    private String name;

    @Override
    public boolean analyseT3DData(String line) {
        this.name = T3DUtils.getString(line);
        return true;
    }
}
