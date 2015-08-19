package org.xtx.ut4converter.t3d;

import java.util.logging.Logger;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.UTGames.UTGame;

public abstract class T3DObject {

	/**
	 * Reference to map converter
	 */
	protected MapConverter mapConverter;

	/**
	 * Actor class (may differ from origin class if actor could not be converted
	 * (e.g: note class) or replaced with another one)
	 */
	protected String t3dClass;

	/**
	 * Name or label of actor
	 */
	protected String name;

	/**
	 * Current game compatibility state for actor. Should automatically change
	 * after convert
	 */
	UTGame game = UTGame.NONE;

	/**
	 * Used to write actor TODO make global StringBuilder that we would 'reset'
	 * after write of each actor (avoiding creating one for each single actor /
	 * perf issues)
	 */
	protected StringBuilder sbf;

	protected Logger logger;

	public abstract void scale(Double newScale);

	public T3DObject(MapConverter mc) {
		this.t3dClass = this.getClass().getSimpleName();
	}

	public T3DObject(MapConverter mc, String t3dClass) {

		this.mapConverter = mc;
		this.t3dClass = t3dClass;
		this.sbf = new StringBuilder();
		this.logger = mc.getLogger();
		this.game = mapConverter.getInputGame();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
