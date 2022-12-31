package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class SmokeGenerator extends Effects {

    public SmokeGenerator(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("BasePuffSize", Float.class);
        registerSimpleProperty("bRepeating", Boolean.class);
        registerSimpleProperty("GenerationType", String.class);
        registerSimpleProperty("TotalNumPuffs", Integer.class);
        registerSimpleProperty("SmokeDelay", Float.class);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBSmokeGenerator_C";
    }

    public String toT3d() {
        return writeSimpleActor("UBSmokeGenerator_C");
    }
}
