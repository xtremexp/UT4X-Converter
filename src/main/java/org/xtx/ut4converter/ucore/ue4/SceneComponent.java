package org.xtx.ut4converter.ucore.ue4;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;

import javax.vecmath.Vector3d;

/**
 * Only for UE4 (UE3 also?) Not detailled for the moment Should contains
 * location and rotation of actor
 * 
 * @author XtremeXp
 *
 */
public class SceneComponent extends T3DObject {

	/**
	 * Location of actor
	 */
	private Vector3d relativeLocation;
	
	/**
	 * Rotation of actor
	 */
	private Vector3d relativeRotation;
	private Vector3d relativeScale3D;

	public SceneComponent(MapConverter mc) {
		super(mc);
		this.name = "SceneComp";
	}

	public void setRelativeLocation(Vector3d relativeLocation) {
		this.relativeLocation = relativeLocation;
	}

	public void setRelativeRotation(Vector3d relativeRotation) {
		this.relativeRotation = relativeRotation;
	}

	public void setRelativeScale3D(Vector3d relativeScale3D) {
		this.relativeScale3D = relativeScale3D;
	}
	
	

}
