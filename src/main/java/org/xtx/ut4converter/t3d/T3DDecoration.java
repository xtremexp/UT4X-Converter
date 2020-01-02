package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DDecoration extends T3DStaticMesh {


    public T3DDecoration(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bOnlyTriggerable", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("bPushable", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("content2", String.class);
        registerSimpleProperty("content3", String.class);
        registerSimpleProperty("contents", String.class);
        registerSimpleProperty("EffectWhenDestroyed", String.class);
        registerSimplePropertyRessource("EndPushSound", T3DRessource.Type.SOUND);
        registerSimplePropertyRessource("PushSound", T3DRessource.Type.SOUND);
    }

    @Override
    public void convert() {
        // TODO extract mesh and convert them to staticmesh

        this.name += "_Type_" + t3dClass;

        super.convert();
    }

    @Override
    public String toString() {

        sbf.append(IDT).append("Begin Actor Class=UBDecoration_C").append(" Name=").append(name).append("\n");

        writeStaticMeshComponent();

        writeSimpleProperties();
        writeEndActor();

        return sbf.toString();
    }
}
