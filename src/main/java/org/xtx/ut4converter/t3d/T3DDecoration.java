package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

import java.util.HashMap;
import java.util.Map;

public class T3DDecoration extends T3DSound {

    protected final Map<String, String> classToSMRef = new HashMap<>();

    public static final String UE4_DECO_FOLDER = "/Game/UEActors/Decoration/";

    public T3DDecoration(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bOnlyTriggerable", Boolean.class);
        registerSimpleProperty("bPushable", Boolean.class);
        registerSimpleProperty("content2", String.class);
        registerSimpleProperty("content3", String.class);
        registerSimpleProperty("contents", String.class);
        registerSimpleProperty("EffectWhenDestroyed", String.class);
        registerSimpleProperty("AnimSequence", String.class);
        // TODO make rotator class and convert to UE4 range
        //registerSimpleProperty("RotationRate", Vector3d.class);
        registerSimplePropertyRessource("EndPushSound", T3DRessource.Type.SOUND);
        registerSimplePropertyRessource("PushSound", T3DRessource.Type.SOUND);
        registerSimplePropertyRessource("Skin", T3DRessource.Type.TEXTURE);
    }


    public String toT3d() {

        this.convProperties.put("StaticMesh", "StaticMesh'" + UE4_DECO_FOLDER + this.t3dClass + "M." + this.t3dClass + "M'");

        String decoT3d = writeSimpleActor("UDecoration_C", "BillboardComponent", "Billboard", "UDecoration_C'/Game/UEActors/UDecoration.Default__UDecoration_C'");

        // don't replicate this property to super T3DSound actor
        this.convProperties.remove("StaticMesh");

        // deco can also have sound actor associated (e.g: Fan)
        return decoT3d + super.toT3d();
    }
}
