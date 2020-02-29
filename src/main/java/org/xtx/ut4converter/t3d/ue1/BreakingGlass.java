package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class BreakingGlass extends ExplodingWall {
    public BreakingGlass(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("Numparticles", Float.class, 13f);
        registerSimpleProperty("ParticleSize", Float.class, 0.75f);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBBreakingGlass_C";
    }

    public String toT3D() {
        return writeSimpleActor("UBBreakingGlass_C");
    }
}
