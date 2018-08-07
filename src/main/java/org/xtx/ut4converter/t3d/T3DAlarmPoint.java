package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class T3DAlarmPoint extends T3DNavigationPoint {
    public T3DAlarmPoint(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("AlarmAnim", String.class, "Bowing");
        registerSimpleProperty("AlarmSound", UPackageRessource.class, null);
        registerSimpleProperty("bAttackWhilePaused", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bDestroyAlarmTriggerer", Boolean.class, Boolean.FALSE);

        // tells event triggered creatures to kill alarm triggerer, even if not normally hate
        registerSimpleProperty("bKillMe", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bNoFail", Boolean.class, Boolean.TRUE);
        registerSimpleProperty("bStopIfNoEnemy", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bStrafeTo", Boolean.class, Boolean.FALSE);

        //how long to pause after playing anim before starting attack while paused
        registerSimpleProperty("DuckTime", Float.class, 0f);

        //next point to go to
        registerSimpleProperty("NextAlarm", String.class, null);

        //how long to pause here
        registerSimpleProperty("PauseTime", Float.class, 2f);
        registerSimpleProperty("ShootTarget", String.class, null);
    }

    @Override
    public String toString() {
        return writeSimpleActor("AlarmPoint_C");
    }
}
