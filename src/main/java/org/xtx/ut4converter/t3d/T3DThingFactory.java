package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DThingFactory extends T3DKeyPoint {

    public T3DThingFactory(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        // if bCovert = true and player could see monster being spawn
        // then it must not spawn the monster
        // only do hidden spawns
        registerSimpleProperty("bCovert", Boolean.class);

        // should set monster physics to falling on spawn (maybe to get it to the floor)
        registerSimpleProperty("bFalling", Boolean.class);

        //only player can trigger it
        registerSimpleProperty("bOnlyPlayerTouched", Boolean.class);

        //stops producing when untouched
        registerSimpleProperty("bStoppable", Boolean.class);

        // number of monsters spawned
        registerSimpleProperty("Capacity", Short.class);

        // Called once all items has been spawned and destroyed.
        registerSimpleProperty("FinishedEvent", String.class);

        // interval each monster is spawned
        registerSimpleProperty("Interval", Float.class);

        // tag of the monster spawned
        registerSimpleProperty("ItemTag", String.class);
        registerSimpleProperty("MaxItems", String.class);
        registerSimplePropertyRessource("Prototype", T3DRessource.Type.MESH).clonePropertyAs("PrototypeOriginal");
        registerSimpleProperty("TimeDistribution", String.class);

    }

    enum TimeDistribution {
        DIST_Constant, DIST_Uniform, DIST_Gaussian
    }

    public String toT3d() {
        return writeSimpleActor("UBThingFactory_C");
    }
}
