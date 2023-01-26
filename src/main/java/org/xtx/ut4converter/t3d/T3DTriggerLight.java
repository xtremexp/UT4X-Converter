package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DTriggerLight extends T3DLight {


    enum InitialStateTL {
        None, TriggerPound, TriggerControl, TriggerToggle, TriggerTurnsOff, TriggerTurnsOn
    }

    /**
     * @param mc Map converter instance
     * @param t3dClass T3d class
     */
    public T3DTriggerLight(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bInitiallyOn", Boolean.class);
        registerSimpleProperty("bDelayFullOn", Boolean.class);
        registerSimpleProperty("ChangeTime", Float.class);
        registerSimpleProperty("InitialState", InitialStateTL.class);
    }

    @Override
    public boolean analyseT3DData(String line) {
        return super.analyseT3DData(line);
    }

    @Override
    public void convert() {
        super.convert();
        this.t3dClass = "UTriggerLight_C";
        this.archetype = "UTriggerLight_C'/Game/UEActors/UTriggerLight.Default__UTriggerLight_C'";
        this.mobility = UE4_Mobility.Stationary;
    }
}
