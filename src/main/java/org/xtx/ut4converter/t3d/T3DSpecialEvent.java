package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * Special event trigger in Unreal Engine 1 (Unreal 1)
 */
public class T3DSpecialEvent extends T3DActor {

    public T3DSpecialEvent(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bBroadcast", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bPlayerViewRot", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("Damage", Float.class, 0f);
        registerSimpleProperty("DamageString", String.class);
        registerSimpleProperty("DamageType", String.class);
        registerSimpleProperty("Message", String.class);
        registerSimplePropertyRessource("Sound", T3DRessource.Type.SOUND);
    }

    public String toT3d() {

        sbf.append(IDT).append("Begin Actor Class=Dispatcher_C \n");

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");

        writeSimpleProperties();

        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

        writeEndActor();

        return super.toString();
    }
}
