package io.github.xtremexp.ut4converter.t3d.ue1;

import io.github.xtremexp.ut4converter.MapConverter;
import io.github.xtremexp.ut4converter.t3d.T3DRessource;
import io.github.xtremexp.ut4converter.t3d.T3DSound;

public abstract class Effects extends T3DSound {
    public Effects(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimplePropertyRessource("EffectSound1", T3DRessource.Type.SOUND);
        registerSimplePropertyRessource("EffectSound2", T3DRessource.Type.SOUND);
        registerSimpleProperty("bOnlyTriggerable", Boolean.class);
    }
}
