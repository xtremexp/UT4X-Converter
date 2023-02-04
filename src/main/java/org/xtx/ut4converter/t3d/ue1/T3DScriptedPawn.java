package org.xtx.ut4converter.t3d.ue1;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DRessource;

import javax.vecmath.Vector3d;

import static org.xtx.ut4converter.t3d.T3DDecoration.UE4_DECO_FOLDER;

public class T3DScriptedPawn extends T3DPawn {

    public T3DScriptedPawn(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("Orders", "Orders", String.class, null);
        registerSimpleProperty("OrderTag", "Orders", String.class, null);
        registerSimpleProperty("WalkingSpeed", "Movement", Float.class, 0.4f).setScalable(true);

        // event when first hate player
        registerSimpleProperty("TeamTag", "AI", String.class, null);
        registerSimpleProperty("FirstHatePlayerEvent", "AI", String.class, null);
        registerSimpleProperty("bQuiet", "AI", Boolean.class, false);
        registerSimpleProperty("bTeamLeader", "AI", Boolean.class, false);
        // don't react to friend's noises (and make their enemy yours)
        registerSimpleProperty("bIgnoreFriends", "AI", Boolean.class, false);
        registerSimpleProperty("bHateWhenTriggered", "AI", Boolean.class, false);
        registerSimpleProperty("bDelayedPatrol", "AI", Boolean.class, false);


        registerSimpleProperty("TimeBetweenAttacks", "Combat", Float.class, 1f);
        registerSimpleProperty("Aggressiveness", "Combat", Float.class, 0f);
        registerSimpleProperty("ReFireRate", "Combat", String.class, 0f);
        // always takes hit
        registerSimpleProperty("bIsWuss", "Combat", Boolean.class, false);
        registerSimpleProperty("bLeadTarget", "Combat", Boolean.class, true);
        // warn target when projectile attack
        registerSimpleProperty("bWarnTarget", "Combat", Boolean.class, true);
        registerSimpleProperty("RangedProjectile", "Combat", String.class, null);
        registerSimpleProperty("ProjectileSpeed", "Combat", Float.class, 800f).setScalable(true);
        registerSimpleProperty("bHasRangedAttack", "Combat", Boolean.class, false);
        registerSimpleProperty("bMovingRangedAttack", "Combat", Boolean.class, false);

        // Headshot detection.
        registerSimpleProperty("HeadOffset", "HeadRegion", Vector3d.class, new Vector3d(0d, 0d, 0d)).setScalable(true);
        registerSimpleProperty("HeadRadius", "HeadRegion", Integer.class, 0).setScalable(true);

        registerSimplePropertyRessource("Acquire", "Sounds", T3DRessource.Type.SOUND, null);
        registerSimplePropertyRessource("Fear", "Sounds", T3DRessource.Type.SOUND, null);
        registerSimplePropertyRessource("Roam", "Sounds", T3DRessource.Type.SOUND, null);
        registerSimplePropertyRessource("Threaten", "Sounds", T3DRessource.Type.SOUND, null);


        // Tentacle
        registerSimpleProperty("WhipDamage", Integer.class, 14);

        classToSMRef.put("SkaarWarrior", "Skaarjw_BREATH");
        classToSMRef.put("Tentacle", "Tentacle1_Fighter");
        classToSMRef.put("Brute", "Brute_Fighter");
        classToSMRef.put("LesserBrute", "LesserBrute_Fighter");
    }

    public String toT3d() {

        if (classToSMRef.containsKey(this.t3dClass)) {

            String sm = classToSMRef.get(this.t3dClass);
            this.convProperties.put("StaticMesh", "StaticMesh'" + UE4_DECO_FOLDER + sm + "." + sm + "'");
        }


        return writeSimpleActor("UScriptedPawn_C", "BillboardComponent", "Billboard", "UScriptedPawn_C'/Game/UEActors/UScriptedPawn.Default__UScriptedPawn_C'");
    }
}
