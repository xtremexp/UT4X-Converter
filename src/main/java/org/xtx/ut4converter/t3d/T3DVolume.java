package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * WIP class
 * 
 * @author XtremeXp
 *
 */
public class T3DVolume extends T3DSound {


	public T3DVolume(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
		// TODO Auto-generated constructor stub
	}

	public T3DVolume(MapConverter mapConverter, String t3dClass, T3DActor actor) {
		super(mapConverter, t3dClass, actor);
	}

	
	@Override
	public void scale(double newScale) {
		super.scale(newScale);
	}
	
	@Override
	public void convert() {
		super.convert();
	}
}
