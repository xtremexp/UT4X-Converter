/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Tells which UT actor classes will be converted
 * 
 * @author XtremeXp
 */
public class SupportedClasses {

	private static final boolean USE_CUSTOM = true;
	
	MapConverter mapConverter;

	/**
	 * If in t3d
	 */
	protected HashMap<String, Class<? extends T3DActor>> classToUtActor = new HashMap<>();

	/**
	 * Actors that are no longer needed for output game. E.G: PathNodes in
	 * Unreal Engine 4 which no longer exists (using some navigation volume to
	 * border the "navigation area") Useful to avoid some spammy log message and
	 * create notes in editor for unconverted actors info
	 */
	protected List<String> uneededActors = new ArrayList<>();

	/**
	 *
	 * @param mapConverter
	 */
	public SupportedClasses(final MapConverter mapConverter) {
		this.mapConverter = mapConverter;
		initialize();
	}

	/**
	 *
	 * @param utxclass
	 */
	public void addClass(String utxclass) {
		classToUtActor.put(utxclass.toLowerCase(), null);
	}

	/**
	 *
	 * @param classNames
	 * @param utActorClass
	 */
	private void putUtClass(Class<? extends T3DActor> utActorClass, String... classNames) {
		if (classNames == null) {
			return;
		}

		for (String className : classNames) {
			classToUtActor.put(className.toLowerCase(), utActorClass);
		}
	}

	/**
	 * Tells
	 * 
	 * @param utClassName
	 * @return
	 */
	public boolean canBeConverted(String utClassName) {
		return classToUtActor.containsKey(utClassName.toLowerCase());
	}

	/**
	 * Get UT Actor class from t3d class name. Might return null if not special
	 * convert class
	 * 
	 * @param utClassName
	 * @return
	 */
	public Class<? extends T3DActor> getConvertActorClass(String utClassName) {
		return classToUtActor.get(utClassName.toLowerCase());
	}

	/**
	 * 
	 * @param utClassName
	 * @return
	 */
	public boolean noNotifyUnconverted(String utClassName) {
		return uneededActors.contains(utClassName);
	}

	private void addMatches(MapConverter mc) {
		HashMap<String, T3DMatch.Match> hm = mc.getActorClassMatch();

		for (String c : hm.keySet()) {
			putUtClass(hm.get(c).t3dClass, c);
		}
	}

	/**
	 * For testing purposes only. Converts only one actor so it's easier to
	 * import it in map being converted
	 * 
	 * @param actor
	 * @param t3dClass
	 */
	public void setConvertOnly(String actor, Class<? extends T3DActor> t3dClass) {
		classToUtActor.clear();
		classToUtActor.put(actor.toLowerCase(), t3dClass);
		mapConverter.setCreateNoteForUnconvertedActors(false);
	}

	/**
	 * TODO split depending on UE12/UE3 input engine
	 */
	private void initialize() {

		// TODO move zones to match with T3DZoneInfo.class
		putUtClass(T3DBrush.class, "Brush", "LavaZone", "WaterZone", "SlimeZone", "NitrogenZone", "PressureZone", "VacuumZone", "BlockAll");

        for (T3DBrush.BrushClass brushClass : T3DBrush.BrushClass.values()) {

            // mover is special case because is dependant of staticmesh for UE2
            // not brush (UE1)
            if (brushClass != T3DBrush.BrushClass.Mover) {
                putUtClass(T3DBrush.class, brushClass.name());
            }
        }

		putUtClass(mapConverter.isFrom(UnrealEngine.UE1) ? T3DMover.class : T3DMoverSM.class, "Mover", "AttachMover", "AssertMover", "RotatingMover", "ElevatorMover", "MixMover", "GradualMover",
				"LoopMover", "InterpActor");

		//putUtClass()
		putUtClass(T3DPlayerStart.class, "PlayerStart", "UTTeamPlayerStart", "UTWarfarePlayerStart");
		putUtClass(T3DStaticMesh.class, "StaticMeshActor");
		// TODO args param only
		final boolean useUbClasses = true;

		// SPECIFIC Unreal 1 conversion test
		if(mapConverter.isFrom(UnrealEngine.UE1) && useUbClasses) {
			putUtClass(T3DDispatcher.class, "Dispatcher");
			putUtClass(T3DSpecialEvent.class, "SpecialEvent");
			putUtClass(T3DMusicEvent.class, "MusicEvent");
			putUtClass(T3DTranslatorEvent.class, "TranslatorEvent");
			putUtClass(T3DCreatureFactory.class, "CreatureFactory");
			putUtClass(T3DSpawnPoint.class, "SpawnPoint");
			putUtClass(T3DAlarmPoint.class, "AlarmPoint");
			putUtClass(T3DCounter.class, "Counter");

			putUtClass(T3DUE1Trigger.class, "Trigger", "TeamTrigger", "ZoneTrigger", "TimedTrigger", "Trigger_ASTeam", "ScriptedTrigger", "VolumeTrigger", "MessageTrigger", "CrowdTrigger", "UseTrigger", "MusicTrigger", "RedirectionTrigger",
					"GravityTrigger", "MaterialTrigger", "TriggeredCondition");
		} else {
			putUtClass(T3DTrigger.class, "Trigger", "TeamTrigger", "ZoneTrigger", "TimedTrigger", "Trigger_ASTeam", "ScriptedTrigger", "VolumeTrigger", "MessageTrigger", "CrowdTrigger", "UseTrigger", "MusicTrigger", "RedirectionTrigger",
					"GravityTrigger", "MaterialTrigger", "TriggeredCondition");
		}



		if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {
			for (T3DLight.UE12_LightActors ut99LightActor : T3DLight.UE12_LightActors.values()) {

				if(useUbClasses && ut99LightActor == T3DLight.UE12_LightActors.TriggerLight){
					putUtClass(T3DTriggerLight.class, ut99LightActor.name());
				} else {
					putUtClass(T3DLight.class, ut99LightActor.name());
				}
			}
		} else if (mapConverter.isFrom(UnrealEngine.UE3)) {

			putUtClass(T3DNote.class, "Note");

			for (T3DSound.UE3_AmbientSoundActor ue3SoundActor : T3DSound.UE3_AmbientSoundActor.values()) {
				putUtClass(T3DSound.class, ue3SoundActor.name());
			}

			for (T3DLight.UE3_LightActor ue4LightActor : T3DLight.UE3_LightActor.values()) {
				putUtClass(T3DLight.class, ue4LightActor.name());
			}
		}

		putUtClass(T3DLevelInfo.class, "LevelInfo");

		if (mapConverter.isTo(UTGames.UnrealEngine.UE3, UTGames.UnrealEngine.UE4)) {
			// disabled until working good
			putUtClass(T3DZoneInfo.class, "ZoneInfo");
		}

		// terrain conversion disabled until working good
		if (mapConverter.isFrom(UTGames.UnrealEngine.UE2)) {
			putUtClass(T3DUE2Terrain.class, "TerrainInfo");
		}

		for (T3DLight.UE4_LightActor ue34LightActor : T3DLight.UE4_LightActor.values()) {
			putUtClass(T3DLight.class, ue34LightActor.name());
		}

		// UT99 Assault
		putUtClass(T3DASObjective.class, "FortStandard");
		putUtClass(T3DASCinematicCamera.class, "SpectatorCam");
		putUtClass(T3DASInfo.class, "AssaultInfo");

		// UT2004, UT99 Dom
		putUtClass(DomPoint.class, "xDomPointA", "xDomPointB", "ControlPoint");
		
		putUtClass(DefensePoint.class, "UTDefensePoint");

		// UT3
		putUtClass(DecalActor.class, "DecalActor");
		putUtClass(HeightFog.class, "HeightFog");

		putUtClass(T3DTeleporter.class, "Teleporter", "FavoritesTeleporter", "VisibleTeleporter", "UTTeleporter", "UTTeleporterCustomMesh");

		if(useUbClasses) {
			putUtClass(T3DSound.class, "AmbientSound", "AmbientSoundSimple");
			putUtClass(T3DDynamicAmbientSound.class, "DynamicAmbientSound");
		} else {
			putUtClass(T3DSound.class, "AmbientSound", "DynamicAmbientSound", "AmbientSoundSimple");
		}
		putUtClass(T3DLiftExit.class, "LiftExit", "UTJumpLiftExit");
		putUtClass(T3DJumpPad.class, "Kicker", "Jumper", "BaseJumpPad_C", "U2Kicker", "U2KickReflector", "xKicker", "UTJumppad");

		addMatches(mapConverter);

		uneededActors.add("PathNode");
		uneededActors.add("InventorySpot");
		uneededActors.add("TranslocDest");
		uneededActors.add("AntiPortalActor"); // ut2004
		uneededActors.add("Adrenaline"); // ut2004
		uneededActors.add("ReachSpec"); // ut2004
		uneededActors.add("ModelComponent"); // ut3
		uneededActors.add("ForcedReachSpec"); // ut3
		uneededActors.add("AdvancedReachSpec"); // ut3
		uneededActors.add("UTWeaponPickupLight"); // ut3
		uneededActors.add("UTArmorPickupLight"); // ut3
		uneededActors.add("UTHealthPickupLight"); // ut3
		uneededActors.add("ProscribedReachSpec"); // ut3

		if (USE_CUSTOM) {
			putUtClass(SpecialEvent.class, "SpecialEvent");
		}
	}

	/**
	 * Restrict convertible ut classes to the ones specified. Allows to convert
	 * some specific actors (e.g: lights, playerstarts, ...)
	 * 
	 * @param className
	 *            List of actor classes (e.g:
	 *            ['Brush','Light','PlayerStart',...]);
	 */
	public void setConvertOnly(String[] className) {

		if (mapConverter.getFilteredClasses() == null || mapConverter.getFilteredClasses().length == 0) {
			return;
		}

		HashMap<String, Class<? extends T3DActor>> classToUtActorNew = new HashMap<>();
		mapConverter.setCreateNoteForUnconvertedActors(false);

		for (String classFilter : className) {
			if (classToUtActor.containsKey((classFilter.toLowerCase()))) {
				classToUtActorNew.put(classFilter.toLowerCase(), classToUtActor.get(classFilter.toLowerCase()));
			}
		}

		classToUtActor = classToUtActorNew;
	}
}
