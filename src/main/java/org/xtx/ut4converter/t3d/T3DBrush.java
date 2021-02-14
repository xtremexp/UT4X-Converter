/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.geom.Vertex;
import org.xtx.ut4converter.tools.Geometry;
import org.xtx.ut4converter.ucore.ue1.BrushPolyflag;
import org.xtx.ut4converter.ucore.ue1.UnMath.ESheerAxis;
import org.xtx.ut4converter.ucore.ue1.UnMath.FScale;
import org.xtx.ut4converter.ucore.ue1.UnMath.FVector;

import javax.vecmath.Vector3d;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Generic Class for T3D brushes (includes movers as well)
 * 
 * @author XtremeXp
 */
public class T3DBrush extends T3DVolume {

	protected BrushClass brushClass = BrushClass.Brush;

	public enum BrushClass {

		// UE1, UE2 Volume
		Brush, Mover, KillZVolume, UTPainVolume, UTWaterVolume, BlockingVolume, WaterVolume,
		LadderVolume, PressureVolume, SnipingVolume, ConvoyPhysicsVolume, xFallingVolume, IonCannonKillVolume,
		HitScanBlockingVolume, ASCriticalObjectiveVolume, LeavingBattleFieldVolume,

		// UE3 and UE4 Volumes
		PostProcessVolume, TriggerVolume, UTSlimeVolume, LavaVolume, UTLavaVolume, UTKillZVolume, CullDistanceVolume,
		/**
		 * TODO for UE3 -> UE4 convert to PhysicsVolume + PainVolume
		 */
		PhysicsVolume,
		// Specific UE3 Volumes

		/**
		 * Has been replaced by AudioVolume in UE4
		 */
		ReverbVolume, DynamicTriggerVolume,
		// Specific UE4 Volume
		AudioVolume, PainCausingVolume, LightmassImportanceVolume, NavMeshBoundsVolume,

		// UT4 specific volume
		UTGameVolume, UTCustomPhysicsVolume, UTNoCameraVolume, UTNavBlockingVolume
		;

		public static BrushClass getBrushClass(String t3dBrushClass) {

			for (BrushClass bc : BrushClass.values()) {
				if (bc.name().equals(t3dBrushClass)) {
					return bc;
				}
			}

			return BrushClass.Brush;
		}
	}

	/**
	 * UE1/2/3
	 */
	private enum UE123_BrushType {
		CSG_Active, CSG_Add, CSG_Subtract, CSG_Intersect, CSG_Deintersect
	}

	/**
	 * UE4
	 */
	private enum UE4_BrushType {
		Brush_Subtract, Brush_Add
	}

	private String brushType;

	/**
	 * Type of brush (regular, portal, semi-solid, ...) UE1/UE2 only
	 */
	private List<BrushPolyflag> polyflags = new ArrayList<>();

	/**
	 * UE1/2 property containing scale and sheer info
	 */
	private FScale mainScale;
	
	/**
	 * UE1/2 property containing scale and sheer info
	 */
	private FScale postScale;

	/**
	 * Used by Unreal Engine 1
	 */
	private Vector3d tempScale;

	/**
	 * Pre-Pivot used for brushes Changed the relative origin of brush
	 */
	private Vector3d prePivot;

	/**
	 * Polygons of the brush
	 */
	private LinkedList<T3DPolygon> polyList = new LinkedList<>();

	/**
	 * E.G: "Begin Brush Name=TestLev_S787"
	 */
	private String modelName;

	/**
	 * Damage par sec for pain causing volumes (lava, slime ...)
	 */
	private Float damagePerSec;

	/**
	 * Used for UT pain volumes
	 */
	private Boolean physicsVolumeebWaterVolume;

	/**
	 * Used for UT pain volumes
	 */
	private Float physicsVolumeFluidFriction;
	
	/**
	 * Used for cull distances only
	 */
	private List<CullDistance> cullDistances;
	
	/**
	 * 
	 * Used for cull distances volumes only
	 *
	 */
	static class CullDistance {
		private Double size;
		private Double distance;

		public void scale(Double scale) {
			if (size != null) {
				size *= scale;
			}
			if (distance != null) {
				distance *= scale;
			}
		}
	}

	
	public T3DBrush(MapConverter mapConverter, String t3dClass) {
		super(mapConverter, t3dClass);

		init();
	}

	/**
	 *
	 * @param mapConverter
	 * @param t3dClass
	 * @param actor Used if creating brush from another type of actor (like zoneinfo for postprocessvolume, ...)
	 */
	public T3DBrush(MapConverter mapConverter, String t3dClass, T3DActor actor) {
		super(mapConverter, t3dClass, actor);

		init();
	}
	
	private void init(){
		brushClass = BrushClass.getBrushClass(t3dClass);

		if (mapConverter.fromUE1orUE2OrUE3()) {
			brushType = UE123_BrushType.CSG_Add.name();
		} else {
			brushType = UE4_BrushType.Brush_Add.name();
		}

		// just need one
		modelName = "Model_6";
	}

	/**
	 * If true reverse the order of vertices when writting converted t3d. This
	 * is due to MainScale factor. Depending on it it.
	 */
	boolean reverseVertexOrder = false;


	boolean isSheetFlatHorizontallyBrush;

	@Override
	public boolean analyseT3DData(String line) {

		// CsgOper=CSG_Subtract
		// BrushType=Brush_Subtract
		if (line.contains("CsgOper")) {
			brushType = line.split("=")[1];
		}

		// MainScale=(Scale=(Y=-1.000000),SheerAxis=SHEER_ZX)
		// MainScale=(SheerAxis=SHEER_ZX)
		else if (line.contains("MainScale=")) {
			mainScale = new FScale();

			if(line.contains("(Scale=")){
				mainScale.scale = new FVector(T3DUtils.getVector3d(line.split("\\(Scale")[1], 1D));
			}
			
			if(line.contains("SheerAxis=")){
				mainScale.sheerAxis = ESheerAxis.valueOf(T3DUtils.getString(line, "SheerAxis"));
			}
			
			if(line.contains("SheerRate=")){
				mainScale.sheerRate = T3DUtils.getFloat(line, "SheerRate");
			}
			
			reverseVertexOrder = mainScale.scale.x * mainScale.scale.y * mainScale.scale.z < 0;
		}


		// PostScale=(Scale=(X=1.058824,Y=1.250000,Z=0.920918),SheerAxis=SHEER_ZX)
		else if (line.contains("PostScale=")) {
			postScale = new FScale();
			
			
			if(line.contains("(Scale=")){
				postScale.scale = new FVector(T3DUtils.getVector3d(line.split("\\(Scale")[1], 1D));
			}
			
			if(line.contains("SheerAxis=")){
				postScale.sheerAxis = ESheerAxis.valueOf(T3DUtils.getString(line, "SheerAxis"));
			}
			
			if(line.contains("SheerRate=")){
				postScale.sheerRate = T3DUtils.getFloat(line, "SheerRate");
			}
		}

		// TempScale=(Scale=(X=0.483090,Y=2.274808,Z=0.488054))
		else if (line.contains("TempScale") && line.contains("(Scale=")) {
			tempScale = T3DUtils.getVector3d(line.split("\\(Scale")[1], 1D);
		}

		else if (line.contains("PrePivot")) {
			prePivot = T3DUtils.getVector3d(line, 0D);
		}

		else if (line.contains("PolyFlags=")) {
			polyflags = BrushPolyflag.parse(T3DUtils.getInteger(line));
		}

		// Begin Polygon Item=Rise Texture=r-plates-g Link=0
		else if (line.contains("Begin Polygon")) {
			polyList.add(new T3DPolygon(line, mapConverter));
		}

		// Origin -00128.000000,-00128.000000,-00128.000000
		else if (line.contains("Origin ")) {
			polyList.getLast().origin = T3DUtils.getPolyVector3d(line, "Origin");
		}

		else if (line.contains("Normal ")) {
			polyList.getLast().normal = T3DUtils.getPolyVector3d(line, "Normal");
		}

		else if (line.contains("TextureU ")) {
			polyList.getLast().setTexture_u(T3DUtils.getPolyVector3d(line, "TextureU"));
		}

		else if (line.contains("TextureV ")) {
			polyList.getLast().setTexture_v(T3DUtils.getPolyVector3d(line, "TextureV"));
		}

		else if (line.contains("Vertex ")) {
			Vector3d coordinates = T3DUtils.getPolyVector3d(line, "Vertex");
			polyList.getLast().vertices.add(new Vertex(coordinates));
		}

		// Pan U=381 V=-7
		else if (line.contains(" Pan ")) {
			polyList.getLast().pan_u = Double.parseDouble(line.split("U=")[1].split(" ")[0]);
			polyList.getLast().pan_v = Double.parseDouble(line.split("V=")[1].split(" ")[0]);
		}

		// Hack, normally analysed in T3DActor but needed
		// for waterzone, lavazone to be converted ...
		else if (line.contains("Begin Actor")) {

			if (isU1ZoneVolume(t3dClass)) {
				forceToBox(90d);
			}

			// need force trigger function else name is null
			return super.analyseT3DData(line);
		}

		else if (line.startsWith("Begin Brush") && line.contains("Name=")) {
			// modelName = line.split("Name=")[1].replaceAll("\"", "");
		}
		
		// CullDistances(1)=(Size=64.000000,CullDistance=3000.000000)
		else if (line.startsWith("CullDistances(")) {
			if (cullDistances == null) {
				cullDistances = new ArrayList<T3DBrush.CullDistance>();
			}

			CullDistance cullDistance = new CullDistance();
			if (line.contains("Size=")) {
				cullDistance.size = Double.valueOf(line.split("Size=")[1].split("\\)")[0].split(",")[0]);
			}

			if (line.contains("CullDistance=")) {
				cullDistance.distance = Double.valueOf(line.split("CullDistance=")[1].split("\\)")[0].split(",")[0]);
			}

			cullDistances.add(cullDistance);
		}

		else if(line.startsWith("DamagePerSec=")){
			this.damagePerSec = T3DUtils.getFloat(line);
		}

		else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	/**
	 * 
	 * @param t3dBrushClass
	 * @return
	 */
	private boolean isU1ZoneVolume(String t3dBrushClass) {

		if (t3dBrushClass.equals(BrushClass.Brush.name())) {
			return false;
		}

		else if (t3dBrushClass.equals("LavaZone") || t3dBrushClass.equals("SlimeZone") || t3dBrushClass.equals("VaccuumZone") || t3dBrushClass.equals("NitrogenZone") || t3dBrushClass.equals(BrushClass.UTSlimeVolume.name()) || t3dBrushClass.equals(BrushClass.UTLavaVolume.name()) || t3dBrushClass.equals(BrushClass.LavaVolume.name())) {
			brushClass = BrushClass.UTPainVolume;
			this.damagePerSec = 20f;
			return true;
		}

		else if (t3dBrushClass.equals("WaterZone")) {
			brushClass = BrushClass.UTWaterVolume;
			return true;
		}

		return false;
	}

	@Override
	public boolean isValidConverting() {

		boolean valid = true;

		if (mapConverter.fromUE123ToUE4()) {
			// do not convert invisible brushes such as portals and so on
			if (BrushPolyflag.hasInvisibleFlag(polyflags)) {
				//logger.warning("Skipped invisible brush " + name);
				valid = false;
			}

			if (UE123_BrushType.valueOf(brushType) == UE123_BrushType.CSG_Active || UE123_BrushType.valueOf(brushType) == UE123_BrushType.CSG_Intersect
					|| UE123_BrushType.valueOf(brushType) == UE123_BrushType.CSG_Deintersect) {
				logger.warning("Skipped unsupported CsgOper '" + brushType + "' in " + mapConverter.getUnrealEngineTo().name() + " for " + name);
				valid = false;
			}
		}

		return valid && super.isValidConverting();
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean isValidWriting() {

		boolean valid = true;

		// is first "red" brush in previous UE editors
		if ("Brush".equals(name)) {
			return false;
		}

		if (mapConverter.fromUE123ToUE4()) {


			// BUG UE4 DON'T LIKE SHEETS brushes or "Light Brushes" mainly
			// coming from UE1/UE2 ...
			// Else geometry building got holes so need to get rid of them ...
			// TODO add note? (some sheet brushes are movers ...)
			// TODO replace sheetbrush with sheet staticmesh
			// if 2 polygons and sheetbrush not a portal
			// only notify if this brush could not be replaced by another
			// actor
			if (isUnsupportedUE4Brush() && children.isEmpty()) {
				// for movers convert them else might impact gameplay events
				if (this.brushClass == BrushClass.Mover) {
					logger.warning(name + " will cause BSP holes unless converted to staticmesh.");
				} else {
					valid = false;
					logger.warning("Skipped unsupported 'sheet brush' in " + mapConverter.getUnrealEngineTo().name() + " for " + name);
				}
			}
		}

		return valid && super.isValidWriting();
	}

	/**
	 * Tells if current brush is sheet brush: - one polygon - 4 vertices for
	 * this polygon
	 * 
	 * @return
	 */
	private boolean isSheetBrush() {
		return polyList.size() == 1 && polyList.get(0).vertices.size() == 4;
	}



	private boolean isHorizontallyFlatBrush(){
		for(final T3DPolygon p : polyList){
			if(p.getVertices().stream().map(Vertex::getZ).distinct().count() > 1){
				return false;
			}
		}

		return true;
	}

	/**
	 * Detect if this current brush is not supported by Unreal Engine 4. This
	 * kind of brush makes bsp holes on import. Generally is a "flat" brush used
	 * in Unreal Engine 1 / 2 as a "Torch", "Water surface" and so on ... 1 poly
	 * = sheet brush 2+ poly (generally torch)
	 * 
	 * @return <code>true</code> If this brush is a sheet brush
	 */
	protected boolean isUnsupportedUE4Brush() {

		return polyList.size() <= 4 && !this.polyflags.contains(BrushPolyflag.SEMI_SOLID) && !this.polyflags.contains(BrushPolyflag.NON_SOLID);
		// FIXME all sheet brushes are well deleted but some (a very few)
		// normal brushes are detected as sheet ones (eg.: stairs / test
		// AS-HighSpeed)
		// so they are not being converted.
		// so the test is not reliable yet
		// for each vertices we check that it is linked to 3 polygons
		/*
		 * for(T3DPolygon poly : polyList){ for(Vector3d v : poly.vertices){
		 * if(getPolyCountWithVertexCoordinate(v) < 3){ return true; } } }
		 * return false;
		 */
	}

	/**
	 * Compute if this brush will cause bsp holes: - only 4 or less polygon - OR
	 * some vertex not bound with at least 3 polygons not enough accurate at
	 * this stage
	 * 
	 * @return <code>true</code> if this brush will cause bsp holes on import
	 */
	private boolean willCauseBspHoles() {

		// sheet brush (total nb polygons <= 4)
		if (polyList.size() <= 4) {
			return true;
		}

		for (T3DPolygon poly : polyList) {
			for (Vertex v : poly.vertices) {
				// vertex only linked with 2 or less other vertices
				// might be candidate for bsp hole
				// however need to do some extra checks
				if (getPolyCountWithVertexCoordinate(v) < 3 && !Geometry.vertexInOtherPoly(polyList, poly, v)) {

					// if this vertex is belonging to edge
					// of another polygon
					return true;
				}
			}
		}

		return false;
	}

	final double VERY_TINY_NUM = 0.001d;

	/**
	 * Return how many polygons are attached to this vertex.
	 * 
	 * @param v
	 *            Brush vertex
	 * @return Number of polygons attached to this vertex.
	 */
	private int getPolyCountWithVertexCoordinate(Vertex v) {

		int count = 0;

		for (T3DPolygon poly : polyList) {

			for (Vertex v2 : poly.vertices) {

				if (Math.abs(v.getX() - v2.getX()) < VERY_TINY_NUM && Math.abs(v.getY() - v2.getY()) < VERY_TINY_NUM && Math.abs(v.getZ() - v2.getZ()) < VERY_TINY_NUM) {
					count++;
					break;
				}
			}
		}

		return count;
	}

	/**
     *
     */
	public static DecimalFormat df = new DecimalFormat("+00000.000000;-00000.000000", new DecimalFormatSymbols(Locale.US));

	/**
	 *
	 * @return
	 */
	public String toT3d() {

		sbf.append(IDT).append("Begin Actor Class=").append(brushClass.name()).append(" Name=").append(name).append("\n");

		// Location Data
		sbf.append(IDT).append("\tBegin Object Name=\"BrushComponent0\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		if(this.damagePerSec != null){
			sbf.append(IDT).append("\tDamagePerSec=").append(this.damagePerSec).append("\n");
		}

		if(this.physicsVolumeebWaterVolume != null){
			sbf.append(IDT).append("\tbWaterVolume=").append(this.physicsVolumeebWaterVolume).append("\n");
		}

		if(this.physicsVolumeFluidFriction != null){
			sbf.append(IDT).append("\tFluidFriction=").append(this.physicsVolumeFluidFriction).append("\n");
		}

		sbf.append(IDT).append("\tBrushType=").append(UE123_BrushType.valueOf(brushType) == UE123_BrushType.CSG_Add ? UE4_BrushType.Brush_Add : UE4_BrushType.Brush_Subtract).append("\n");

		if (this.polyflags.contains(BrushPolyflag.SEMI_SOLID)) {
			sbf.append(IDT).append("\tPolyFlags=").append(BrushPolyflag.SEMI_SOLID.getPow()).append("\n");
		} else if (this.polyflags.contains(BrushPolyflag.NON_SOLID)) {
			sbf.append(IDT).append("\tPolyFlags=").append(BrushPolyflag.NON_SOLID.getPow()).append("\n");
		}

		// UE3 only CullDistanceVolume
		if (brushClass == BrushClass.CullDistanceVolume && cullDistances != null) {

			int idx = 0;
			boolean hasCullDistProp = false;

			for (CullDistance cullDistance : cullDistances) {
				// CullDistances(1)=(Size=64.000000,CullDistance=3000.000000)
				sbf.append(IDT).append("\tCullDistances(").append(idx).append(")=(");
				if (cullDistance.size != null) {
					hasCullDistProp = true;
					sbf.append("Size=").append(cullDistance.size).append(",");
				}
				if (cullDistance.distance != null) {
					hasCullDistProp = true;
					sbf.append("CullDistance=").append(cullDistance.distance).append(",");
				}
				if (hasCullDistProp) {
					sbf.deleteCharAt(sbf.length() - 1);
				}
				sbf.append(")\n");
				idx++;
			}
		}
		
		sbf.append(IDT).append("\tBegin Brush Name=").append(modelName).append("\n");
		sbf.append(IDT).append("\t\tBegin PolyList\n");

		int numPoly = 0;

		for (T3DPolygon t3dPolygon : polyList) {

			if (reverseVertexOrder) {
				t3dPolygon.reverseVertexOrder();
			}

			t3dPolygon.toT3D(sbf, df, IDT, numPoly);
			numPoly++;
		}

		sbf.append(IDT).append("\t\tEnd PolyList\n");
		sbf.append(IDT).append("\tEnd Brush\n");

		sbf.append(IDT).append("\tBrush=Model'").append(modelName).append("'\n");
		sbf.append(IDT).append("\tBrushComponent=BrushComponent0\n");

		for (String line : forcedWrittenLines) {
			sbf.append(IDT).append(line).append("\n");
		}
		
		// write specific properties of post process volume brush subclass
		if(this instanceof T3DPostProcessVolume){
			T3DPostProcessVolume ppv = (T3DPostProcessVolume) this;
			ppv.writeProps();
		}

		writeEndActor();

		// UT3 has postprocess volumes
		// TODO merge/refactor/move to T3DPostProcessVolume class
		if ((mapConverter.getInputGame().engine.version < UnrealEngine.UE3.version) && (brushClass == BrushClass.UTWaterVolume || brushClass == BrushClass.UTSlimeVolume)) {

			// add post processvolume
			T3DBrush postProcessVolume = createBox(mapConverter, 95d, 95d, 95d);
			postProcessVolume.brushClass = BrushClass.PostProcessVolume;
			postProcessVolume.name = this.name + "PPVolume";
			postProcessVolume.location = this.location;

			if (null != t3dClass)
				switch (t3dClass) {
					case "SlimeZone":
					case "UTSlimeVolume":
						// slimy ppv copied/pasted from DM-DeckTest (UT4)
						postProcessVolume.forcedWrittenLines
								.add("Settings=(bOverride_FilmWhitePoint=True,bOverride_AmbientCubemapIntensity=True,bOverride_DepthOfFieldMethod=True,FilmWhitePoint=(R=0.700000,G=1.000000,B=0.000000,A=1.000000),FilmShadowTint=(R=0.000000,G=1.000000,B=0.180251,A=1.000000),AmbientCubemapIntensity=0.000000,DepthOfFieldMethod=DOFM_Gaussian)");
						break;
					case "WaterZone":
						postProcessVolume.forcedWrittenLines.add("Settings=(bOverride_FilmWhitePoint=True,bOverride_BloomIntensity=True,FilmWhitePoint=(R=0.189938,G=0.611443,B=1.000000,A=0.000000))");
						break;
				}

			sbf.append(postProcessVolume.toString());

			// TODO add sheet surface
		}

		return super.toString();
	}

	/**
	 * Creates a brush box.
	 * 
	 * @param mc
	 *            Map Converter
	 * @param width
	 * @param length
	 * @param height
	 * @return
	 */
	public static T3DBrush createBox(MapConverter mc, Double width, Double length, Double height) {

		T3DBrush volume = new T3DBrush(mc, BrushClass.Brush.name());
		volume.polyList = Geometry.createBox(width, length, height);

		return volume;
	}

	/**
	 * Force brush to be a box
	 * 
	 * @param size Size
	 */
	public void forceToBox(Double size) {
		polyList.clear();

		polyList = Geometry.createBox(size, size, size);
	}

	/**
	 * Creates a cylinder brush
	 * 
	 * @param mc
	 *            Map Converter
	 * @param radius
	 *            Radius of cylinder
	 * @param height
	 *            Height
	 * @param sides
	 *            Number of sides for cylinder
	 * @return
	 */
	public static T3DBrush createCylinder(MapConverter mc, Double radius, Double height, int sides) {

		T3DBrush volume = new T3DBrush(mc, BrushClass.Brush.name());
		volume.polyList.clear();
		volume.polyList = Geometry.createCylinder(radius, height, sides);

		return volume;
	}

	/**
     *
     */
	@Override
	public void convert() {

		if ("BlockAll".equals(t3dClass)) {
			brushClass = BrushClass.BlockingVolume;
			if(collisionRadius == null){
				collisionRadius = 10d; // default value in UE1/UE2
			}
			
			if(collisionHeight == null){
				collisionHeight = 10d;
			}

			polyList = Geometry.createCylinder(collisionRadius, collisionHeight, 8);
			super.convert();
		}
		
		// UT3
		if(brushClass == BrushClass.UTKillZVolume || brushClass == BrushClass.xFallingVolume){
			brushClass = BrushClass.KillZVolume;
		}
		
		// UT2004
		else if(brushClass == BrushClass.WaterVolume){
			brushClass = BrushClass.UTWaterVolume;
		}
		
		// TODO handle other properties of volume like friction, ...
		// maybe add a super-class T3DVolume?
		// UTSlimeVolume does not exists in UT4
		else if (brushClass == BrushClass.UTSlimeVolume || brushClass == BrushClass.UTLavaVolume || brushClass == BrushClass.LavaVolume || brushClass == BrushClass.PressureVolume) {

			if(brushClass == BrushClass.PressureVolume){
				this.physicsVolumeebWaterVolume = Boolean.FALSE;
			}

			brushClass = BrushClass.UTPainVolume;

			if(this.damagePerSec == null){
				this.damagePerSec = 20f;
			}
		}

		if (mapConverter.isFromUE1UE2ToUE3UE4()) {
			transformPermanently();
		}

		// Update Location if prepivot set
		if (prePivot != null) {

			prePivot.negate();
			// location = location - prepivot
			if (location == null) {
				location = prePivot;
			} else {
				location.add(prePivot);
			}

			prePivot = null;
		}

		// TODO check texture alignement after convert
		for (T3DPolygon p : polyList) {
			p.convert();
		}

		// Replace Sheet Brush with Sheet StaticMesh
		// only if it is not a mover
		// will cause bdp hole first, but since movers are then converted to staticmeshes
		// in UT4 editor, won't cause bsphole
		if (isSheetBrush() && !this.polyflags.contains(BrushPolyflag.SEMI_SOLID) && !this.polyflags.contains(BrushPolyflag.NON_SOLID)) {
			if(!isHorizontallyFlatBrush()) {
				T3DStaticMesh sheetStaticMesh = new T3DStaticMesh(mapConverter, this);
				children.add(sheetStaticMesh);
			} else {
				isSheetFlatHorizontallyBrush = true;
			}
		}

		if (mapConverter.isTo(UnrealEngine.UE4)) {
			// ReverbVolume replaced with audio volume from ut3 to ut4
			// TODO convert specific properties of these volume
			if (brushClass == BrushClass.ReverbVolume) {
				brushClass = BrushClass.AudioVolume;
			}
			if (brushClass == BrushClass.DynamicTriggerVolume) {
				brushClass = BrushClass.TriggerVolume;
			}
		}

		super.convert();
	}

	/**
	 * TransformPermanently a brush in UT/U1 Editor After that rotation,
	 * mainscale and postscale are 'reset' Origin = (Origin * MainScale x
	 * Rotate) * PostScale PrePivot = PrePivot * Rotation Normal = Normal *
	 * Rotation MainScale * Vector -> Rotation * Vector -> PostScale * Vector
	 */
	private void transformPermanently() {

		if (prePivot != null) {
			Geometry.transformPermanently(prePivot, mainScale, rotation, postScale, false);
		}

		for (T3DPolygon polygon : polyList) {
			polygon.transformPermanently(mainScale, rotation, postScale);
		}

		rotation = null;
		mainScale = null;
		postScale = null;

		// TODO see purpose of TempScale
		// after tp in Unreal Engine, it is still set
	}

	/**
	 * Rescale brush. Must be done always after convert
	 */
	@Override
	public void scale(Double newScale) {
		
		if (cullDistances != null) {
			for (CullDistance cullDistance : cullDistances) {
				cullDistance.scale(newScale);
			}
		}

		for (T3DPolygon polygon : polyList) {
			polygon.scale(newScale);
		}

		super.scale(newScale);
	}

	/**
	 * Returns the max position of vertex belonging to this brush.
	 * 
	 * @return Max position
	 */
	public Vector3d getMaxVertexPos() {

		Vector3d max = new Vector3d(0d, 0d, 0d);

		for (T3DPolygon p : polyList) {
			for (Vertex v : p.vertices) {

				Vector3d c = v.getCoordinates();
				max.x = Math.max(max.x, c.x);
				max.y = Math.max(max.y, c.y);
				max.z = Math.max(max.z, c.z);
			}
		}

		if (location != null) {
			max.x = Math.max(max.x, location.x + max.x);
			max.y = Math.max(max.y, location.y + max.y);
			max.z = Math.max(max.z, location.z + max.z);
		}

		return max;
	}

	/**
	 * Returns the min position of vertex belonging to this brush.
	 * 
	 * @return Min position
	 */
	public Vector3d getMinVertexPos() {

		Vector3d min = new Vector3d(0d, 0d, 0d);

		for (T3DPolygon p : polyList) {
			for (Vertex v : p.vertices) {

				Vector3d c = v.getCoordinates();
				min.x = Math.min(min.x, c.x);
				min.y = Math.min(min.y, c.y);
				min.z = Math.min(min.z, c.z);
			}
		}

		if (location != null) {
			min.x = Math.min(min.x, location.x + min.x);
			min.y = Math.min(min.y, location.y + min.y);
			min.z = Math.min(min.z, location.z + min.z);
		}

		return min;
	}

	/**
	 * Sets polygons to this brush
	 * 
	 * @param polyList Polygon list
	 */
	public void setPolyList(LinkedList<T3DPolygon> polyList) {
		this.polyList = polyList;
	}

	public LinkedList<T3DPolygon> getPolyList() {
		return polyList;
	}

	public List<BrushPolyflag> getPolyflags() {
		return polyflags;
	}
}
