/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.*;

/**
 * Tells which UT actor classes will be converted
 * 
 * @author XtremeXp
 */
public class SupportedClasses {

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
	public SupportedClasses(MapConverter mapConverter) {
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
	public void putUtClass(Class<? extends T3DActor> utActorClass, String... classNames) {
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
	}

	private void initialize() {

		// FIXME block all has wrong volume, totally screwed
		// TODO move zones to match with T3DZoneInfo.class
		putUtClass(T3DBrush.class, "Brush", "LavaZone", "WaterZone", "SlimeZone", "NitrogenZone", "PressureZone", "VacuumZone");// ,
																																// "BlockAll");

		putUtClass(mapConverter.isFrom(UnrealEngine.UE1) ? T3DMover.class : T3DMoverSM.class, "Mover", "AttachMover", "AssertMover", "RotatingMover", "ElevatorMover", "MixMover", "GradualMover",
				"LoopMover", "InterpActor");

		putUtClass(T3DPlayerStart.class, "PlayerStart");
		putUtClass(T3DStaticMesh.class, "StaticMeshActor");

		for (T3DBrush.BrushClass brushClass : T3DBrush.BrushClass.values()) {

			// mover is special case because is dependant of staticmesh for UE2
			// not brush (UE1)
			if (brushClass != T3DBrush.BrushClass.Mover) {
				putUtClass(T3DBrush.class, brushClass.name());
			}
		}

		for (T3DLight.UE12_LightActors ut99LightActor : T3DLight.UE12_LightActors.values()) {
			putUtClass(T3DLight.class, ut99LightActor.name());
		}

		putUtClass(T3DLevelInfo.class, "LevelInfo");

		if (mapConverter.isTo(UTGames.UnrealEngine.UE3, UTGames.UnrealEngine.UE4)) {
			// disabled until working good
			// putUtClass(T3DZoneInfo.class, "ZoneInfo");
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

		// TODO specific other UE3 light SpotLightMovable, SpotLightToggable ...
		putUtClass(T3DTeleporter.class, "Teleporter", "FavoritesTeleporter", "VisibleTeleporter", "UTTeleporter", "UTTeleporterCustomMesh");
		putUtClass(T3DSound.class, "AmbientSound", "DynamicAmbientSound", "AmbientSoundSimple");
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
	}
}
