package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * CreatureFactory actor found in Unreal 1.
 * Allows to spawn X creatures (Prototype) at location of "SpawnPoint" actors
 * every Y (Interval) seconds
 */
public class T3DCreatureFactory extends T3DThingFactory {

    public T3DCreatureFactory(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        // Extra creatures for coop mode
        registerSimpleProperty("AddedCoopCapacity", Short.class, null);

        // alarmtag given to creatures from this factory
        registerSimpleProperty("AlarmTag", String.class, null);

        // creatures from this factory will have these orders
        registerSimpleProperty("Orders", String.class, "Attacking");
        registerSimpleProperty("OrderTag", String.class, null);
    }

    public String toT3D() {
        return writeSimpleActor("UBCreatureFactory_C");
    }
}
