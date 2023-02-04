package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DNavigationPoint;
import org.xtx.ut4converter.t3d.T3DRessource;

public class T3DPatrolPoint extends T3DNavigationPoint {


    public T3DPatrolPoint(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        // Next point to go to
        registerSimpleProperty("Nextpatrol", String.class);

        // How long to pause here
        registerSimpleProperty("PauseTime", Float.class, 0f);

        // Animation to play while paused (if used it will ignores PauseTime).
        registerSimpleProperty("PatrolAnim", String.class);

        // Sound to play while paused.
        registerSimplePropertyRessource("PatrolSound", T3DRessource.Type.SOUND);

        // Number of times animation should play
        registerSimpleProperty("numAnims", Short.class, 0);
    }
    @Override
    public String toT3d() {
        return writeSimpleActor("UPatrolPoint_C", "BillboardComponent", "Billboard", "UPatrolPoint_C'/Game/UEActors/UPatrolPoint.Default__UPatrolPoint_C'");
    }
}
