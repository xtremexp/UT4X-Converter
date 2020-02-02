/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.apache.commons.math3.util.Pair;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.vecmath.Vector3d;
import java.util.*;

import static org.xtx.ut4converter.t3d.T3DObject.IDT;

/**
 * Since T3DMover extends T3DBrush for Unreal Engine 1 and T3DMover extends
 * T3DStaticMesh for Unreal Engine > 1 Need to have a common "class" for both
 * actors for sharing same properties
 * 
 * @author XtremeXp
 */
public class MoverProperties implements T3D {

	/**
	 * Sounds used by movers when it started moving, is moving ... TODO make a
	 * list of UPackageRessource binded with some property name
	 */
	private UPackageRessource closedSound, closingSound, openedSound, openingSound, moveAmbientSound;

	/**
	 * CHECK usage?
	 */
	private List<Vector3d> savedPositions = new ArrayList<>();

	/**
	 * Map of [NumKey, Position]
	 *
	 * List of positions where mover moves UTUE4: Lift
	 * Destination=(X=0.000000,Y=0.000000,Z=730.000000) (no support for several
	 * nav localisations unlike UE1/2) UT99: KeyPos(1)=(Y=72.000000)
	 */
	private Map<Integer, Vector3d> positions = new LinkedHashMap<>();

	/**
	 * Map of [NumKey, Rotation]
	 */
	private Map<Integer, Vector3d> rotations = new LinkedHashMap<>();


	/**
	 * CHECK usage? U1: BaseRot=(Yaw=-49152)
	 */
	private List<Vector3d> savedRotations = new ArrayList<>();

    /**
	 * How long it takes for the mover to get to next position
	 */
	private Double moveTime;

	/**
	 * How long the mover stay static in his final position before returning to
	 * home
	 */
	private Double stayOpenTime;

	/**
	 * How long time the mover is available again after getting back to home
	 * position
	 */
	private Double delayTime;

	private T3DActor mover;

	/**
	 * Default state for Unreal 1
	 * TODO check for other games
	 * Depending on state mover will stay open, loop, be controled by trigger only and so on...
	 */
	private InitialState initialState;

	private T3DGradualMover.InitialState initialStateGradualMover;

	private BumpType bumpType;

	private MoverEncroachType moverEncroachType;

	private MoverGlideType moverGlideType;

	/**
	 * Reference to converter
	 */
	private MapConverter mapConverter;

	private List<T3DSimpleProperty> simpleProperties = new LinkedList<>();

	public MoverProperties(T3DActor mover, MapConverter mapConverter) {
		this.mover = mover;
		this.mapConverter = mapConverter;

		mover.registerSimpleProperty("bDamageTriggered", Boolean.class, Boolean.FALSE);
		mover.registerSimpleProperty("bDirectionalPushoff", Boolean.class, Boolean.FALSE);
		mover.registerSimpleProperty("bSlave", Boolean.class, Boolean.FALSE);
		mover.registerSimpleProperty("bTriggerOnceOnly", Boolean.class, Boolean.FALSE);
		mover.registerSimpleProperty("BumpEvent", String.class, null);
		mover.registerSimpleProperty("bUseTriggered", Boolean.class, Boolean.FALSE);
		mover.registerSimpleProperty("DamageTreshold", Float.class, 0f);
		mover.registerSimpleProperty("EncroachDamage", Float.class, 0f);
		mover.registerSimpleProperty("OtherTime", Float.class, 0f);
		mover.registerSimpleProperty("PlayerBumpEvent", String.class, null);
		mover.registerSimpleProperty("AttachTag", String.class);
		mover.registerSimpleProperty("NumKeys", Integer.class, 1);
	}

	enum BumpType {
		BT_PlayerBump, BT_PawnBump, BT_AnyBump
	}

	enum MoverEncroachType {
		ME_StopWhenEncroach, ME_ReturnWhenEncroach, ME_CrushWhenEncroach, ME_IgnoreWhenEncroach, ME_LiftWhenEncroach
	}

	enum MoverGlideType {
		MV_GlideByTime, MV_MoveByTime
	}

	enum InitialState {
		StandOpenTimed, BumpButton, BumpOpenTimed, ConstantLoop, TriggerPound, TriggerControl, TriggerToggle, TriggerOpenTimed, GradualTriggerOpenTimed, GradualTriggerToggle,
		/**
		 * UT2004
		 */
		TriggerAdvance,
		/**
		 *
		 */
		RotatingMover, LoopMove, LeadInOutMover, None
	}

	@Override
	public boolean analyseT3DData(String line) {
		/**
		 * OpenSound=SoundCue'A_Movers.Movers.Elevator01_StartCue'
         OpenedSound=SoundCue'A_Movers.Movers.Elevator01_StopCue'
         CloseSound=SoundCue'A_Movers.Movers.Elevator01_StartCue'
		 */
		// UE1 -> 'Wait at top time' (UE4)
		if (line.contains("StayOpenTime")) {
			stayOpenTime = T3DUtils.getDouble(line);
		}

		// UE1 -> 'Lift Time' (UE4)
		else if (line.contains("MoveTime")) {
			moveTime = T3DUtils.getDouble(line);
		}

		// UE1 -> 'Retrigger Delay' (UE4)
		else if (line.contains("DelayTime")) {
			delayTime = T3DUtils.getDouble(line);
		}

		// UE1 -> 'CloseStartSound' ? (UE4)
		else if (line.startsWith("ClosedSound=")) {
			closedSound = mover.mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
		}

		// UE1 -> 'CloseStopSound' ? (UE4)
		else if (line.startsWith("ClosingSound=") || line.startsWith("ClosingAmbientSound=")) {
			closingSound = mover.mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
		}

		// UE1 -> 'OpenStartSound' ? (UE4)
		else if (line.startsWith("OpeningSound=") || line.startsWith("OpeningAmbientSound=")) {
			openingSound = mover.mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
		}

		// UE1 -> 'OpenStopSound' ? (UE4)
		else if (line.startsWith("OpenedSound=")) {
			openedSound = mover.mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
		}

		// UE1 -> 'Closed Sound' (UE4)
		else if (line.startsWith("MoveAmbientSound=") || line.startsWith("OpenSound=")) {
			moveAmbientSound = mover.mapConverter.getUPackageRessource(line.split("\'")[1], T3DRessource.Type.SOUND);
		}

		// UE1 -> 'Lift Destination' (UE12)
		else if (line.contains("SavedPos=")) {
			savedPositions.add(T3DUtils.getVector3d(line.split("SavedPos")[1], 0D));
		}

		// UE1 -> 'Saved Positions' (UE12)
		else if (line.contains("SavedRot=")) {
			savedRotations.add(T3DUtils.getVector3dRot(line.split("SavedRot")[1]));
		}

		// empty value as seen in some maps ("InitialState=")
		else if (line.contains("InitialState=") && !line.endsWith("=")) {
			if(mover instanceof T3DGradualMover){
				this.initialStateGradualMover = T3DGradualMover.InitialState.valueOf(T3DUtils.getString(line));
			} else {
				this.initialState = InitialState.valueOf(T3DUtils.getString(line));
			}
		}

		else if (line.contains("BumpType=")) {
			this.bumpType = BumpType.valueOf(T3DUtils.getString(line));
		}

		else if (line.contains("MoverEncroachType=")) {
			this.moverEncroachType = MoverEncroachType.valueOf(T3DUtils.getString(line));
		}

		else if (line.contains("MoverGlideType=")) {
			this.moverGlideType = MoverGlideType.valueOf(T3DUtils.getString(line));
		}

		else if(line.startsWith("KeyRot")){

			final Pair<Integer, String> arrayEntry = T3DUtils.getArrayEntry(line);
			rotations.put(arrayEntry.getKey(), T3DUtils.getVector3dRot(arrayEntry.getValue()));
		}

		// UE1 -> 'Saved Positions' (UE12)
		else if (line.contains("KeyPos")) {
			final Pair<Integer, String> arrayEntry = T3DUtils.getArrayEntry(line);
			positions.put(arrayEntry.getKey(), T3DUtils.getVector3d(arrayEntry.getValue(), 0d));
		} else {
			return mover.parseSimpleProperty(line);
		}

		return true;
	}

	public void writeMoverProperties(final StringBuilder sbf) {
		// Write the mover as Destination Lift
		// TODO write as well matinee actor (once implementation done)
		// because it's impossible to know ("guess") if a mover is a lift or
		// another kind of mover (button, door, ...)
		// TODO use UBMover once UBMover blueprint done
		sbf.append(IDT).append("Begin Actor Class=").append(mover.t3dClass).append(" Name=").append(mover.name).append("_Lift\n");
		sbf.append(IDT).append("\tBegin Object Name=\"Scene1\"\n");
		mover.writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");
		sbf.append(IDT).append("\tScene1=Scene\n");
		sbf.append(IDT).append("\tRootComponent=Scene1\n");

		if (moveTime != null) {
			sbf.append(IDT).append("\tLift Time=").append(moveTime).append("\n");
		}

		// TODO handle multi position / rotation later
		// because we use last position but there might more than one position !
		if (!positions.isEmpty()) {
			Vector3d lastPosition = new Vector3d();

			// all positions are relative to previous one
			// since then need to sum them all
			// TODO remove this when blueprint handle multi-position
			for(Map.Entry<Integer, Vector3d> posEntry : positions.entrySet()){

				// KeyPos(1)=(X=-96.000000)
				lastPosition = posEntry.getValue();

				// for Unreal Beta mod only
				sbf.append(IDT).append("\tKeyPos(").append(posEntry.getKey()).append(")=").append(T3DUtils.toStringVec(posEntry.getValue())).append("\n");
			}

			sbf.append(IDT).append("\tLift Destination=(X=").append(T3DActor.fmt(lastPosition.x)).append(",Y=").append(T3DActor.fmt(lastPosition.y)).append(",Z=").append(T3DActor.fmt(lastPosition.z)).append(")\n");
		}

		if(!rotations.isEmpty()){
			Vector3d lastRot = new Vector3d();

			// same comment as above
			for(Map.Entry<Integer, Vector3d> rotEntry : rotations.entrySet()){

				lastRot = rotEntry.getValue();

				// for Unreal Beta mod only
				sbf.append(IDT).append("\tKeyRot(").append(rotEntry.getKey()).append(")=(Pitch=").append(T3DActor.fmt(rotEntry.getValue().x)).append(",Yaw=").append(T3DActor.fmt(rotEntry.getValue().y)).append(",Roll=").append(T3DActor.fmt(rotEntry.getValue().z)).append(")\n");
			}

			sbf.append(IDT).append("\tLift Destination Rot=(Pitch=").append(T3DActor.fmt(lastRot.x)).append(",Yaw=").append(T3DActor.fmt(lastRot.y)).append(",Roll=").append(T3DActor.fmt(lastRot.z)).append(")\n");
		}

		if (openingSound != null) {
			sbf.append(IDT).append("\tOpenStartSound=SoundCue'").append(openingSound.getConvertedName(mover.mapConverter)).append("'\n");
		} else if(mapConverter.convertSounds()) {
			sbf.append(IDT).append("\tOpenStartSound=None\n");
		}

		if (openedSound != null) {
			sbf.append(IDT).append("\tOpenStopSound=SoundCue'").append(openedSound.getConvertedName(mover.mapConverter)).append("'\n");
		} else if(mapConverter.convertSounds()) {
			sbf.append(IDT).append("\tOpenStopSound=None\n");
		}

		if (closingSound != null) {
			sbf.append(IDT).append("\tCloseStartSound=SoundCue'").append(closingSound.getConvertedName(mover.mapConverter)).append("'\n");
		} else if(mapConverter.convertSounds()) {
			sbf.append(IDT).append("\tCloseStartSound=None\n");
		}

		if (closedSound != null) {
			sbf.append(IDT).append("\tCloseStopSound=SoundCue'").append(closedSound.getConvertedName(mover.mapConverter)).append("'\n");
		} else if(mapConverter.convertSounds()) {
			sbf.append(IDT).append("\tCloseStopSound=None\n");
		}

		if (moveAmbientSound != null) {
			sbf.append(IDT).append("\tMoveLoopSound=SoundCue'").append(moveAmbientSound.getConvertedName(mover.mapConverter)).append("'\n");
		} else if(mapConverter.convertSounds()) {
			sbf.append(IDT).append("\tMoveLoopSound=None\n");
		}

		if (stayOpenTime != null) {
			sbf.append(IDT).append("\tWait at top time=").append(stayOpenTime).append("\n");
		}

		if (delayTime != null) {
			sbf.append(IDT).append("\tRetrigger Delay=").append(delayTime).append("\n");
		}

		if (initialState != null) {
			sbf.append(IDT).append("\tInitialState=").append("NewEnumerator").append(initialState.ordinal()).append("\n");
		}

		if (initialStateGradualMover != null) {
			sbf.append(IDT).append("\tInitialState=").append("NewEnumerator").append(initialStateGradualMover.ordinal()).append("\n");
		}

		if (bumpType != null) {
			sbf.append(IDT).append("\tBumpType=").append("NewEnumerator").append(bumpType.ordinal()).append("\n");
		}

		if (moverEncroachType != null) {
			sbf.append(IDT).append("\tMoverEncroachType=").append("NewEnumerator").append(moverEncroachType.ordinal()).append("\n");
		}

		if (moverGlideType != null) {
			sbf.append(IDT).append("\tMoverGlideType=").append("NewEnumerator").append(moverGlideType.ordinal()).append("\n");
		}

		if (mover instanceof T3DMoverSM) {
			T3DMoverSM moverSm = (T3DMoverSM) mover;

			if (moverSm.staticMesh != null && moverSm.getMapConverter().convertStaticMeshes()) {
				sbf.append(IDT).append("\tLift Mesh=StaticMesh'").append(moverSm.staticMesh.getConvertedName(moverSm.getMapConverter())).append("'\n");
			}
		}

		mover.writeSimpleProperties();

		mover.writeEndActor();
	}

	@Override
	public void convert() {

		// used to match very similar sound resources by name (e.g:
		// A_Movers.Movers.Elevator01.Loop -> A_Movers.Movers.Elevator01.LoopCue
		final boolean isFromUe3 = mapConverter.isFrom(UnrealEngine.UE3);

		if(stayOpenTime == null){
			// default stay open time with UE1 (TODO check UE2/UE3)
			stayOpenTime = 4d;
		}

		// mover might be rotated only
		// so we don't want to have the default 200 Z axis offset
		// by adding a 0 destination position
		if(this.positions.isEmpty()){
			// TODO test this commented
			//this.positions.add(new Vector3d(0, 0, 0));
		}

		if(mapConverter.convertSounds()) {
			if (openingSound != null) {
				openingSound.export(UTPackageExtractor.getExtractor(mover.mapConverter, openingSound), !isFromUe3);
			}

			if (openedSound != null) {
				openedSound.export(UTPackageExtractor.getExtractor(mover.mapConverter, openedSound), !isFromUe3);
			}

			if (closingSound != null) {
				closingSound.export(UTPackageExtractor.getExtractor(mover.mapConverter, closingSound), !isFromUe3);
			}

			if (closedSound != null) {
				closedSound.export(UTPackageExtractor.getExtractor(mover.mapConverter, closedSound), !isFromUe3);
			}

			if (moveAmbientSound != null) {
				moveAmbientSound.export(UTPackageExtractor.getExtractor(mover.mapConverter, moveAmbientSound), !isFromUe3);
			}
		}

		for(Vector3d rotator : rotations.values()){
			// convert 65536 rotator old range to UE4 range
			if(mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE3, UnrealEngine.UE3)){
				T3DUtils.convertRotatorTo360Format(rotator);
			}
		}

		// as see, with Dig (Unreal 1 map)
		// if movetime = 0, then mover won't move at all
		// so need to force to some tiny value
		if(moveTime != null && moveTime == 0d){
			moveTime = 0.1d;
		}

		if(mapConverter.isUseUbClasses()) {
			mover.t3dClass = "UBMover_C";
		} else {
			mover.t3dClass = "Generic_Lift_C";
		}
	}

	@Override
	public void scale(Double newScale) {

		for( Vector3d position : positions.values() ) {
			position.scale(newScale); 
		}
	}

	@Override
	public String getName() {
		return mover.name;
	}

	public T3DActor getMover() {
		return mover;
	}

	@Override
	public void toT3d(StringBuilder sb, String prefix) {
	}
}
