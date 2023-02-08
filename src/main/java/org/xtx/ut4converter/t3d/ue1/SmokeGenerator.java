package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class SmokeGenerator extends Effects {

    public SmokeGenerator(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("BasePuffSize", Float.class, 1.75f).setScalable(true);
        registerSimpleProperty("bRepeating", Boolean.class, false);
        registerSimpleProperty("GenerationType", String.class, "Class'UnrealShare.SpriteSmokePuff'");
        registerSimpleProperty("TotalNumPuffs", Integer.class, 200);
        registerSimpleProperty("SmokeDelay", Float.class, 0.15f);
        registerSimpleProperty("SizeVariance", Float.class, 1f);
        registerSimpleProperty("RisingVelocity", Float.class, 75f).setScalable(true);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "USmokeGenerator_C";
    }

    public String toT3d() {
        return writeSimpleActor("USmokeGenerator_C", "BillboardComponent", "Billboard", "USmokeGenerator_C'/Game/UEActors/USmokeGenerator.Default__USmokeGenerator_C'");
    }
}
