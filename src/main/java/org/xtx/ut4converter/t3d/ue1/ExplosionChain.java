package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class ExplosionChain extends Effects {


    public ExplosionChain(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("Damage", Float.class);
        registerSimpleProperty("DelayTime", Float.class);
        registerSimpleProperty("MomentumTransfer", Float.class);
        registerSimpleProperty("Size", Float.class);
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
