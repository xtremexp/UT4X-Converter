package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * More advanced implementation of Unreal Engine 1 Trigger
 * using our better implementation Trigger blueprint
 * compared to {@link T3DTrigger}
 */
public class T3DUE1Trigger extends T3DSound {
    public T3DUE1Trigger(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bInitiallyActive", Boolean.class, Boolean.TRUE);
        registerSimpleProperty("bTriggerOnceOnly", Boolean.class, Boolean.FALSE);
        registerSimpleProperty("ClassProximityType", String.class, null);
        registerSimpleProperty("DamageThreshold", Float.class, 0f);
        registerSimpleProperty("Message", String.class, null);
        registerSimpleProperty("RepeatTriggerTime", Float.class, 0f);
        registerSimpleProperty("ReTriggerDelay", Float.class, 0f);
    }

    enum TriggerType{
        TT_PlayerProximity, TT_PawnProximity, TT_ClassProximity, TT_AnyProximity, TT_Shoot
    }

    @Override
    public String toString() {

        sbf.append(IDT).append("Begin Actor Class=UBTrigger_C \n");

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"CollisionComp\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tCollisionComponent=CollisionComp\n");

        if(collisionRadius != null){
            sbf.append(IDT).append("\tCollisionRadius=").append(collisionRadius).append("\n");
        }

        if(collisionHeight != null){
            sbf.append(IDT).append("\tCollisionHeight=").append(collisionHeight).append("\n");
        }

        writeSimpleProperties();

        sbf.append(IDT).append("\tRootComponent=CollisionComp\n");

        writeEndActor();

        return super.toString();
    }
}
