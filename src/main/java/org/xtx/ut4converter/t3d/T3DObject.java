package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.ucore.UnrealEngine;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
	 * Name or label of actor.
	 * Might be changed during conversion process
	 */
	protected String name;

	/**
	 * Original name of object
	 * (never changes)
	 */
	protected String originalName;

	/**
	 * Current game compatibility state for actor. Should automatically change
	 * after convert
	 */
	private UnrealGame game = new UnrealGame();

	protected Logger logger;

	/**
	 * Used to write actor TODO make global StringBuilder that we would 'reset'
	 * after write of each actor (avoiding creating one for each single actor /
	 * perf issues)
	 */
	protected final StringBuilder sbf;

	final List<T3DSimpleProperty> registeredProperties;

	/**
	 * Minimal indentation when writing t3d converted actor
	 */
	public final static String IDT = "\t";

	public T3DObject(MapConverter mc) {
		initialise(mc);
		this.t3dClass = this.getClass().getSimpleName();
		this.name = this.t3dClass;
		this.registeredProperties = new ArrayList<>();
		this.sbf = new StringBuilder();
	}

	public T3DObject(MapConverter mc, String t3dClass) {
		initialise(mc);
		this.t3dClass = t3dClass;
		this.name = this.t3dClass + "_0";
		this.registeredProperties = new ArrayList<>();
		this.sbf = new StringBuilder();
	}

	private void initialise(MapConverter mc) {
		this.mapConverter = mc;
		this.logger = mc.getLogger();
		this.game = mapConverter.getInputGame();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getT3dClass() {
		return t3dClass;
	}


	public void writeBeginObj(StringBuilder sbf, String prefix) {

		if (prefix != null) {
			sbf.append(prefix);
		}

		if (this instanceof T3DActor) {
			sbf.append("Begin Actor Name=\"").append(name).append("\" Class=").append(t3dClass).append("\n");
		} else {
			sbf.append("Begin Object Name=\"").append(name).append("\" Class=").append(t3dClass).append("\n");
		}
	}

	boolean parseSimpleProperty(final String line){

		boolean propFound = false;

		for(final T3DSimpleProperty simpleProperty : this.registeredProperties){
			propFound |= simpleProperty.readPropertyFromT3dLine(line, mapConverter);
		}

		return propFound;
	}

	public T3DSimpleProperty registerSimplePropertyRessource(final String propertyName, final T3DRessource.Type typeRessource){
		final T3DSimpleProperty simpleProperty = new T3DSimpleProperty(propertyName, typeRessource, false);
		this.registeredProperties.add(simpleProperty);
		return simpleProperty;
	}

	public T3DSimpleProperty registerSimpleProperty(final String propertyName, final Object classType){
		return registerSimpleProperty(propertyName, classType, null);
	}

	public void registerSimpleArrayProperty(final String propertyName, final Object classType){
		final T3DSimpleProperty simpleProperty = new T3DSimpleProperty(propertyName, classType, null, true);
		this.registeredProperties.add(simpleProperty);
	}

	/**
	 *
	 * @param propertyName Property name
	 * @param classType Type of property (String, Float, or other)
	 */
	public T3DSimpleProperty registerSimpleProperty(final String propertyName, final Object classType, final Object defaultValue){
		final T3DSimpleProperty simpleProperty = new T3DSimpleProperty(propertyName, classType, defaultValue, false);
		this.registeredProperties.add(simpleProperty);
		return simpleProperty;
	}

	void writeSimpleProperties(){
		for(final T3DSimpleProperty simpleProperty : this.registeredProperties){
			simpleProperty.writeProperty(sbf, mapConverter);
		}
	}

	public UnrealGame getGame() {
		return game;
	}

	public void setGame(UnrealGame game) {
		this.game = game;
	}

	protected boolean isTo(UnrealEngine unrealEngine){
		return this.mapConverter.getUnrealEngineTo() == unrealEngine;
	}
}
