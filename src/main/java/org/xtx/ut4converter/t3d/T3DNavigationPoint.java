package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

import java.lang.reflect.Array;

public abstract class T3DNavigationPoint extends T3DActor {
    public T3DNavigationPoint(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        // Pawn should never use serpentine movement upon entering this path.
        registerSimpleProperty("bNoStrafeTo", Boolean.class);

        //reachspecs from this path only in the direction the path is facing (180 degrees)
        registerSimpleProperty("bOneWayPath", Boolean.class);

        // only players should use this path
        registerSimpleProperty("bPlayerOnly", Boolean.class);

        //added cost to visit this pathnode
        registerSimpleProperty("ExtraCost", Short.class, 0);

        // list of names or tags of NavigationPoints which should always be connected from this path
        registerSimpleProperty("ForcedPaths", Array.class);

        // When path is forced, use this as path size.
        registerSimpleProperty("ForcedPathSize", Integer.class, 150);

        // Maximum path distance (used in editor while binding paths).
        registerSimpleProperty("MaxPathDistance", Integer.class, 1000).setScalable(true);

        //creature clan owning this area (area visible from this point)
        registerSimpleProperty("OwnerTeam", String.class);

        // pointer to path description in zoneinfo LocationStrings array
        registerSimpleProperty("PathDescription", Short.class, 12);

        // list of names or tags of NavigationPoints which should never be connected from this path
        registerSimpleProperty("ProscribedPaths", Array.class);
    }
}
