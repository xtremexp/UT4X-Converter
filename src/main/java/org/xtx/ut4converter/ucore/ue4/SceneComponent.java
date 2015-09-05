package org.xtx.ut4converter.ucore.ue4;

import javax.vecmath.Vector3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;

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

	public Vector3d getRelativeLocation() {
		return relativeLocation;
	}

	public void setRelativeLocation(Vector3d relativeLocation) {
		this.relativeLocation = relativeLocation;
	}

	public Vector3d getRelativeRotation() {
		return relativeRotation;
	}

	public void setRelativeRotation(Vector3d relativeRotation) {
		this.relativeRotation = relativeRotation;
	}

	public Vector3d getRelativeScale3D() {
		return relativeScale3D;
	}

	public void setRelativeScale3D(Vector3d relativeScale3D) {
		this.relativeScale3D = relativeScale3D;
	}
	
	

}
