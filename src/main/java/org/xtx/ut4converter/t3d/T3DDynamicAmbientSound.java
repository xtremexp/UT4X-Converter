package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;


public class T3DDynamicAmbientSound extends T3DKeyPoint {

    public T3DDynamicAmbientSound(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bDontRepeat", Boolean.class);
        registerSimpleProperty("bInitiallyOn", Boolean.class);
        registerSimpleProperty("maxRecheckTime", Float.class, 10f);
        registerSimpleProperty("minRecheckTime", Float.class, 5f);
        registerSimpleProperty("playProbability", Float.class, 0.6f);
        registerSimpleArrayPropertyRessource("Sounds", T3DRessource.Type.SOUND);
        registerSimpleArrayPropertyRessource("SoundSlots", T3DRessource.Type.SOUND);
    }

    public String toT3d() {
        return writeSimpleActor("UDynamicAmbientSound_C", "SceneComponent", "DefaultSceneRoot", "UDynamicAmbientSound_C'/Game/UEActors/UDynamicAmbientSound.Default__UDynamicAmbientSound_C'");
    }
}
