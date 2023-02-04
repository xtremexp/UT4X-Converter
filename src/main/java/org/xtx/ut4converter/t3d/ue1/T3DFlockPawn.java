package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;

import static org.xtx.ut4converter.t3d.T3DDecoration.UE4_DECO_FOLDER;

public class T3DFlockPawn extends T3DPawn {


    public T3DFlockPawn(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        // Bird
        registerSimpleProperty("CircleRadius", Float.class, 500f).setScalable(true);
        registerSimpleProperty("bCircle", Boolean.class, false);
        registerSimpleProperty("GoalTag", String.class);

        classToSMRef.put("bird1", "Bird_Flight");
    }

    public String toT3d() {

        if (classToSMRef.containsKey(this.t3dClass)) {

            String sm = classToSMRef.get(this.t3dClass);
            this.convProperties.put("StaticMesh", "StaticMesh'" + UE4_DECO_FOLDER + sm + "." + sm + "'");
        }

        return writeSimpleActor("UFlockPawn_C", "BillboardComponent", "Billboard", "UFlockPawn_C'/Game/UEActors/UFlockPawn.Default__UFlockPawn_C'");
    }
}
