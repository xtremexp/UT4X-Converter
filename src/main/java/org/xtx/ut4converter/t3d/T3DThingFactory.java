package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DThingFactory extends T3DKeyPoint {

    public T3DThingFactory(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        // if bCovert = true and player could see monster being spawn
        // then it must not spawn the monster
        // only do hidden spawns
        registerSimpleProperty("bCovert", Boolean.class, Boolean.TRUE);

        // should set monster physics to falling on spawn (maybe to get it to the floor)
        registerSimpleProperty("bFalling", Boolean.class, Boolean.TRUE);

        //only player can trigger it
        registerSimpleProperty("bOnlyPlayerTouched", Boolean.class, Boolean.FALSE);

        //stops producing when untouched
        registerSimpleProperty("bStoppable", Boolean.class, Boolean.FALSE);

        // number of monsters spawned
        registerSimpleProperty("Capacity", Short.class, 1);

        // Called once all items has been spawned and destroyed.
        registerSimpleProperty("FinishedEvent", String.class, null);

        // interval each monster is spawned
        registerSimpleProperty("Interval", Float.class, 1);

        // tag of the monster spawned
        registerSimpleProperty("ItemTag", String.class, null);
        registerSimpleProperty("MaxItems", String.class, 1);
        registerSimplePropertyRessource("Prototype", T3DRessource.Type.MESH).clonePropertyAs("PrototypeOriginal");
        registerSimpleProperty("TimeDistribution", String.class, TimeDistribution.DIST_Constant.name());

    }

    enum TimeDistribution {
        DIST_Constant, DIST_Uniform, DIST_Gaussian
    }

    @Override
    public String toString() {
        return writeSimpleActor("UBThingFactory_C");
    }
}
