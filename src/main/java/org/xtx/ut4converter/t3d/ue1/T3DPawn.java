package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DActor;

import java.util.HashMap;
import java.util.Map;

import static org.xtx.ut4converter.t3d.T3DDecoration.UE4_DECO_FOLDER;

public class T3DPawn extends T3DActor {

    protected final Map<String, String> classToSMRef = new HashMap<>();

    public T3DPawn(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("GroundSpeed", Float.class, 320).setScalable(true);
        registerSimpleProperty("AccelRate", Float.class, 200).setScalable(true);
        registerSimpleProperty("AirSpeed", Float.class, 320).setScalable(true);
        registerSimpleProperty("JumpZ", Float.class, 325).setScalable(true);
        registerSimpleProperty("AlarmTag", String.class);

        // StaticMesh'/Game/UEActors/Decoration/Skaarjw_BREATH.Skaarjw_BREATH'
        classToSMRef.put("SkaarWarrior", "Skaarjw_BREATH");
    }


    public String toT3d() {

        if (classToSMRef.containsKey(this.t3dClass)) {

            String sm = classToSMRef.get(this.t3dClass);
            this.convProperties.put("StaticMesh", "StaticMesh'" + UE4_DECO_FOLDER + sm + "." + sm + "'");
        }


        return writeSimpleActor("UPawn_C", "BillboardComponent", "Billboard", "UPawn_C'/Game/UEActors/UPawn.Default__UPawn_C'");
    }
}
