package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.ue1.T3DThingFactory;

/**
 * Class used to summon monsters at this actor location
 * via CreatureFactory of ThingFactory.
 * The only used property is the "Tag" binded with the factory.
 * Seen in UE1
 */
public class T3DSpawnPoint extends T3DNavigationPoint {


    public T3DSpawnPoint(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        T3DThingFactory.propsToRegister().forEach(p -> this.getRegisteredProperties().add(p));
    }

    @Override
    public String toT3d() {
        return writeSimpleActor("USpawnPoint_C", "BillboardComponent", "Billboard", "USpawnPoint_C'/Game/UEActors/USpawnPoint.Default__USpawnPoint_C'");
    }
}
