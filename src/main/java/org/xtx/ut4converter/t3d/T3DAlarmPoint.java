package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DAlarmPoint extends T3DNavigationPoint {
    public T3DAlarmPoint(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("AlarmAnim", String.class);
        registerSimplePropertyRessource("AlarmSound", T3DRessource.Type.SOUND);
        registerSimpleProperty("bAttackWhilePaused", Boolean.class);
        registerSimpleProperty("bDestroyAlarmTriggerer", Boolean.class);

        // tells event triggered creatures to kill alarm triggerer, even if not normally hate
        registerSimpleProperty("bKillMe", Boolean.class);
        registerSimpleProperty("bNoFail", Boolean.class);
        registerSimpleProperty("bStopIfNoEnemy", Boolean.class);
        registerSimpleProperty("bStrafeTo", Boolean.class);

        //how long to pause after playing anim before starting attack while paused
        registerSimpleProperty("DuckTime", Float.class);

        //next point to go to
        registerSimpleProperty("NextAlarm", String.class);

        //how long to pause here
        registerSimpleProperty("PauseTime", Float.class);
        registerSimpleProperty("ShootTarget", String.class);
    }

    public String toT3d() {
        return writeSimpleActor("UAlarmPoint_C", "BillboardComponent", "Billboard", "UAlarmPoint_C'/Game/UEActors/UAlarmPoint.Default__UAlarmPoint_C'");
    }
}
