package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * More advanced implementation of Unreal Engine 1 Trigger
 * using our better implementation Trigger blueprint
 * compared to {@link T3DTriggerVolume}
 */
public class T3DUE1Trigger extends T3DSound {

    private TriggerType triggerType;

    /**
     * Default is "NormalTrigger"
     */
    private InitialState initialState;

    public T3DUE1Trigger(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bInitiallyActive", Boolean.class);
        registerSimpleProperty("bTriggerOnceOnly", Boolean.class);
        registerSimpleProperty("ClassProximityType", String.class);
        registerSimpleProperty("DamageThreshold", Float.class);
        registerSimpleProperty("Message", String.class);
        registerSimpleProperty("RepeatTriggerTime", Float.class);
        registerSimpleProperty("ReTriggerDelay", Float.class);
    }

    enum TriggerType{
        TT_PlayerProximity, TT_PawnProximity, TT_ClassProximity, TT_AnyProximity, TT_Shoot,
        /**
         * Unreal 2 specific - Triggered when pressing key 'E'
         */
        TT_Use
    }

    enum InitialState{
        None, OtherTriggerTurnsOff, OtherTriggerTurnsOn, OtherTriggerToggles, NormalTrigger
    }

    @Override
    public boolean analyseT3DData(String line) {

        if (line.startsWith("TriggerType=")) {
            this.triggerType = TriggerType.valueOf(T3DUtils.getString(line));
        } else if (line.startsWith("InitialState=")) {
            this.initialState = InitialState.valueOf(T3DUtils.getString(line));
        } else {
            return super.analyseT3DData(line);
        }

        return false;
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

        if(this.triggerType != null){
            sbf.append(IDT).append("\tTriggerType=NewEnumerator").append(this.triggerType.ordinal()).append("\n");
        }

        if(this.initialState != null){
            sbf.append(IDT).append("\tInitialState=NewEnumerator").append(this.initialState.ordinal()).append("\n");
        }

        writeSimplePropertiesOld();

        sbf.append(IDT).append("\tRootComponent=CollisionComp\n");

        writeEndActor();

        return super.toString();
    }
}
