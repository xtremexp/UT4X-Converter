package org.xtx.ut4converter.t3d.ue2.u2;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.T3DSound;

public class AlarmTrigger extends T3DSound {

    public AlarmTrigger(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimplePropertyRessource("AlarmSound", T3DRessource.Type.SOUND);
        registerSimpleProperty("AlarmMessage", String.class);
        registerSimpleArrayProperty("AlarmMessages", String.class);
        registerSimpleProperty("bAmbient", Boolean.class, Boolean.TRUE);
        registerSimpleProperty("bToggeable", Boolean.class, Boolean.TRUE);
    }

    @Override
    public String toString() {
        return writeSimpleActor("U2AlarmTrigger_C");
    }
}
