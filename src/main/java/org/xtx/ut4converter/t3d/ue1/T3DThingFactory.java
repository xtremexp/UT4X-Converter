package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DSimpleProperty;

import java.util.ArrayList;
import java.util.List;

import static org.xtx.ut4converter.t3d.T3DDecoration.UE4_DECO_FOLDER;
import static org.xtx.ut4converter.t3d.ue1.T3DThingFactory.EDistribution.DIST_Constant;

public class T3DThingFactory extends T3DActor {


    enum EDistribution {
        DIST_Constant,
        DIST_Uniform,
        DIST_Gaussian
    }

    public T3DThingFactory(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        propsToRegister().forEach(p -> this.getRegisteredProperties().add(p));
    }

    public static List<T3DSimpleProperty> propsToRegister() {

        final List<T3DSimpleProperty> props = new ArrayList<>();
        props.add(new T3DSimpleProperty("prototype", String.class, null, false).clonePropertyAs("prototypeActor"));
        props.add(new T3DSimpleProperty("maxitems", Integer.class, 1, false));
        props.add(new T3DSimpleProperty("capacity", Integer.class, 1000000, false));
        props.add(new T3DSimpleProperty("interval", Float.class, 1f, false));
        props.add(new T3DSimpleProperty("itemtag", String.class, false, false));
        props.add(new T3DSimpleProperty("bFalling", Boolean.class, true, false));
        props.add(new T3DSimpleProperty("timeDistribution", EDistribution.class, DIST_Constant, false));
        props.add(new T3DSimpleProperty("bOnlyPlayerTouched", Boolean.class, false, false));
        props.add(new T3DSimpleProperty("bCovert", Boolean.class, false, false));
        props.add(new T3DSimpleProperty("bStoppable", Boolean.class, false, false));

        return props;
    }

    @Override
    public String toT3d() {
        return writeSimpleActor("UThingFactory_C", "BillboardComponent", "Billboard", "UThingFactory_C'/Game/UEActors/UThingFactory.Default__UThingFactory_C'");
    }
}
