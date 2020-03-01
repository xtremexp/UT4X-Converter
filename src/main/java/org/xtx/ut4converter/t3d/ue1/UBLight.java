package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DLight;

public class UBLight extends T3DLight {
    /**
     * @param mc
     * @param t3dClass
     */
    public UBLight(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("LightPeriod", Integer.class, 32);
        registerSimpleProperty("LightPhase", Integer.class, 0);
    }

    @Override
    public void convert() {
        super.convert();

        if(this.getLightType() != null && this.getLightType() != UE12_LightType.LT_Steady && UE4_LightActor.PointLight.name().equals(this.t3dClass)) {
            // only apply special light if point light
            // because UBPointLight extends PointLight
            // TODO handle spotlight
            this.t3dClass = "UBPointLight_C";
        }
    }
}
