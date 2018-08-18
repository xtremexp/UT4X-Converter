package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;
import org.xtx.ut4converter.t3d.T3DSound;

public abstract class Effects extends T3DSound {
    public Effects(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimplePropertyRessource("EffectSound1", T3DRessource.Type.SOUND);
        registerSimplePropertyRessource("EffectSound2", T3DRessource.Type.SOUND);
        registerSimpleProperty("bOnlyTriggerable", Boolean.class);
    }
}
