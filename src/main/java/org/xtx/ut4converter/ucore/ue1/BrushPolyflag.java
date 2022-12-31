/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue1;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author XtremeXp
 */
public enum BrushPolyflag {

	ZONE_PORTAL(67108864), TWO_SIDED(256), SEMI_SOLID(32), NON_SOLID(8), TRANSPARENT(4), MASQUED(2), INVISIBLE(1);
	// SOLID(0);

	final Integer pow;

	BrushPolyflag(Integer pow) {
		this.pow = pow;
	}

	public Integer getPow() {
		return pow;
	}

	/**
	 * Parse integer to polyflags list. Unreal engine stores all flag in one
	 * single number doing the sum of each value which are power of 2)
	 * E.g: "Begin Polygon Item=OUTSIDE Texture=Starship.Wall.sh_wlp Flags=1073741824 Link=9"
	 * 
	 * @param polyflag Polyflag number (e.g: 1073741824)
	 * @return List of brush poly flags associated (maybe none)
	 */
	public static List<BrushPolyflag> parse(Integer polyflag) {

		List<BrushPolyflag> flags = new ArrayList<>();

		for (BrushPolyflag bp : BrushPolyflag.values()) {

			if (polyflag - bp.getPow() >= 0) {
				flags.add(bp);
				polyflag -= bp.getPow();
			}
		}

		return flags;
	}

	public static boolean hasInvisibleFlag(List<BrushPolyflag> polyflags) {
		return polyflags != null && polyflags.contains(INVISIBLE);
	}
}
