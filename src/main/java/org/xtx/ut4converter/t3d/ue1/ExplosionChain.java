package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class ExplosionChain extends Effects {


    public ExplosionChain(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("Damage", Float.class, 100f);
        registerSimpleProperty("DelayTime", Float.class, 0.3f);
        registerSimpleProperty("MomentumTransfer", Float.class, 100000f);
        registerSimpleProperty("Size", Float.class, 1f);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBExplosionChain_C";
    }

    public String toT3d() {
        return writeSimpleActor("UBExplosionChain_C");
    }
}
