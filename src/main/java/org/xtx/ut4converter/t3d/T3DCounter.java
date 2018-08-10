package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

public class T3DCounter extends T3DSound{
    public T3DCounter(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bShowMessage", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("CompleteMessage", String.class, "Completed!");
        registerSimpleProperty("CountMessage", String.class, "Only %i more to go...");
        registerSimpleProperty("NumToCount", Integer.class, 0);
    }

    @Override
    public String toString() {
        return writeSimpleActor("UBCounter_C");
    }
}
