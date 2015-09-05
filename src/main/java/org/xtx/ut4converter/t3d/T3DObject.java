package org.xtx.ut4converter.t3d;

import java.lang.reflect.Field;
import java.util.List;
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

	protected Logger logger;

	/**
	 * Minimal indentation when writing t3d converted actor
	 */
	public final static String IDT = "\t";

	public T3DObject(MapConverter mc) {
		initialise(mc);
		this.t3dClass = this.getClass().getSimpleName();
		this.name = this.t3dClass;
	}

	public T3DObject(MapConverter mc, String t3dClass) {
		initialise(mc);
		this.t3dClass = t3dClass;
		this.name = this.t3dClass + "_0";
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

	public void writeEndObj(StringBuilder sbf, String prefix) {

		if (prefix != null) {
			sbf.append(prefix);
		}

		if (this instanceof T3DActor) {
			sbf.append("End Actor\n");
		} else {
			sbf.append("End Object\n");
		}
	}

	/**
	 * Write sub-objects of this object if not-null and of class T3DObject
	 * (unreal objects). Only write "public" fields in class not null
	 * 
	 * @param sb
	 * @param prefix
	 */
	public void writeObjDefinition(StringBuilder sbf, String prefix) {

		for (Field f : getClass().getFields()) {

			Object obj;
			try {
				obj = f.get(this);

				if (obj == null) {
					continue;
				}

				T3DObject t3dObj = null;

				if (T3DObject.class.isAssignableFrom(obj.getClass())) {
					t3dObj = (T3DObject) obj;
				} else if (obj instanceof List) {
					List objList = (List) obj;

					if (objList != null && !objList.isEmpty()) {
						if (objList.get(0) != null && objList.get(0) instanceof T3DObject) {
							t3dObj = (T3DObject) objList.get(0);
						}
					}
				}

				if (t3dObj != null) {
					t3dObj.writeBeginObj(sbf, prefix);
					t3dObj.writeObjDefinition(sbf, prefix + "\t");
					T3DUtils.writeEndObj(sbf, prefix);
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
