package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DCounter extends T3DSound {
    public T3DCounter(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bShowMessage", Boolean.class);
        registerSimpleProperty("CompleteMessage", String.class);
        registerSimpleProperty("CountMessage", String.class);
        registerSimpleProperty("NumToCount", Integer.class);
    }

    public String toT3d() {
        return writeSimpleActor("UCounter_C", "BillboardComponent", "Billboard", "UCounter_C'/Game/UEActors/UCounter.Default__UCounter_C'");
    }
}
