/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.t3d.T3DMatch.Match;
import org.xtx.ut4converter.ucore.UPackageRessource;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.UnrealGame;
import org.xtx.ut4converter.ucore.ue4.SceneComponent;

import javax.vecmath.Vector3d;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static org.xtx.ut4converter.ucore.UnrealEngine.*;

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
	 * Extra properties that should be written
	 */
	protected Map<String, Object> convProperties = new HashMap<>();

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
	 * Group
	 */
	protected String group;

	/**
	 * UE1/2/ (3?) property in Events->Event
	 */
	protected String event;

	/**
	 * If true actor should be hidden in-game
	 */
	private Boolean bHidden;

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


	/**
	 * UE1/UE2 property: bCollideActors
	 * In UE3 its CollideActors
	 */
	protected Boolean collideActors;

	/**
	 * UE1/UE2 property only
	 */
	protected Boolean collideWorld;

	/**
	 * UE1/UE2 property only
	 */
	protected Boolean blockActors;

	/**
	 * UE1/UE2 property only
	 */
	protected Boolean blockPlayers;

	/**
	 * UE3+ List of components
	 */
	protected List<Component> components = new ArrayList<>();

	/**
	 * UE3 property
	 * Default collision type for static meshes in ut3/ue3
	 */
	protected UE3CollisionType collisionType = UE3CollisionType.COLLIDE_NoCollision;

	public enum UE3CollisionType {
		COLLIDE_CustomDefault,
		COLLIDE_NoCollision,
		COLLIDE_BlockAll,
		COLLIDE_BlockWeapons,
		COLLIDE_TouchAll,
		COLLIDE_TouchWeapons,
		COLLIDE_BlockAllButWeapons,
		COLLIDE_TouchAllButWeapons
	}

	/**
	 * Used to add extra Z location (for converting pickup for exemple not
	 * having same 'origin')solar
	 */
	protected Double offsetZLocation = 0D;

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
	protected T3DActor parent;

	/**
	 * If not empty current actor will be replaced by these ones when writing
	 * t3d converted stuff.
	 */
	protected List<T3DActor> children = new ArrayList<>();


	protected String currentSubObjectClass;

	protected String currentSubObjectName;


	/**
	 * Begin Object Class=DistributionFloatUniform Name=DistributionDelayTime
	 * For UE3 only used to get subobject definitions
	 *
	 * @param line T3D line text to analyse
	 */
	public void preAnalyse(String line) {

		if (!mapConverter.isFrom(UnrealEngine.UE3)) {
			return;
		}
		// Class=DistributionFloatUniform Name=DistributionPitch
		// ObjName=DistributionFloatUniform
		if (line.startsWith("Begin Object")) {
			currentSubObjectClass = line.split("Class=")[1].split(" Name=")[0];
			currentSubObjectName = line.split("Name=")[1].split(" ")[0];
		} else if (line.startsWith("End Object")) {
			currentSubObjectClass = null;
			currentSubObjectName = null;
		}
	}

	/**
	 * Read line of t3d file to parse data about current t3d actor being read
	 *
	 * @param line Line
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
	 * @param mc Map converter instance
	 * @param t3dClass T3d class
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
			properties.put(line.substring(0, equalsIdx).trim(), line.substring(equalsIdx + 1));
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
		else if (line.startsWith("bHidden=")) {
			bHidden = T3DUtils.getBoolean(line);
		}
		else if (line.startsWith("bCollideActors=")) {
			this.collideActors = T3DUtils.getBoolean(line);
		}
		else if (line.startsWith("bCollideWorld=")) {
			this.collideWorld = T3DUtils.getBoolean(line);
		}
		else if (line.startsWith("bBlockActors=")) {
			this.blockActors = T3DUtils.getBoolean(line);
		}
		else if (line.startsWith("bBlockPlayers=")) {
			this.blockPlayers = T3DUtils.getBoolean(line);
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
			this.originalName = this.name;
		}

		else if (line.contains("CollisionRadius=")) {
			collisionRadius = T3DUtils.getDouble(line);
		}

		else if (line.contains("CollisionHeight=")) {
			collisionHeight = T3DUtils.getDouble(line);
		}

		else if (line.startsWith("Group=")) {
			group = T3DUtils.getString(line);
		}

		else if (line.startsWith("Tag=")) {
			tag = T3DUtils.getString(line);
		}

		else if (line.startsWith("Event=")) {
			event = line.split("=")[1].replaceAll("\"", "");
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
		writeLocRotAndScale(sbf, Objects.requireNonNull(UnrealEngine.from(getOutputGame().getUeVersion())), location, rotation, scale3d);
	}

	/**
	 * Write Location Rotation and drawScale of converted actor
	 *
	 * @param sbf String builder
	 * @param outEngine Unreal Engine out
	 * @param location Location
	 * @param rotation Rotation
	 * @param scale3d Scale 3D
	 */
	private static void writeLocRotAndScale(StringBuilder sbf, UnrealEngine outEngine, Vector3d location, Vector3d rotation, Vector3d scale3d) {

		sbf.append(writeUE4LocRotAndScale(outEngine.version >= 4, location, rotation, scale3d, IDT + "\t"));
	}

	public static String writeUE4LocRotAndScale(boolean isRelative, Vector3d location, Vector3d rotation, Vector3d scale3d, String baseText) {

		StringBuilder sb = new StringBuilder();
		String prefix = isRelative ? "Relative":"";

		if (location != null) {
			sb.append(baseText).append(prefix).append("Location=(X=").append(fmt(location.x)).append(",Y=").append(fmt(location.y)).append(",Z=").append(fmt(location.z)).append(")\n");
		}

		// RelativeRotation=(Pitch=14.179391,Yaw=13.995641,Roll=14.179387)
		if (rotation != null) {
			sb.append(baseText).append(prefix).append("Rotation=(Pitch=").append(fmt(rotation.x)).append(",Yaw=").append(fmt(rotation.y)).append(",Roll=").append(fmt(rotation.z)).append(")\n");
		}

		// RelativeScale3D=(X=4.000000,Y=3.000000,Z=2.000000)
		if (scale3d != null) {
			sb.append(baseText).append(prefix).append("Scale3D=(X=").append(fmt(scale3d.x)).append(",Y=").append(fmt(scale3d.y)).append(",Z=").append(fmt(scale3d.z)).append(")\n");
		}

		return sb.toString();
	}

	/**
	 *
	 * @param newScale New scale
	 */
	public void scale(double newScale) {

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


	/**
	 *
	 * @return Location of actor
	 */
	public Vector3d getLocation() {
		return location;
	}

	/**
	 * Format a double to string unreal engine format
	 * @param value Double
	 * @return Formatted double string
	 */
	public static String fmt(double value) {
		return formatValue(value);
	}

	/**
	 *
	 * @param value Double
	 * @return Formatted double string
	 */
	public static String formatValue(double value) {
		DecimalFormat df = new DecimalFormat("0.000000", new DecimalFormatSymbols(Locale.US));
		return df.format(value);
	}

	/**
	 * Get the input game this actor come from
	 *
	 * @return Input game
	 */
	protected UnrealGame getInputGame() {
		return mapConverter.getInputGame();
	}

	/**
	 * Get the output game to which it must be converted
	 *
	 * @return Output game
	 */
	protected UnrealGame getOutputGame() {
		return mapConverter.getOutputGame();
	}

	/**
	 *
	 * @return Map converter instance
	 */
	public MapConverter getMapConverter() {
		return this.mapConverter;
	}

	public void setLocation(Vector3d location) {
		this.location = location;
	}

	/**
     *
     */
	public void convert() {

	    if(collisionHeight == null){
	        // default collision height for UE1
            collisionHeight = 40d;
        }

        if(collisionRadius == null){
            // default collision radius for UE1
            collisionRadius = 40d;
        }

		if (coLocation != null && mapConverter.getInputGame().getUeVersion() == UnrealEngine.UE1.version) {
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

			if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3) && mapConverter.isTo(UE4)) {
				rotFac = 360d / 65536d;
			} else if (mapConverter.isTo(UnrealEngine.UE1, UnrealEngine.UE2, UnrealEngine.UE3) && mapConverter.isFrom(UE4)) {
				rotFac = 65536d / 360d;
			}

			rotation.x *= rotFac;
			rotation.y *= rotFac;
			rotation.z *= rotFac;
		}

		// Brush name need to be the original one to fix ut3 brushes order (based on actor name)
		if (this.name != null && !(this instanceof T3DBrush && this.mapConverter.isFrom(UnrealEngine.UE3))) {

			final String[] namePrefixSp = this.name.split("_");
			String namePrefix = null;
			if (namePrefixSp.length == 2) {
				namePrefix = namePrefixSp[0];
			} else if (namePrefixSp.length == 3) {
				namePrefix = namePrefixSp[0] + "_" + namePrefixSp[1];
			}

			// rename name to name_tag->event
			// so it's easier to see what original UE1 actor may trigger another one (event)
			if (this.tag != null && !this.tag.equals(namePrefix)) {
				this.name += "_" + this.tag;
			}

			if (this.event != null && !this.event.equals(namePrefix)) {
				this.name += "->" + this.event;
			}
		}

		// Notify actor was converted
		setGame(mapConverter.getOutputGame());

		// Converts all child actors as well
		children.forEach((child) -> {
			if (child.needsConverting()) {
				child.convert();
			}
		});

		for(final T3DSimpleProperty simpleProperty : registeredProperties){
			if(simpleProperty.getPropertyValue() instanceof final UPackageRessource packageRessource){
				final T3DRessource.Type type = simpleProperty.getRessourceType();

				if(type == T3DRessource.Type.SOUND && mapConverter.convertSounds()){
					packageRessource.export(UTPackageExtractor.getExtractor(mapConverter, packageRessource));
				}
				else if(type == T3DRessource.Type.TEXTURE && mapConverter.convertTextures()){
					packageRessource.export(UTPackageExtractor.getExtractor(mapConverter, packageRessource));
				}
				// MESHES have to be converted to staticmeshes
				else if((type == T3DRessource.Type.STATICMESH  ||  type == T3DRessource.Type.MESH) && mapConverter.convertStaticMeshes()){
					packageRessource.export(UTPackageExtractor.getExtractor(mapConverter, packageRessource));
				}
			}
		}
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

	/**
     *
     */
	protected void writeEndActor() {

		// means we did not even write "begin actor" so we skip ...
		if (sbf.length() == 0) {
			return;
		}

		if (mapConverter.isTo(UE4)) {
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
				sbf.append(IDT).append("\tDrawScale=").append(drawScale).append("\n");
            }
			sbf.append(IDT).append("\tName=\"").append(name).append("\"\n");
		}

		if (group != null) {
			// need check for UE4
			sbf.append(IDT).append("\tGroup=\"").append(group).append("\n");
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
	 * @return <code>true</code> if this actor should be written in converted .t3d file
	 */
	public boolean isValidWriting() {
		return validWriting;
	}

	@Override
	public String toString() {
		return sbf.toString();
	}

	/**
	 * Indicates if this actor needs to be converted
	 *
	 * @return <code>true</code>> If actor needs to be converted
	 */
	private boolean needsConverting() {
		return getGame() != mapConverter.getOutputGame();
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

	protected String writeSimpleActor(final String actorClass){
		return writeSimpleActor(actorClass, "SceneComponent");
	}

	protected String writeSimpleActor(final String actorClass, final String componentType){

		final String rootComponentName = componentType + "0";

		sbf.append(IDT).append("Begin Actor Class=").append(actorClass).append(" \n");

		sbf.append(IDT).append("\tBegin Object Class=\"").append(componentType).append("\" Name=\"").append(rootComponentName).append("\"\n");
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tBegin Object Name=\"").append(rootComponentName).append("\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		if ("SceneComponent".equals(componentType)) {
			sbf.append(IDT).append("\tDefaultSceneRoot=").append(rootComponentName).append("\n");
		} else if ("StaticMeshComponent".equals(componentType)) {
			sbf.append(IDT).append("\tStaticMeshComponent=").append(rootComponentName).append("\n");
		}

		writeSimpleProperties();

		if (this.bHidden != null) {
			sbf.append(IDT).append("\tbHidden=").append(this.bHidden).append("\n");
		}

		sbf.append(IDT).append("\tRootComponent=").append(rootComponentName).append("\n");

		writeEndActor();

		return sbf.toString();
	}

	public List<T3DActor> getChildren() {
		return children;
	}

	/**
	 * FOR TESTING PURPOSE ONLY
	 * This follows order of execution in T3DLevelConvertor :
	 * Convert -> Scale -> toT3D()
	 *
	 * @param scaleFactor Scale factor
	 * @return Converted actor as t3d string
	 */
	protected String convertScaleAndToT3D(double scaleFactor) {
		this.convert();
		this.scale(scaleFactor);

		return "Begin Map\nBegin Level\n" + toT3d() + "End Level\nEnd Map";
	}

	protected void addComponent(final Component component){
		this.components.add(component);
	}

	protected void addConvProperty(String key, Object value){
		this.convProperties.put(key, value);
	}

	/**
	 * Export current actor to unreal text format.
	 * @return Actor exported in unreal text format
	 */
	public abstract String toT3d();

	public void toT3dNew(int ueVersion){

		sbf.append("\tBegin Actor Class=").append(this.t3dClass).append(" Name=").append(this.name);

		// Archetype=StaticMeshActor'Engine.Default__StaticMeshActor'
		if (ueVersion == 3) {
			sbf.append(" Archetype=").append(this.t3dClass).append("'Engine.Default__").append(this.t3dClass).append("'");
		}
		sbf.append("\n");

		for (final Component comp : components) {

			if (ueVersion >= 4) {
				sbf.append("\t\tBegin Object Class=").append(comp.getComponentClass()).append(" Name=\"").append(comp.getName()).append("\"\n");
				sbf.append("\t\tEnd Object\n");
			}

			sbf.append("\t\tBegin Object Class=").append(comp.getComponentClass()).append(" Name=").append(comp.getName()).append(" ObjName=").append(comp.getObjName()).append("\n");

			for (Map.Entry<String, Object> entry : comp.getProperties().entrySet()) {
				sbf.append("\t\t\t").append(entry.getKey()).append("=").append(entry.getValue() != null ? entry.getValue().toString() : "None").append("\n");
			}

			// location is inside component for UE4
			if (ueVersion >= 4) {
				writeLocRotAndScale();
			}

			sbf.append("\t\tName=\"").append(comp.getName()).append("\"\n");
			sbf.append("\t\tEnd Object\n");
		}

		int idx = 0;

		for (final Component comp : components) {

			if (ueVersion == 3) {
				sbf.append("\t\t").append(comp.getComponentClass()).append("=").append(comp.getReference(ueVersion)).append("\n");
				sbf.append("\t\tComponents(").append(idx).append(")=").append(comp.getReference(ueVersion)).append("\n");
			} else if (ueVersion == 4) {
				if (idx == 0) {
					sbf.append("\t\t").append(comp.getComponentClass()).append("=").append(comp.getName()).append("\n");
					sbf.append("\t\tRootComponent=").append(comp.getName()).append("\n");
				} else {
					sbf.append("\t\tInstanceComponents(").append(idx - 1).append(")=").append(comp.getName()).append("\n");
				}
			}
			idx++;
		}

		// actor specific properties
		for (Map.Entry<String, Object> entry : convProperties.entrySet()) {
			sbf.append("\t\t").append(entry.getKey()).append("=").append(entry.getValue() != null ? entry.getValue().toString(): "None").append("\n");
		}

		if (ueVersion <= 3) {
			writeLocRotAndScale();
		}

		writeEndActor();
	}
}
