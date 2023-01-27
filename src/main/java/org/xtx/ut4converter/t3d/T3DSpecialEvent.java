package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * Special event trigger in Unreal Engine 1 (Unreal 1)
 */
public class T3DSpecialEvent extends T3DActor {


    enum InitialState {
        PlayerPath, PlayAmbientSoundEffect, PlayersPlaySoundEffect, PlaySoundEffect, KillInstigator, DamageInstigator, DisplayMessage, None
    }

    public T3DSpecialEvent(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bBroadcast", Boolean.class, false);
        registerSimpleProperty("bPlayerViewRot", Boolean.class);
        registerSimpleProperty("Damage", Float.class, 0);
        registerSimpleProperty("DamageString", String.class);
        registerSimpleProperty("DamageType", String.class);
        registerSimpleProperty("Message", String.class);
        registerSimpleProperty("InitialState", InitialState.class);
        registerSimplePropertyRessource("Sound", T3DRessource.Type.SOUND);
    }

    public String toT3d() {
        return writeSimpleActor("USpecialEvent_C", "BillboardComponent", "Billboard", "USpecialEvent_C'/Game/UEActors/USpecialEvent.Default__USpecialEvent_C'");
    }
}
