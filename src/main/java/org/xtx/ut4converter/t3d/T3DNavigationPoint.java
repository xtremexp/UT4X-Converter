package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

import java.lang.reflect.Array;

abstract class T3DNavigationPoint extends T3DActor {
    T3DNavigationPoint(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        // Pawn should never use serpentine movement upon entering this path.
        registerSimpleProperty("bNoStrafeTo", Boolean.class);

        //reachspecs from this path only in the direction the path is facing (180 degrees)
        registerSimpleProperty("bOneWayPath", Boolean.class);

        // only players should use this path
        registerSimpleProperty("bPlayerOnly", Boolean.class);

        //added cost to visit this pathnode
        registerSimpleProperty("ExtraCost", Short.class);

        // list of names or tags of NavigationPoints which should always be connected from this path
        registerSimpleProperty("ForcedPaths", Array.class);

        // When path is forced, use this as path size.
        registerSimpleProperty("ForcedPathSize", Integer.class);

        // Maximum path distance (used in editor while binding paths).
        registerSimpleProperty("MaxPathDistance", Integer.class);

        //creature clan owning this area (area visible from this point)
        registerSimpleProperty("OwnerTeam", String.class);

        // pointer to path description in zoneinfo LocationStrings array
        registerSimpleProperty("PathDescription", Short.class);

        // list of names or tags of NavigationPoints which should never be connected from this path
        registerSimpleProperty("ProscribedPaths", Array.class);
    }
}
