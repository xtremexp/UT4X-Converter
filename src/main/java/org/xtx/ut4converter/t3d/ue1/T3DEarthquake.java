package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DActor;

public class T3DEarthquake extends T3DActor {


    public T3DEarthquake(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("magnitude", Float.class, 2000f);
        registerSimpleProperty("duration", Float.class, 5f);
        registerSimpleProperty("radius", Float.class, 300f).setScalable(true);
        registerSimpleProperty("bThrowPlayer", Boolean.class, true);
    }

    @Override
    public String toT3d() {
        return writeSimpleActor("UEarthquake_C", "BillboardComponent", "Billboard", "UEarthquake_C'/Game/UEActors/UEarthquake.Default__UEarthquake_C'");
    }
}
