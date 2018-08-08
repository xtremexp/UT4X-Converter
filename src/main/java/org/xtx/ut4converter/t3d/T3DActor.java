/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.t3d.T3DMatch.Match;
import org.xtx.ut4converter.ucore.ue4.SceneComponent;

import javax.vecmath.Vector3d;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * 
 * @author XtremeXp
 */
public abstract class T3DActor extends T3DObject {

	/**
	 * All original properties stored for this actor. Basically is a map of
	 * "key" and value" E.G: Brush=Model'MyLevel.Brush' -> Key = Brush, Value =
	 * Model'MyLevel.Brush' Might be used after parsing t3d actor data to
	 * convert it.
	 */
	protected Map<String, String> properties;

	/**
	 * Possible match for t3d actor
	 */
	protected Match match;

	/**
	 * Unreal Engine 4 only. Root Component Type
	 */
	protected T3DMatch.UE4_RCType ue4RootCompType = T3DMatch.UE4_RCType.UNKNOWN;

	/**
	 * Original actor class
	 */
	protected String t3dOriginClass;

	/**
	 * UE1/2/3? property in Events->Tag
	 */
	protected String tag;
	
	/**
	 * UE1/2/ (3?) property in Events->Event
	 */
	protected String event;

	/**
	 * Location of actor (if null means 0 location)
	 */
	protected Vector3d location;

	/**
	 * Co-Location of actor Used by some old Unreal 1 / UT99 maps ... Useless
	 * for convert.
	 */
	protected Vector3d coLocation;

	/**
	 * Old-Location of actor Used by some old Unreal 1 / UT99 maps ...
	 */
	protected Vector3d oldLocation;

	/**
	 * Rotation of actor
	 */
	protected Vector3d rotation;

	/**
	 * 3D Scaling
	 */
	protected Vector3d scale3d;

	/**
	 * Scale in unreal editor of sprite
	 */
	protected Double drawScale;

	/**
	 * UT99
	 */
	protected Double collisionRadius;

	/**
	 * UT99
	 */
	protected Double collisionHeight;

	String otherdata = "";

	boolean usecolocation = false;

	/**
	 * Used to add extra Z location (for converting pickup for exemple not
	 * having same 'origin')solar
	 */
	Double offsetZLocation = 0D;

	/**
     *
     */
	protected boolean validWriting = true;

	/**
	 * Only used bu UE4 (and UE3?) Contains location and rotation data of actor
	 */
	public SceneComponent sceneComponent;

	/**
	 * Force these lines to be written (not used yet for each subclass of this
	 * class)
	 */
	protected List<String> forcedWrittenLines = new ArrayList<>();

	/**
	 * Linked actors to this one. (e.g: teleporters)
	 */
	protected List<T3DActor> linkedTo = new ArrayList<>();

	/**
	 * If this actor has been created from another one. This property should be
	 * not null
	 */
	T3DActor parent;

	/**
	 * If not empty current actor will be replaced by these ones when writing
	 * t3d converted stuff.
	 */
	protected List<T3DActor> children = new ArrayList<>();


	
	private boolean invalidActorForWrite;

	String currentSubObjectClass;
	String currentSubObjectName;

	/**
	 * Begin Object Class=DistributionFloatUniform Name=DistributionDelayTime
	 * For UE3 only used to get subobject definitions
	 * 
	 * @param line
	 */
	public void preAnalyse(String line) {

		if (!mapConverter.isFrom(UnrealEngine.UE3)) {
			return;
		}
		// Class=DistributionFloatUniform Name=DistributionPitch
		// ObjName=DistributionFloatUniform
		if (line.startsWith("Begin Object")) {
			currentSubObjectClass = line.split("Class=")[1].split(" Name=")[0];
			currentSubObjectName = line.split("Name=")[1].split("\\ ")[0];
		} else if (line.startsWith("End Object")) {
			currentSubObjectClass = null;
			currentSubObjectName = null;
		}
	}

	/**
	 * Read line of t3d file to parse data about current t3d actor being read
	 * 
	 * @param line
	 * @return true if data has been extracted from line false else (useful to
	 *         check which data has not been parsed)
	 */
	public boolean analyseT3DData(String line) {
		return parseOtherData(line);
	}

	public T3DActor(MapConverter mc, String t3dClass){
		super(mc, t3dClass);
		
		sceneComponent = new SceneComponent(mc);
		properties = new HashMap<>();
	}
	
	/**
	 *
	 * @param mc
	 * @param t3dClass
	 * @param actor
	 *            If not null will copy some basic properties like location to
	 *            this actor
	 */
	public T3DActor(MapConverter mc, String t3dClass, T3DActor actor) {

		super(mc, t3dClass);

		sceneComponent = new SceneComponent(mc);
		properties = new HashMap<>();

		if (actor != null) {
			this.location = actor.location;
			this.rotation = actor.rotation;
			this.scale3d = actor.scale3d;
			this.drawScale = actor.drawScale;
			this.tag = actor.tag;
			this.name = actor.name;
		}
	}

	public void setT3dOriginClass(String t3dOriginClass) {
		this.t3dOriginClass = t3dOriginClass;
	}

	/**
	 * Get some important info about actors like location,rotation,drawscale,...
	 * 
	 * @param line
	 *            T3D level line being analyzed
	 * @return true if some Data has been parsed.
	 */
	public boolean parseOtherData(String line) {
		int equalsIdx = line.indexOf("=");

		// BlockAll hack overide
		if ((!(this instanceof T3DBrush) || "BlockAll".equals(t3dClass)) && equalsIdx != -1) {
			properties.put(line.substring(0, equalsIdx).trim(), line.substring(equalsIdx + 1, line.length()));
		}

		if (line.startsWith("Location=") || line.contains("\tLocation=")) {
			location = T3DUtils.getVector3d(line, 0D);
			sceneComponent.setRelativeLocation(location);
		}

		else if (line.startsWith("OldLocation=") || line.contains("\tOldLocation=")) {
			oldLocation = T3DUtils.getVector3d(line, 0D);
		}

		else if (line.startsWith("ColLocation=") || line.contains("\tColLocation=")) {
			coLocation = T3DUtils.getVector3d(line, 0D);
		}

		else if (line.startsWith("DrawScale3D")) {

			Vector3d tmpScale = T3DUtils.getVector3d(line, 1D);

			if (scale3d == null) {
				scale3d = tmpScale;
			} else {
				scale3d.x *= tmpScale.x;
				scale3d.y *= tmpScale.y;
				scale3d.z *= tmpScale.z;
			}

			sceneComponent.setRelativeScale3D(scale3d);
		}

		else if (line.startsWith("DrawScale=")) {

			double scale = T3DUtils.getDouble(line);

			// Scale and Scale3d can both be used to scale up/down staticmeshes
			if (this instanceof T3DStaticMesh) {
				if (scale3d == null) {
					scale3d = new Vector3d(scale, scale, scale);
				} else {
					scale3d.scale(scale);
				}
			} else {
				drawScale = T3DUtils.getDouble(line);
			}
		}

		else if (line.startsWith("Rotation")) {
			rotation = T3DUtils.getVector3dRot(line);
			sceneComponent.setRelativeRotation(rotation);
		}

		else if (line.contains("Name=")) {
			// "Begin Object Class=Action_PLAYSOUND Name=Action_PLAYSOUND3" for
			// UT2004 scripted trigger
			// TODO better handle that
			if (mapConverter.isFrom(UnrealEngine.UE2) && line.contains("Begin Object")) {
				return false;
			}
			name = line.split("Name=")[1].replaceAll("\"", "");
		}

		else if (line.contains("CollisionRadius=")) {
			collisionRadius = T3DUtils.getDouble(line);
		}

		else if (line.contains("CollisionHeight=")) {
			collisionHeight = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("Group=")) {
			addOtherData(line);
		}

		else if (line.startsWith("Tag=")) {
			tag = T3DUtils.getString(line);
		}

		else if (line.startsWith("Event=")) {
			event = line.split("\\=")[1].replaceAll("\\\"", "");
		}

		else {
			if (equalsIdx != -1 && !(this instanceof T3DNote) && !parseSimpleProperty(line)) {
				this.mapConverter.getT3dLvlConvertor().logUnconvertedProperty(this.getT3dClass(), line.substring(0, equalsIdx).split("\\(")[0]);
			}
			return false;
		}

		return true;
	}

	protected void writeLocRotAndScale() {
		writeLocRotAndScale(sbf, getOutputGame().engine, location, rotation, scale3d);
	}

	public void writeLocRotSceneComponent(String prefix) {
		sceneComponent.writeBeginObj(sbf, prefix);
		writeLocRotAndScale();
		sceneComponent.writeEndObj(sbf, prefix);
	}

	/**
	 * Write Location Rotation and drawScale of converted actor
	 * 
	 * @param sbf
	 * @param outEngine
	 * @param location
	 * @param rotation
	 * @param scale3d
	 */
	public static void writeLocRotAndScale(StringBuilder sbf, UnrealEngine outEngine, Vector3d location, Vector3d rotation, Vector3d scale3d) {

		String baseText = IDT + "\t\t";

		if (outEngine.version >= UTGames.UnrealEngine.UE4.version) {
			if (location != null) {
				sbf.append(baseText).append("RelativeLocation=(X=").append(fmt(location.x)).append(",Y=").append(fmt(location.y)).append(",Z=").append(fmt(location.z)).append(")\n");
			}

			// RelativeRotation=(Pitch=14.179391,Yaw=13.995641,Roll=14.179387)
			if (rotation != null) {
				sbf.append(baseText).append("RelativeRotation=(Pitch=").append(fmt(rotation.x)).append(",Yaw=").append(fmt(rotation.y)).append(",Roll=").append(fmt(rotation.z)).append(")\n");
			}

			// RelativeScale3D=(X=4.000000,Y=3.000000,Z=2.000000)
			if (scale3d != null) {
				sbf.append(baseText).append("RelativeScale3D=(X=").append(fmt(scale3d.x)).append(",Y=").append(fmt(scale3d.y)).append(",Z=").append(fmt(scale3d.z)).append(")\n");
			}
		}
		// checked U1, UT99, U2, UT2004, UT3
		else {
			if (location != null) {
				sbf.append(baseText).append("Location=(X=").append(fmt(location.x)).append(",Y=").append(fmt(location.y)).append(",Z=").append(fmt(location.z)).append(")\n");
			}

			// RelativeRotation=(Pitch=14.179391,Yaw=13.995641,Roll=14.179387)
			if (rotation != null) {
				sbf.append(baseText).append("Rotation=(Pitch=").append(fmt(rotation.x)).append(",Yaw=").append(fmt(rotation.y)).append(",Roll=").append(fmt(rotation.z)).append(")\n");
			}

			// RelativeScale3D=(X=4.000000,Y=3.000000,Z=2.000000)
			if (scale3d != null) {
				sbf.append(baseText).append("Scale3D=(X=").append(fmt(scale3d.x)).append(",Y=").append(fmt(scale3d.y)).append(",Z=").append(fmt(scale3d.z)).append(")\n");
			}
		}
	}

	/**
	 * 
	 * @param newScale
	 */
	public void scale(Double newScale) {

		if (newScale == null) {
			return;
		}

		if (newScale > 1) {
			if (location != null)
				location.scale(newScale);
			if (coLocation != null)
				coLocation.scale(newScale);
			if (drawScale != null)
				drawScale *= newScale;
			if (collisionHeight != null)
				collisionHeight *= newScale;
			if (collisionRadius != null)
				collisionRadius *= newScale;
			if (scale3d != null)
				scale3d.scale(newScale);
		}
	}

	private void addOtherData(String somedata) {
		this.otherdata += somedata + "\n";
	}

	/**
	 *
	 * @param otherdata
	 */
	public void setOtherdata(String otherdata) {
		this.otherdata = otherdata;
	}

	/**
	 *
	 * @return
	 */
	public Vector3d getLocation() {
		return location;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static String fmt(double value) {
		return formatValue(value);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static String formatValue(double value) {
		DecimalFormat df = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));
		return df.format(value);
	}

	/**
	 *
	 * @param line
	 * @return
	 */
	public static String getActorClass(String line) {
		return (line.split("=")[1]).split(" ")[0];
	}

	/**
	 * Get the input game this actor come from
	 * 
	 * @return
	 */
	protected UTGames.UTGame getInputGame() {
		return mapConverter.getInputGame();
	}

	/**
	 * Get the output game to which it must be converted
	 * 
	 * @return
	 */
	protected UTGames.UTGame getOutputGame() {
		return mapConverter.getOutputGame();
	}

	/**
	 *
	 * @param offsetZLocation
	 */
	public void setOffsetZLocation(Double offsetZLocation) {
		this.offsetZLocation = offsetZLocation;
	}

	/**
	 *
	 * @return
	 */
	protected MapConverter getMapConverter() {
		return this.mapConverter;
	}

	/**
     *
     */
	public void convert() {

		if (coLocation != null && mapConverter.getInputGame().engine == UTGames.UnrealEngine.UE1) {
			if (location != null) {
				location.add(coLocation);
			} else {
				location = coLocation;
			}

			coLocation = null;
		}

		// changes height of actor if needed (so aligned with floor for example)
		if (offsetZLocation != null) {
			if (location != null) {
				location.z += offsetZLocation;
			} else {
				location = new Vector3d(0, 0, offsetZLocation);
			}

			offsetZLocation = null;
		}

		if (rotation != null) {
			// Rotation range changed from UE4
			// for brushes no need that since they have been transformed
			// permanently
			// Vertice data updated with rotation and rotation reset

			double rotFac = 1d;

			if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3) && mapConverter.isTo(UnrealEngine.UE4)) {
				rotFac = 360d / 65536d;
			} else if (mapConverter.isTo(UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3) && mapConverter.isFrom(UnrealEngine.UE4)) {
				rotFac = 65536d / 360d;
			}

			rotation.x *= rotFac;
			rotation.y *= rotFac;
			rotation.z *= rotFac;
		}
		
		// Brush name need to be the original one to fix ut3 brushes order (based on actor name)
		if (this.name != null && !(this instanceof T3DBrush && this.mapConverter.isFrom(UnrealEngine.UE3))) {

			final String[] namePrefixSp = this.name.split("\\_");
			String namePrefix = null;
			if (namePrefixSp.length == 2) {
				namePrefix = namePrefixSp[0];
			} else if (namePrefixSp.length == 3) {
				namePrefix = namePrefixSp[0] + "_" + namePrefixSp[1];
			}

			if (this.tag != null && !this.tag.equals(namePrefix)) {
				this.name += "_" + this.tag;
			}

			if (this.event != null && !this.event.equals(namePrefix)) {
				this.name += "->" + this.event;
			}
		}

		// Notify actor was converted
		game = mapConverter.getOutputGame();

		// Converts all child actors as well
		children.stream().forEach((child) -> {
			if (child.needsConverting()) {
				child.convert();
			}
		});
	}

	/**
	 * We may not want to convert this t3d actor after analyzing data, that's
	 * the purpose of this.
	 * 
	 * @return true is this t3d actor is allowed to be converted else not
	 */
	public boolean isValidConverting() {
		return true;
	}

	protected void writeEndObject() {
		sbf.append(IDT).append("\tEnd Object\n");
	}

	protected void writeBeginActor() {
		sbf.append(IDT).append("Begin Actor Class=").append(t3dClass).append(" Name=").append(name).append("\n");
	}

	/**
     *
     */
	protected void writeEndActor() {

		// means we did not even write "begin actor" so we skip ...
		if (sbf.length() == 0) {
			return;
		}

		if (mapConverter.toUnrealEngine4()) {
			if (drawScale != null) {
				sbf.append(IDT).append("\tSpriteScale=").append(drawScale).append("\n");
			}
			sbf.append(IDT).append("\tActorLabel=\"").append(name).append("\"\n");

			if (this.tag != null) {
				sbf.append(IDT).append("\tTags(0)=\"").append(this.tag).append("\"\n");
			}

			// not handle by UE4 natively but by some of our custom blueprints !
			if (this.event != null) {
				sbf.append(IDT).append("\tEvent=\"").append(this.event).append("\"\n");
			}
		}
		// Checked u1, ut99, ut2004, ut3
		else {
			if (drawScale != null) {
				sbf.append(IDT).append("\tDrawScale=").append(drawScale).append("\"\n");
            }
			sbf.append(IDT).append("\tName=\"").append(name).append("\n");
		}

		sbf.append(IDT).append("End Actor\n");
	}

	/**
	 * Says if this actor is valid to be written to t3d Should be called always
	 * after isValidConverting Some actors might be valid for convert but not
	 * for write. E.g: sheet brushes not valid for write but valid for
	 * convert(auto-create sheet static mesh) isValidConverting() --> convert()
	 * -> isValidWritting() -> write(actor.writeMoverProperties())
	 * 
	 * @return
	 */
	public boolean isValidWriting() {
		return validWriting;
	}

	public void setValidWriting(boolean validWriting) {
		this.validWriting = validWriting;
	}

	@Override
	public String toString() {
		return sbf.toString();
	}

	/**
	 * Null-safe property getter
	 * 
	 * @param name
	 * @return
	 */
	protected String getProperty(String name) {

		if (properties.containsKey(name)) {
			return properties.get(name);
		} else {
			return null;
		}
	}

	/**
	 * Indicates if this actor needs to be converted
	 * 
	 * @return <code>true</code>> If actor needs to be converted
	 */
	private boolean needsConverting() {
		return game != mapConverter.getOutputGame();
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Replace current actor that won't be converted with another one.
	 * 
	 * @param actor
	 *            Actor replacement
	 */
	protected void replaceWith(T3DActor actor) {
		children.add(actor);
		validWriting = false;
	}

	/**
	 * Return the name of the actor
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the reference of this actor in level E.G: Generic_Lift_C
	 * '/Game/Maps/AS-Mazon/AS-Mazon-V01.AS-Mazon-V01:PersistentLevel.Mover4'
	 * 
	 * @return
	 */
	public String getLevelReference() {

		if (t3dClass == null) {
			return null;
		}

		// UE4
		// TODO check <=UE3 getLevelReference
		return t3dClass + "'" + mapConverter.getRelativeUtMapPath() + "." + mapConverter.getOutMapName() + ":PersistentLevel." + name;
	}

	public SceneComponent getSceneComponent() {
		return sceneComponent;
	}

	/**
	 * Get original class of actor
	 * 
	 * @return
	 */
	public String getT3dOriginClass() {
		return t3dOriginClass;
	}

	String writeSimpleActor(final String actorClass){
		sbf.append(IDT).append("Begin Actor Class=").append(actorClass).append(" \n");

		sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");

		writeSimpleProperties();

		sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

		writeEndActor();

		return sbf.toString();
	}

}
