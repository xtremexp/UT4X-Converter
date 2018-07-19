/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d.iface;

/**
 *
 * @author XtremeXp
 */
public interface T3D {

	void convert();

	void scale(Double newScale);

	boolean analyseT3DData(String line);

	void toT3d(StringBuilder sb, String prefix);

	String getName();
}
