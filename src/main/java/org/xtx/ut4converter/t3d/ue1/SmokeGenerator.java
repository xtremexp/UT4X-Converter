package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class SmokeGenerator extends Effects {

    public SmokeGenerator(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("BasePuffSize", Float.class, 1.75f);
        registerSimpleProperty("bRepeating", Boolean.class);
        registerSimpleProperty("GenerationType", String.class, "UnrealShare.SpriteSmokePuff");
        registerSimpleProperty("TotalNumPuffs", Integer.class, 200);
        registerSimpleProperty("SmokeDelay", Float.class, 0.15f);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBExplosionChain_C";
    }

    @Override
    public String toString() {
        return writeSimpleActor("UBExplosionChain_C");
    }
}
