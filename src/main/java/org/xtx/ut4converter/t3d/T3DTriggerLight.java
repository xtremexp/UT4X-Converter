package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DTriggerLight extends T3DLight {
    /**
     * @param mc
     * @param t3dClass
     */
    public T3DTriggerLight(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bInitiallyOn", Boolean.class, Boolean.TRUE);
        registerSimpleProperty("bDelayFullOn", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("ChangeTime", Float.class, Boolean.FALSE);
        registerSimpleProperty("InitialState", String.class, null);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBTriggerLight_C";
    }
}
