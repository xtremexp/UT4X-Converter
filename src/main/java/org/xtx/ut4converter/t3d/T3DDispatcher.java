package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;


/**
 * Dispatcher trigger class as found in Unreal Engine 1
 */
public class T3DDispatcher extends T3DActor {


    public T3DDispatcher(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleArrayProperty("OutDelays", Float.class);
        registerSimpleArrayProperty("OutEvents", String.class);
    }

    public String toT3d() {
        return writeSimpleActor("UDispatcher_C", "BillboardComponent", "Billboard", "UDispatcher_C'/Game/UEActors/UDispatcher.Default__UDispatcher_C'");
    }
}
