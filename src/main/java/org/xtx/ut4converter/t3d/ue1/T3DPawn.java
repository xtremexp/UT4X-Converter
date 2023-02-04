package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DActor;
import org.xtx.ut4converter.t3d.T3DRessource;

import java.util.HashMap;
import java.util.Map;


public abstract class T3DPawn extends T3DActor {

    enum EAttitude {
        ATTITUDE_Fear,		//will try to run away
        ATTITUDE_Hate,		// will attack enemy
        ATTITUDE_Frenzy,	//will attack anything, indiscriminately
        ATTITUDE_Threaten,	// animations, but no attack
        ATTITUDE_Ignore,
        ATTITUDE_Friendly,
        ATTITUDE_Follow
    }

    enum EIntelligence //important - order in increasing intelligence
    {
        BRAINS_NONE, //only reacts to immediate stimulus
        BRAINS_REPTILE, //follows to last seen position
        BRAINS_MAMMAL, //simple navigation (limited path length)
        BRAINS_HUMAN   //complex navigation, team coordination, use environment stuff (triggers, etc.)
    }

    enum EPawnSightCheck
    {
        SEE_PlayersOnly, // See bIsPlayer Pawns only
        SEE_All, // See all Pawns
        SEE_None // Don't perform any sight checks
    }


    protected final Map<String, String> classToSMRef = new HashMap<>();

    public T3DPawn(final MapConverter mc, final String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("bCanStrafe", "Combat", Boolean.class, false);
        registerSimpleProperty("bFixedStart", "Orders", Boolean.class, false);

        registerSimpleProperty("GroundSpeed", "Movement", Float.class, 320).setScalable(true);
        registerSimpleProperty("AccelRate", "Movement", Float.class, 200).setScalable(true);
        registerSimpleProperty("AirSpeed", "Movement", Float.class, 320).setScalable(true);
        registerSimpleProperty("JumpZ", "Movement", Float.class, 325).setScalable(true);
        registerSimpleProperty("AlarmTag", "Orders", String.class, null);

        // Maximum seeing distance.
        registerSimpleProperty("SightRadius", "AI", Float.class, 2500f).setScalable(true);

        //Cosine of limits of peripheral vision.
        registerSimpleProperty("PeripheralVision", "AI", Float.class, 0f).setScalable(true);

        //Minimum noise loudness for hearing
        registerSimpleProperty("HearingThreshold", "AI", Float.class, 0.3f).setScalable(true);

        // The distance pawn can see the hunting target with FindPathToward/bHunting.
        registerSimpleProperty("HuntOffDistance", "AI", Float.class, 0f).setScalable(true);

        registerSimpleProperty("DropWhenKilled", String.class);

        registerSimpleProperty("Skill", "AI", Float.class, 0f);
        registerSimpleProperty("Health", Integer.class, 100);
        registerSimpleProperty("AttitudeToPlayer", "AI", EAttitude.class, EAttitude.ATTITUDE_Hate);
        registerSimpleProperty("Intelligence", "AI", EIntelligence.class, EIntelligence.BRAINS_MAMMAL);
        registerSimpleProperty("SightCheckType", "AI", EPawnSightCheck.class, EPawnSightCheck.SEE_PlayersOnly);

        registerSimplePropertyRessource("HitSound1", "Sounds", T3DRessource.Type.SOUND, null);
        registerSimplePropertyRessource("HitSound2", "Sounds", T3DRessource.Type.SOUND, null);
        registerSimplePropertyRessource("Land", "Sounds", T3DRessource.Type.SOUND, "Sound'UnrealShare.Generic.Land1'");
        registerSimplePropertyRessource("Die", "Sounds", T3DRessource.Type.SOUND, null);
        registerSimplePropertyRessource("WaterStep", "Sounds", T3DRessource.Type.SOUND, "Sound'UnrealShare.Generic.LSplash'");
    }

}
