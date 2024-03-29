/*
$ * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import javafx.concurrent.Task;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.tools.FileUtils;
import org.xtx.ut4converter.controller.ConversionViewController;

import javax.vecmath.Vector3d;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.xtx.ut4converter.ucore.UnrealEngine.*;

/**
 * Converts T3D Unreal 1 / Unreal Tournament to Unreal Tournament "4" t3d file
 *
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class T3DLevelConvertor extends Task<Object> {


	/**
	 * Logger, should be the same as mapconverter logger
	 */
	private final Logger logger;

	/**
	 * Current map converter
	 */
	private final MapConverter mapConverter;

	/**
	 * Ignored unconverted properties that are not relevant to know they are not converted
	 * since there are useless ones.
	 */
	private static final List<String> IGNORED_UNCONVERTED_PROP = Arrays.asList("Region", "Level", "Brush", "Base", "PhysicsVolume", "myMarker", "StaticMeshInstance");

	/**
	 * Input t3d file that need conversion
	 */
	private final File inT3dFile;

	/**
	 * Converted input t3d file
	 */
	private final File outT3dFile;

	/**
	 * Actors that were not converted.
	 */
	private final SortedSet<String> unconvertedActors = new TreeSet<>();

	/**
	 * List of actors that were converted
	 */
	private final List<T3DActor> convertedActors = new ArrayList<>();

	/**
	 * If true will replace unconverted actors with Note actor
	 */
	private boolean createNoteForUnconvertedActors = true;

	private Vector3d levelDimension;

	private Vector3d boundBoxLocalisation;

	private int actorCount;

	/**
	 * If true will not update JavaFX progress and messages
	 */
	private boolean noUi;

	/**
	 * Unconverted properties
	 * Map[ActorClass, [Property1, Property2, ...]]
	 */
	private final Map<String, Set<String>> unconvertedProperties = new HashMap<>();

	/**
	 * If true means we don't analyze t3d lines of current actor being parsed
	 */
	boolean banalyseline = false;

	/**
	 * T3D actor being converted
	 */
	private T3DActor uta = null;

	private int actorsReadCount;


	/**
	 * Used for .t3d UE3 map file which only uses "Begin Object" to declare actor (and not Begin Actor)
	 * The first line of .t3d would be Begin Object Class=Level
	 */
	private int currentObjectLevel = 0;

	/**
	 * If true current t3d is using "Begin Object" to declare a new actor
	 */
	private Boolean isObjectActorClassOnly;

	/**
	 * @param originalT3d  Original t3d ut3 file
	 * @param convertedT3d New t3d file converted in UT4 t3d format
	 * @param mc           MapConverter options
	 * @param noUi         Set true if using from tests
	 */
	public T3DLevelConvertor(File originalT3d, File convertedT3d, MapConverter mc, boolean noUi) {

		this.inT3dFile = originalT3d;
		this.outT3dFile = convertedT3d;
		this.mapConverter = mc;
		this.mapConverter.setT3dLvlConvertor(this);
		this.logger = mc.getLogger();
		this.noUi = noUi;

		if (!this.noUi) {
			ConversionViewController cont = mapConverter.getConversionViewController();

			if (cont == null) {
				return;
			}

			cont.getProgressBarDetail().progressProperty().bind(progressProperty());
			cont.getProgressIndicatorDetail().progressProperty().bind(progressProperty());
			cont.getProgressMessageDetail().textProperty().bind(messageProperty());

			updateProgress(0, 100);
		}
	}


	/**
	 * Count the total number of actors. Used to track progress for conversion
	 */
	private void countTotalActors() throws IOException {

		if (inT3dFile == null || !inT3dFile.exists()) {
			assert inT3dFile != null;
			throw new IllegalArgumentException("File " + inT3dFile.getAbsolutePath() + " does not exists!");
		}

		final Charset charset = FileUtils.detectEncoding(inT3dFile);

		try (final FileInputStream fis = new FileInputStream(inT3dFile); final InputStreamReader isr = new InputStreamReader(fis, charset); final BufferedReader bfr = new BufferedReader(isr)) {

			String line;

			// Read input t3d file and convert actors
			while ((line = bfr.readLine()) != null) {
				if (isBeginActor(line)) {
					actorCount++;
				}
				// for ut3 triggering "End Object" thingy ...
				else {
					isEndActor(line);
				}
			}
		}
	}

	/**
	 * Converts t3d file for final game
	 *
	 * @throws IOException Error writing t3d file
	 */
	public void readConvertAndWrite() throws IOException {


		if (this.inT3dFile == null || !this.inT3dFile.exists()) {
			throw new IllegalArgumentException("T3D file not specified or not existing.");
		}

		countTotalActors();

		logger.info("Converting t3d map " + inT3dFile.getName() + " to " + mapConverter.getOutputGame().getName() + " t3d level");

		// 1 - Read actor data from file
		updateMessage("Reading actors");
		readActors();

		// 2 - Convert actors
		updateMessage("Converting actors");
		convertActors();

		if (mapConverter.getFilteredClasses() == null || mapConverter.getFilteredClasses().length == 0) {
			addMapConversionNotesInfo();
		}

		// 3 - Write to file
		updateMessage("Writing actors");
		writeActors();

		updateProgress(100, 100);
		updateMessage("All done!");
	}

	protected void updateProgress(long workDone, long max) {
		if(!noUi) {
			super.updateProgress(workDone, max);
		}
	}

	protected void updateMessage(String message) {
		if(!noUi) {
			super.updateMessage(message);
		}
	}

	/**
	 * Write info about original converted map
	 * and the scale ratior applied.
	 * Might be usefull if need to re-import actors
	 * to converted map, knowing which was the original scale.
	 */
	private void addMapConversionNotesInfo(){
		final T3DNote note = new T3DNote(mapConverter);
		note.setLocation(new Vector3d(0d, 200d, 0d));
		note.setName("NoteConvertedMap");
		note.setText("Converted Map: " + mapConverter.getInMap().getName());
		convertedActors.add(note);

		final T3DNote noteScale = new T3DNote(mapConverter);
		noteScale.setLocation(new Vector3d(0d, 300d, 0d));
		noteScale.setName("NoteScale");
		noteScale.setText("Scale: " + mapConverter.getScale());
		convertedActors.add(noteScale);
	}


	/**
	 * Find an actor by name
	 * @param actorName Actor name
	 * @return Actor found if any
	 */
	public T3DActor findActorByName(String actorName) {

		for (T3DActor uta : convertedActors) {
			if (uta != null && uta.getName() != null && uta.getName().equals(actorName)) {
				return uta;
			}
		}

		return null;
	}

	/**
	 * Write converted actors to .t3d file
	 *
	 * @throws IOException Exception thrown
	 */
	private void writeActors() throws IOException {

		updateProgress(0, convertedActors.size());

		try (final BufferedWriter bwr = new BufferedWriter(new FileWriter(outT3dFile))) {

			String buffer;
			long actorsWriten = 0;

			writeHeader(bwr);

			for (final T3DActor actor : convertedActors) {

				if (actor == null) {
					continue;
				}

				try {
					// write parent actor
					// actor not valid for conversion should not be written as well!
					if (actor.isValidWriting() && actor.isValidConverting()) {
						buffer = actor.toT3d();

						if (buffer != null) {
							bwr.write(buffer);
						}
					}

					// write replacement actors
					for (T3DActor repActor : actor.children) {
						if (repActor.isValidWriting()) {
							bwr.write(repActor.toT3d());
						}
					}
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error while writting actor " + actor.getName() + ":", e);
				}

				updateProgress(++actorsWriten, convertedActors.size());
			}

			writeFooter(bwr);
		}

		logger.info("Written file " + outT3dFile.getName());
	}

	/**
	 *
	 */
	private void convertActors() {

		int convertedActorsCount = 0;
		long xx = 80 - MapConverter.PROGRESS_BEFORE_CONVERT;
		updateProgress(0, convertedActors.size());

		for (T3DActor uta : convertedActors) {

			if (this.mapConverter.checkConversionCancelled()) {
				break;
			}

			if (uta != null) {

				try {
					if (uta.isValidConverting()) {
						// we might want to only re-scale map
						if (uta.getMapConverter().getOutputGame() != uta.getMapConverter().getInputGame()) {
							updateMessage("Converting " + uta.name);
							uta.convert();
						}
						// rescale if needed
						// must always be done after convert
						uta.scale(mapConverter.getScale());
					}
				}
				// we don't want to stop conversion of other actors if one actor fails to
				catch (Exception e) {
					logger.log(Level.WARNING, "Error converting actor " + uta.getName(), e);
					e.printStackTrace();
				}

			}

			convertedActorsCount ++;

			// update global conversion
			if(!noUi){
				float pcDone = ((float) convertedActorsCount) / convertedActors.size();
				long newProgress = (long) (MapConverter.PROGRESS_BEFORE_CONVERT + xx * pcDone);
				mapConverter.updateMapConverterProgress(newProgress, 100);
				updateProgress(convertedActorsCount, convertedActors.size());
			}
		}

		logger.info(convertedActors.size() + " converted actors ");
	}

	/**
	 * Read actor data from original t3d level file
	 *
	 * @throws IOException Exception thrown
	 */
	private void readActors() throws IOException {

		final Charset charset = FileUtils.detectEncoding(inT3dFile);

		try (final FileInputStream fis = new FileInputStream(inT3dFile); final InputStreamReader isr = new InputStreamReader(fis, charset); final BufferedReader bfr = new BufferedReader(isr)) {

			String line;
			// Current line of T3D File being analyzed
			int linenumber = 1;

			// Read input t3d file and convert actors
			while ((line = bfr.readLine()) != null) {

				if (this.mapConverter.checkConversionCancelled()) {
					break;
				}

				try {
					analyzeLine(line.trim());
					linenumber++;
				} catch (Exception e) {
					e.printStackTrace();
					logger.log(Level.WARNING, "Error parsing Line #" + linenumber + " for " + inT3dFile.getName());
					if (uta != null) {
						logger.log(Level.WARNING, "Current Actor Class: " + uta.t3dClass + " Line:");
					}
					logger.log(Level.WARNING, "\"" + line + "\"");
					logger.log(Level.WARNING, "ERROR:", e);
				}
			}

			logger.info(convertedActors.size() + " actors read");
		}
	}

	/**
	 * Says if current line is data for a new actor
	 *
	 * @param line t3d data line
	 * @return <code>true</code> if lines describe a new actor
	 */
	private boolean isBeginActor(String line) {

		if (line.startsWith("Begin Actor")) {
			isObjectActorClassOnly = Boolean.FALSE; // was null at init
			return true;
		} else if (this.mapConverter.getInputGame().getUeVersion() == 3) {
			// Terrain actors does not starts with "Begin Actor Class=Terrain"
			if (line.startsWith("Begin Terrain ")) {
				return true;
			}
			// UDK sometimes have issues exporting some actors to T3D
			// E.G:  Text="Begin Map\r\n   Begin Level\r\n      Begin Actor Class=BlockingVo...."
			// UDK starts any actor with "Begin Object" not with "Begin Actor Class=XXX"
			// and actor can also contains other objects
			else if (line.trim().startsWith("Begin Object") && (isObjectActorClassOnly == null || Boolean.TRUE.equals(isObjectActorClassOnly))) {
				currentObjectLevel++;

				// t3d map file starts with Begin Object Class=Level Name=PersistentLevel - = level 1
				/*
					Begin Object Class=Level Name=PersistentLevel
   						Begin Object Class=AdvancedReachSpec Name=AdvancedReachSpec_0
				 */
				// So actor is level 2
				if (currentObjectLevel == 2) {
					isObjectActorClassOnly = Boolean.TRUE;
					return true;
				}
				return false;
			}
		}

		return false;
	}

	/**
	 * Parses t3d line and return true if it ends actor data
	 *
	 * @param line T3d line to parse
	 * @return <code>true</code> if its last line of actor else <code>false</code>
	 */
	private boolean isEndActor(String line) {

		// Terrain actor does not begins with "Begin Actor Class=Terrain"
		if (line.startsWith("End Actor")) {
			return true;
		} else if (this.mapConverter.getInputGame().getUeVersion() == 3) {
			// Terrain actors does not starts with "Begin Actor Class=Terrain"
			if (line.startsWith("Begin Terrain ")) {
				return true;
			}
			// UDK ends any actor with "Begin Object"
			else if (line.trim().startsWith("End Object") && Boolean.TRUE.equals(isObjectActorClassOnly)) {
				currentObjectLevel--;
				return currentObjectLevel == 0;
			}
		}

		return false;
	}


	/**
	 * Parses t3d line
	 *
	 * @param line current T3D line being read
	 * @throws NoSuchMethodException     Exception thrown
	 * @throws InvocationTargetException Exception thrown
	 * @throws InstantiationException    Exception thrown
	 * @throws IllegalAccessException    Exception thrown
	 */
	private void analyzeLine(String line) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

		// Current actor class
		Class<? extends T3DActor> utActorClass;

		if (isBeginActor(line)) {
			actorsReadCount++;

			// UT3 terrain is not an actor
			String currentClass;
			if (line.startsWith("Begin Terrain ")) {
				currentClass = "Terrain";
			} else {
				currentClass = getActorClass(line);
			}

			if (mapConverter.getSupportedActorClasses().canBeConverted(currentClass)) {
				utActorClass = mapConverter.getSupportedActorClasses().getConvertActorClass(currentClass);
				banalyseline = true;

				if (utActorClass != null) {
					Constructor<? extends T3DActor> cons = utActorClass.getConstructor(MapConverter.class, String.class);
					uta = cons.newInstance(mapConverter, currentClass);
					uta.setT3dOriginClass(currentClass);
					convertedActors.add(uta);
					uta.analyseT3DData(line);
				}
			} else {
				// skips some useless/uneeded actors to notify unconverted (e.g:
				// pathnodes for UE4/UT4)
				if (!mapConverter.getSupportedActorClasses().noNotifyUnconverted(currentClass)) {
					unconvertedActors.add(currentClass);

					if (createNoteForUnconvertedActors) {
						banalyseline = true;
						uta = new T3DNote(mapConverter, "Unconverted: " + currentClass);
						uta.setT3dOriginClass(currentClass);
						uta.analyseT3DData(line);
						convertedActors.add(uta);
					} else {
						//logger.warning("Unconverted " + currentClass);
						banalyseline = false;
					}
				}
			}
		}

		// Actor End
		else if (isEndActor(line)) {

			updateProgress(actorsReadCount, actorCount);
			// Reset
			banalyseline = false;
			uta = null;
		}

		// Actor data being analyzed
		else {
			if (banalyseline && uta != null) {
				uta.preAnalyse(line);
				uta.analyseT3DData(line);
			}
		}
	}

	/**
	 * Returns current actor class from t3d line defining actor
	 *
	 * @param line t3d line
	 * @return Actor class
	 */
	private String getActorClass(String line) {
		return (line.split("=")[1]).split(" ")[0];
	}

	/**
	 * Name of the big additive brush automatically added by converter
	 */
	public static final String THE_BIG_BRUSH_NAME = "BigAdditiveBrush";

	/**
	 * Write header of T3D file TODO check for UE1/UE2
	 *
	 * @throws IOException Exception thrown
	 */
	private void writeHeader(final BufferedWriter bwr) throws IOException {

		bwr.write("Begin Map\n");
		bwr.write("\tBegin Level Name=" + mapConverter.getUt4ReferenceBaseFolder() + "/" + mapConverter.getOutMapName() + "\n");

		// Since UE3+ maps are in additive mode only unlike UE1/UE2 so need to add a big additive brush
		// to simulate creating a level in subtract mode (not existing in UE4
		// Don't add the brush if actor filter has brush actor class
		if ((mapConverter.isFrom(UE1, UE2) && mapConverter.isTo(UE3, UE4, UE5)) && (!mapConverter.hasClassFilter() || (mapConverter.hasClassFilter() && Arrays.asList(mapConverter.getFilteredClasses()).contains("Brush")))) {

			double offset = 200d;
			Vector3d boundBox = getLevelDimensions();

			T3DBrush additiveBrush = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
			additiveBrush.location = boundBoxLocalisation;
			additiveBrush.name = THE_BIG_BRUSH_NAME;
			additiveBrush.brushClass = T3DBrush.BrushClass.Brush;

			// no need accurate light map resolution for this brush since it will
			// be never seen by player
			for (T3DPolygon p : additiveBrush.getPolyList()) {
				p.setLightMapScale(2048);
			}

			bwr.write(additiveBrush.toT3d());
		}
	}

	/**
	 *
	 * @return Level dimensions
	 */
	private Vector3d getLevelDimensions() {

		if (levelDimension == null) {
			Vector3d max = new Vector3d(0d, 0d, 0d);
			Vector3d min = new Vector3d(0d, 0d, 0d);

			// get the max/min boundaries of brush vertices on whole level
			for (T3DActor actor : convertedActors) {

				if (actor instanceof T3DBrush brush) {

					Vector3d maxA = brush.getMaxVertexPos();
					Vector3d minA = brush.getMinVertexPos();

					max.x = Math.max(max.x, maxA.x);
					max.y = Math.max(max.y, maxA.y);
					max.z = Math.max(max.z, maxA.z);

					min.x = Math.min(min.x, minA.x);
					min.y = Math.min(min.y, minA.y);
					min.z = Math.min(min.z, minA.z);
				}
			}

			// box dimensions that would fit perfectly the level in
			levelDimension = new Vector3d();
			levelDimension.x = Math.abs(max.x) + Math.abs(min.x);
			levelDimension.y = Math.abs(max.y) + Math.abs(min.y);
			levelDimension.z = Math.abs(max.z) + Math.abs(min.z);

			Vector3d loc = new Vector3d();
			loc.x = (max.x + min.x) / 2;
			loc.y = (max.y + min.y) / 2;
			loc.z = (max.z + min.z) / 2;

			boundBoxLocalisation = loc;
		}

		return levelDimension;
	}


	/**
	 * Write footer of converted t3d file
	 * For UE4+ maps add a lightmassimportancevolume
	 *
	 * @throws IOException Error writing t3d file
	 */
	private void writeFooter(final BufferedWriter bwr) throws IOException {

		if (mapConverter.isTo(UE4, UE5)) {

			Vector3d boundBox = getLevelDimensions();
			double offset = 100d;

			if (!mapConverter.hasClassFilter()) {
				// Automatically add a lightMassVolume around the whole level
				T3DBrush lightMassVolume = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
				lightMassVolume.location = boundBoxLocalisation;
				lightMassVolume.name = "LightMassImpVolume";
				lightMassVolume.brushClass = T3DBrush.BrushClass.LightmassImportanceVolume;
				bwr.write(lightMassVolume.toT3d());
			}
		}

		bwr.write("\tEnd Level\n");
		bwr.write("\tBegin Surface\n");
		bwr.write("\tEnd Surface\n");
		bwr.write("End Map\n");
	}

	public void setCreateNoteForUnconvertedActors(boolean createNoteForUnconvertedActors) {
		this.createNoteForUnconvertedActors = createNoteForUnconvertedActors;
	}

	public Map<String, Set<String>> getUnconvertedProperties() {
		return unconvertedProperties;
	}

	/**
	 * Logs unconverted property from actor.
	 * Will help convertings these properties if they have not been converted yet!
	 * @param t3dClass T3d class of actor
	 * @param property Property name of actor that was not converted.
	 */
	void logUnconvertedProperty(final String t3dClass, final String property) {

		if(IGNORED_UNCONVERTED_PROP.contains(property)){
			return;
		}

		if (unconvertedProperties.containsKey(t3dClass)) {
			unconvertedProperties.get(t3dClass).add(property);
		} else {
			Set<String> props = new HashSet<>();
			props.add(property);
			unconvertedProperties.put(t3dClass, props);
		}
	}

	public SortedSet<String> getUnconvertedActors() {
		return unconvertedActors;
	}

	@Override
	protected Object call() throws Exception {
		readConvertAndWrite();
		return null;
	}

	public List<T3DActor> getConvertedActors() {
		return convertedActors;
	}

	public void setNoUi(boolean noUi) {
		this.noUi = noUi;
	}
}
