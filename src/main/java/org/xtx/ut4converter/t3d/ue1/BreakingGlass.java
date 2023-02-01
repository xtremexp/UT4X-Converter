package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

public class BreakingGlass extends ExplodingWall {
    public BreakingGlass(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("Numparticles", Float.class, 16f);
        registerSimpleProperty("ParticleSize", Float.class, 0.75f);
    }


    public String toT3d() {
        return writeSimpleActor("UBreakingGlass_C", "SceneComponent", "DefaultSceneRoot", "UBreakingGlass_C'/Game/UEActors/UBreakingGlass.Default__UBreakingGlass_C'");
    }
}
