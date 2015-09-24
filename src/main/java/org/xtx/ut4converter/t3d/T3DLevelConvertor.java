/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGameTypes;
import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.UTGames.UnrealEngine;
import org.xtx.ut4converter.ui.ConversionViewController;

/**
 * Converts T3D Unreal 1 / Unreal Tournament to Unreal Tournament "4" t3d file
 * 
 * @author XtremeXp
 */
@SuppressWarnings("restriction")
public class T3DLevelConvertor extends Task<Object> {

	/**
	 * Current map converter
	 */
	MapConverter mapConverter;

	/**
	 * Input t3d file that need conversion
	 */
	File inT3dFile;

	/**
	 * Converted input t3d file
	 */
	File outT3dFile;

	/**
	 * Reader for input t3d file
	 */
	BufferedReader bfr;

	/**
	 * Writer for converted t3d file
	 */
	BufferedWriter bwr;

	/**
	 * String cache for converted t3d actors. Converted actor is sent to the
	 * writer.
	 */
	StringBuilder sbf;

	/**
	 * Actors that were not converted.
	 */
	public SortedSet<String> unconvertedActors = new TreeSet<>();

	LinkedList<T3DActor> convertedActors = new LinkedList<>();

	/**
	 * Assault objectives. Declared here so we can set out the good "order"
	 * prop. TODO move out to proper class
	 */
	SortedMap<Integer, T3DASObjective> objectives = new TreeMap<>();

	boolean createNoteWhenUnconverted = true;

	Logger logger;

	Vector3d levelDimension;

	Vector3d boundBoxLocalisation;

	int actorCount;

	/**
	 * Set objective order from DefaultPriority UT99 prop from FortStandard
	 * actors
	 */
	private void setAssaultObjectiveOrder() {

		int order = objectives.size() - 1;

		for (T3DASObjective obj : objectives.values()) {
			obj.order = order;
			order--;
		}
	}

	/**
	 * 
	 * @param originalT3d
	 *            Original t3d ut3 file
	 * @param convertedT3d
	 *            New t3d file converted in UT4 t3d format
	 * @param mc
	 *            MapConverter options
	 */
	public T3DLevelConvertor(File originalT3d, File convertedT3d, MapConverter mc) {

		this.inT3dFile = originalT3d;
		this.outT3dFile = convertedT3d;
		this.mapConverter = mc;
		this.mapConverter.setT3dLvlConvertor(this);
		this.logger = mc.getLogger();

		initialise();
	}

	/**
     * 
     */
	private void initialise() {

		ConversionViewController cont = mapConverter.getConversionViewController();

		if (cont == null) {
			return;
		}

		cont.getProgressBarDetail().progressProperty().bind(progressProperty());
		cont.getProgressIndicatorDetail().progressProperty().bind(progressProperty());
		cont.getProgressMessageDetail().textProperty().bind(messageProperty());

		updateProgress(0, 100);
	}

	/**
	 * Count the total number of actors. Used to track progress for conversion
	 */
	private void countTotalActors() throws Exception {

		if (inT3dFile == null || !inT3dFile.exists()) {
			throw new Exception("File " + inT3dFile.getAbsolutePath() + " does not exists!");
		}

		try {

			bfr = new BufferedReader(new FileReader(inT3dFile));
			String line;

			// Read input t3d file and convert actors
			while ((line = bfr.readLine()) != null) {
				if (isBeginActor(line)) {
					actorCount++;
				}
				// for ut3 triggering "End Object" thingy ...
				else if (isEndActor(line)) {

				}
			}
		} finally {
			bfr.close();
		}

	}

	/**
	 * Converts t3d file for final game
	 * 
	 * @throws Exception
	 */
	public void convert() throws Exception {

		if (inT3dFile == null || !inT3dFile.exists()) {
			throw new Exception("File " + inT3dFile.getAbsolutePath() + " does not exists!");
		}

		try {
			countTotalActors();
		} catch (Exception e) {
		}

		logger.info("Converting t3d map " + inT3dFile.getName() + " to " + mapConverter.getOutputGame().name + " t3d level");

		try {

			bfr = new BufferedReader(new FileReader(inT3dFile));
			bwr = new BufferedWriter(new FileWriter(outT3dFile));

			/**
			 * Current line of T3D File being analyzed
			 */
			int linenumber = 1;
			String line;

			// Read input t3d file and convert actors
			while ((line = bfr.readLine()) != null) {
				try {
					analyzeLine(line);
					linenumber++;
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Error while parsing data from T3D Level File " + inT3dFile.getName());
					logger.log(Level.SEVERE, "Line #" + linenumber + " Current Actor Class: " + uta.t3dClass + " Line:");
					logger.log(Level.SEVERE, "\"" + line + "\"");
					logger.log(Level.SEVERE, "ERROR:", e);
				}
			}

			// Write T3D converted file
			write(bwr);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error while writting T3D converted level file " + inT3dFile.getName());
			logger.log(Level.SEVERE, "ERROR:", e);
		} finally {
			bwr.close();
			bfr.close();
		}

		updateProgress(100, 100);
		updateMessage("All done!");
	}

	/**
	 * 
	 * @param bw
	 * @throws IOException
	 */
	private void write(BufferedWriter bw) throws IOException {

		// fast 'code' for setting assault good order for objectives
		// TODO move out in near future ...
		setAssaultObjectiveOrder();

		writeHeader();

		String buffer = null;

		for (T3DActor actor : convertedActors) {

			if (actor == null) {
				continue;
			}

			// write parent actor
			if (actor.isValidWriting()) {
				buffer = actor.toString();

				if (buffer != null) {
					bw.write(buffer);
				}
			}

			// write replacement actors
			for (T3DActor repActor : actor.children) {
				if (repActor.isValidWriting()) {
					bw.write(repActor.toString());
				}
			}
		}

		writeFooter();
	}

	/**
	 * If true means we don't anaylise t3d lines of current actor being parsed
	 */
	boolean banalyseline = false;
	String currentClass = "";

	/**
	 * Current actor class
	 */
	Class<? extends T3DActor> utActorClass = null;
	T3DActor uta = null;

	private final int LEVEL_OBJECT_LEVEL = 1;

	/**
	 * Begin Object Class=Level
	 */
	private int deepObjectLevel = LEVEL_OBJECT_LEVEL - 1;

	/**
	 * Says if current line is data for new actor
	 * 
	 * @param line
	 * @return
	 */
	private boolean isBeginActor(String line) {

		if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {
			return line.contains("Begin Actor");
		}

		// Any actor/sub-class begins with "Begin Object"
		else if (mapConverter.isFrom(UnrealEngine.UE3)) {

			if (line.trim().startsWith("Begin Object")) {

				deepObjectLevel++;
				return (deepObjectLevel == (LEVEL_OBJECT_LEVEL + 1));
			}
		}

		return false;
	}

	private boolean isEndActor(String line) {

		if (mapConverter.isFrom(UnrealEngine.UE1, UnrealEngine.UE2)) {
			return line.contains("End Actor");
		}

		// Any actor begin with "Begin Object"
		else if (mapConverter.isFrom(UnrealEngine.UE3)) {

			if (line.trim().startsWith("End Object")) {
				deepObjectLevel--;
				return (deepObjectLevel == LEVEL_OBJECT_LEVEL);
			}
		}

		return false;
	}

	private int actorsReadCount;

	/**
	 * Analyze T3D line to get and convert UT data
	 * 
	 * @param line
	 *            current T3D line being read
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void analyzeLine(String line) throws IOException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

		line = line.trim();

		if (isBeginActor(line)) {
			actorsReadCount++;
			currentClass = getActorClass(line);

			if (mapConverter.getSupportedActorClasses().canBeConverted(currentClass)) {
				utActorClass = mapConverter.getSupportedActorClasses().getConvertActorClass(currentClass);
				banalyseline = true;

				if (utActorClass != null) {
					Constructor<? extends T3DActor> cons = utActorClass.getConstructor(MapConverter.class, String.class);
					uta = cons.newInstance(mapConverter, getActorClass(line));
					uta.setT3dOriginClass(currentClass);
					convertedActors.add(uta);
					uta.analyseT3DData(line);
				}
			} else {
				// skips some useless/uneeded actors to notify unconverted (e.g:
				// pathnodes for UE4/UT4)
				if (!mapConverter.getSupportedActorClasses().noNotifyUnconverted(currentClass)) {
					unconvertedActors.add(currentClass);

					if (createNoteWhenUnconverted) {
						banalyseline = true;
						utActorClass = T3DNote.class;
						uta = new T3DNote(mapConverter, "Unconverted: " + currentClass, true);
						uta.setT3dOriginClass(currentClass);
						uta.analyseT3DData(line);
						convertedActors.add(uta);
					} else {
						logger.warning("Unconverted " + currentClass);
						banalyseline = false;
					}
				}
			}
		}

		// Actor End - We write converted data to t3d file
		else if (isEndActor(line)) {

			updateProgress(actorsReadCount, actorCount);

			if (banalyseline) {
				if (uta != null) {

					if (uta.isValidConverting()) {
						// we might want to only re-scale map
						if (uta.getMapConverter().getOutputGame() != uta.getMapConverter().getInputGame()) {
							updateMessage("Converting " + uta.name);
							uta.convert();
						}
						// rescale if needed
						// must always be done after convert
						uta.scale(mapConverter.getScale());
					} else {
						convertedActors.removeLast();
					}
				}
			}

			// Reset
			banalyseline = false;
			utActorClass = null;
			uta = null;
		}

		// Actor data being analyzed
		else {
			if (banalyseline) {
				if (uta != null) {
					uta.preAnalyse(line);
					uta.analyseT3DData(line);
				}
			}
		}
	}

	/**
	 * Returns current actor class from t3d line defining actor
	 * 
	 * @param line
	 *            t3d line
	 * @return Actor class
	 */
	private String getActorClass(String line) {
		return (line.split("=")[1]).split(" ")[0];
	}

	/**
	 * Write header of T3D file TODO check for UE1/UE2
	 * 
	 * @throws IOException
	 */
	private void writeHeader() throws IOException {

		bwr.write("Begin Map\n");
		bwr.write("\tBegin Level Name=" + mapConverter.getRelativeUtMapPath() + "\n");

		// Auto creates a big additive brush surrounding level
		// to simulate creating a level in subtract mode (not existing in UE4
		// ...)
		if (mapConverter.isFromUE1UE2ToUE3UE4()) {

			Double offset = 200d;
			Vector3d boundBox = getLevelDimensions();

			T3DBrush additiveBrush = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
			additiveBrush.location = boundBoxLocalisation;
			additiveBrush.name = "BigAdditiveBrush";
			additiveBrush.brushClass = T3DBrush.BrushClass.Brush;

			// no need accurate light map resolution on this brush since it will
			// be never seen by player
			for (T3DPolygon p : additiveBrush.polyList) {
				p.lightMapScale = 2048d;
			}

			bwr.write(additiveBrush.toString());
		}
	}

	/**
	 * 
	 * @return
	 */
	private Vector3d getLevelDimensions() {

		if (levelDimension == null) {
			Vector3d max = new Vector3d(0d, 0d, 0d);
			Vector3d min = new Vector3d(0d, 0d, 0d);

			// get the max/min boundaries of brush vertices on whole level
			for (T3DActor actor : convertedActors) {

				if (actor instanceof T3DBrush) {

					T3DBrush brush = (T3DBrush) actor;

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
	 * Write footer of converted t3d file // Begin Map
	 * Name=/Game/RestrictedAssets/Maps/WIP/DM-SolarTest // Begin Level
	 * NAME=PersistentLevel TODO check for UE1/UE2
	 * 
	 * @throws IOException
	 */
	private void writeFooter() throws IOException {

		if (mapConverter.toUnrealEngine4()) {

			Vector3d boundBox = getLevelDimensions();
			Double offset = 100d;

			// Automatically add a lightMassVolume around the whole level
			T3DBrush lightMassVolume = T3DBrush.createBox(mapConverter, boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
			lightMassVolume.location = boundBoxLocalisation;
			lightMassVolume.name = "LightMassImpVolume";
			lightMassVolume.brushClass = T3DBrush.BrushClass.LightmassImportanceVolume;
			bwr.write(lightMassVolume.toString());

			offset = 150d;

			// Automatically add a navigation volume
			// FIXME UED4 editor crashes for unknown reason
			/*
			 * T3DBrush navMeshBoundsVolume = T3DBrush.createBox(mapConverter,
			 * boundBox.x + offset, boundBox.y + offset, boundBox.z + offset);
			 * navMeshBoundsVolume.location = loc; navMeshBoundsVolume.name =
			 * "NavMeshBndsVolume"; navMeshBoundsVolume.brushClass =
			 * T3DBrush.BrushClass.NavMeshBoundsVolume;
			 * bwr.write(navMeshBoundsVolume.toString());
			 */

			// warn designer to do these steps
			// to be able to convert correctly objectives
			// since WorldInfo actor not imported by UE4 (which contained
			// gametype)
			if (UTGameTypes.isUt99Assault(mapConverter)) {
				logger.log(Level.WARNING, "After .t3d file import, set gametype to assault mode in world settings");
				logger.log(Level.WARNING, "delete all actors in map. Save and re-import the .t3d file");
				logger.log(Level.WARNING, "You should now see properly assault objectives !");
			}
		}

		bwr.write("\tEnd Level\n");
		bwr.write("\tBegin Surface\n");
		bwr.write("\tEnd Surface\n");
		bwr.write("End Map\n");
	}

	/**
	 *
	 * @param createNoteWhenUnconverted
	 */
	public void setCreateNoteWhenUnconverted(boolean createNoteWhenUnconverted) {
		this.createNoteWhenUnconverted = createNoteWhenUnconverted;
	}

	/**
	 * Test map conversion
	 */
	public static void test() {

		try {

			File t3dFile = new File("Z:\\TEMP\\UT99Maps\\Test\\DM-UT99-MoverTest.t3d");
			MapConverter mc = new MapConverter(UTGames.UTGame.UT99, UTGames.UTGame.UT4, t3dFile, "Z:\\Temp");

			// ((SupU1UT99ToUT4Classes)
			// mc.getSupportedActorClasses()).setConvertOnly("AmbientSound",
			// T3DSound.class);
			mc.setScale(2.5d);
			mc.convert();

			System.out.println("Unconverted classes:");

			for (String className : mc.getT3dLvlConvertor().unconvertedActors) {
				System.out.println(className);
			}
		} catch (Exception ex) {
			Logger.getLogger(T3DLevelConvertor.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}

		System.exit(0);
	}

	@Override
	protected Object call() throws Exception {
		convert();
		return null;
	}
}
