package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DGradualMover extends T3DMover {

    public enum InitialState {
        GradualTriggerToggle, GradualTriggerPound, GradualTriggerOpenTimed, StandOpenTimed, BumpButton, BumpOpenTimed, ConstantLoop, TriggerPound, TriggerControl, TriggerToggle, TriggerOpenTimed, None
    }

    /**
     * @param mc Map converter instance
     * @param t3dClass T3d class
     */
    public T3DGradualMover(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleArrayProperty("CloseTimes", Float.class);
        registerSimpleArrayProperty("Events", String.class);
        registerSimpleArrayProperty("OpenTimes", Float.class);
        registerSimpleArrayProperty("Tags", String.class);
    }

    @Override
    public void convert() {
        super.convert();

        this.t3dClass = "UBGradualMover_C";
    }
}
