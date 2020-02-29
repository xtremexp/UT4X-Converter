package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DDecoration;

public class Barrel extends T3DDecoration {
    public Barrel(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("Health", Integer.class, 10);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "U1Barrel_C";
    }

    @Override
    public void scale(Double newScale) {
        super.scale(newScale);
        // TODO check for scale != 2.5 (U1 default scale ratio)
        this.scale3d = null;
    }

    public String toT3d() {
        return writeSimpleActor("U1Barrel_C", "StaticMeshComponent");
    }
}
