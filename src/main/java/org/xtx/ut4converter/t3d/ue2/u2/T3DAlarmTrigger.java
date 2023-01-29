package org.xtx.ut4converter.t3d.ue2.u2;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DRessource;

public class T3DAlarmTrigger extends T3DActor {

    public T3DAlarmTrigger(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimplePropertyRessource("AlarmSound", T3DRessource.Type.SOUND);
        registerSimpleProperty("AlarmMessage", String.class);
        registerSimpleArrayProperty("AlarmMessages", String.class);
        registerSimpleProperty("bAmbient", Boolean.class, true);
        registerSimpleProperty("bToggleable", Boolean.class, true);
        registerSimpleProperty("bInitiallyOn", Boolean.class);
        registerSimpleProperty("bLetterboxText", Boolean.class);
        registerSimpleProperty("KeyBinding", String.class);
        registerSimpleProperty("bLetterboxText", Boolean.class);
        registerSimpleProperty("AlarmSoundVolume", Float.class);
        registerSimpleProperty("AlarmSoundNoOverride", Boolean.class, true);
        registerSimpleProperty("AlarmSoundRadius", Float.class, 0f);
        registerSimpleProperty("AlarmSoundPitch", Float.class, 1f);
    }

    public String toT3d() {
        return writeSimpleActor("U2AlarmTrigger_C", "BillboardComponent", "Billboard", "U2AlarmTrigger_C'/Game/UEActors/U2AlarmTrigge.Default__U2AlarmTrigger_C'");
    }
}
