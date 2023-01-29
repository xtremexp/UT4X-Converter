package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * More advanced implementation of Unreal Engine 1 Trigger
 * using our better implementation Trigger blueprint
 * compared to {@link T3DTriggerVolume}
 */
public class T3DUE1Trigger extends T3DSound {

    public T3DUE1Trigger(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bInitiallyActive", Boolean.class);
        registerSimpleProperty("bTriggerOnceOnly", Boolean.class);
        registerSimpleProperty("ClassProximityType", String.class);
        registerSimpleProperty("DamageThreshold", Float.class);
        registerSimpleProperty("Message", String.class);
        registerSimpleProperty("RepeatTriggerTime", Float.class);
        registerSimpleProperty("ReTriggerDelay", Float.class);
        registerSimpleProperty("TriggerType", TriggerType.class, TriggerType.TT_PlayerProximity);
        registerSimpleProperty("InitialState", InitialState.class, InitialState.NormalTrigger);}

    enum TriggerType{
        /**
         * Can be triggered by players only
         */
        TT_PlayerProximity,
        /**
         * Can be triggered by monsters only
         */
        TT_PawnProximity,
        /**
         * Trigger by any specific class actor
         */
        TT_ClassProximity,
        /**
         * Can be triggered by either monsters or players
         */
        TT_AnyProximity,
        /**
         * Triggered shotting at it
         */
        TT_Shoot,
        /**
         * Unreal 2 specific - Triggered when pressing key 'E'
         */
        TT_Use
    }

    /**
     * Default is NormalTrigger
     */
    enum InitialState{
        None, OtherTriggerTurnsOff, OtherTriggerTurnsOn, OtherTriggerToggles, NormalTrigger
    }


    public String toT3d() {

        sbf.append(IDT).append("Begin Actor Class=UTriggerNew_C Archetype=UTrigger_C'/Game/UEActors/UTriggerNew.Default__UTriggerNew_C'\n");

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

        writeSimplePropertiesOld();

        sbf.append(IDT).append("\tRootComponent=CollisionComp\n");

        writeEndActor();

        return super.toString();
    }
}
