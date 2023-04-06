package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DDecoration;
import org.xtx.ut4converter.t3d.T3DSimpleProperty;

import java.util.HashMap;
import java.util.Map;

public class T3DCarcass extends T3DDecoration {


    final Map<String, String> defaultBaseSubMesh = new HashMap<>();

    //extract method
    public T3DCarcass(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);
        registerSimpleProp();
        smRefConversion();
        defaultConversion();
    }

    private void defaultConversion() {
        defaultBaseSubMesh.put("MaleBodyThree", "DeadMale3_Dead1");
        defaultBaseSubMesh.put("MaleBodyTwo", "DeadMale2_Dead1");
        defaultBaseSubMesh.put("MaleBody", "DeadMale1_Dead1");
        defaultBaseSubMesh.put("FemaleBody", "Fem1Body_Slump1");
    }

    private void smRefConversion() {
        classToSMRef.put("FemaleBody", "Fem1Body");
        classToSMRef.put("MaleBodyThree", "DeadMale3");
        classToSMRef.put("MaleBodyTwo", "DeadMale2");
        classToSMRef.put("MaleBody", "DeadMale1");
    }

    private void registerSimpleProp() {
        registerSimpleProperty("bReducedHeight", Boolean.class);
        registerSimpleProperty("flies", Integer.class, 0);
        registerSimpleProperty("rats", Integer.class, 0);
    }

    public String toT3d() {

        final T3DSimpleProperty animSeqSP = this.getRegisteredProperties().stream().filter(p -> "AnimSequence".equals(p.getPropertyName()) && p.getPropertyValue() != null).findAny().orElse(null);

        if (classToSMRef.containsKey(this.t3dClass)) {

            if (animSeqSP != null) {
                String animSeq = (String) animSeqSP.getPropertyValue();
                String sm = classToSMRef.get(this.t3dClass);
                this.convProperties.put("StaticMesh", "StaticMesh'" + UE4_DECO_FOLDER + sm + "_" + animSeq + "." + sm + "_" + animSeq + "'");
            } else {
                String sm = defaultBaseSubMesh.get(this.t3dClass);
                this.convProperties.put("StaticMesh", "StaticMesh'" + UE4_DECO_FOLDER + sm + "." + sm + "'");
            }
        }

        return writeSimpleActor("UCarcass_C", "BillboardComponent", "Billboard", "UCarcass_C'/Game/UEActors/UCarcass.Default__UCarcass_C'");
    }
}
