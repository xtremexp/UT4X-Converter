package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DTriggerLight extends T3DLight {


    private InitialStateTL initialState;

    enum InitialStateTL {
        None, TriggerPound, TriggerControl, TriggerToggle, TriggerTurnsOff, TriggerTurnsOn
    }

    /**
     * @param mc
     * @param t3dClass
     */
    public T3DTriggerLight(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bInitiallyOn", Boolean.class, Boolean.TRUE);
        registerSimpleProperty("bDelayFullOn", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("ChangeTime", Float.class, Boolean.FALSE);
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("InitialState=")) {
            this.initialState = InitialStateTL.valueOf(T3DUtils.getString(line));
        } else {
            return super.analyseT3DData(line);
        }

        return true;
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UBTriggerLight_C";
    }

    public InitialStateTL getInitialState() {
        return initialState;
    }
}
