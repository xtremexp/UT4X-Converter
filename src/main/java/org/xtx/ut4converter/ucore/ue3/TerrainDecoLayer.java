/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.ucore.ue3;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.ucore.UPackageRessource;

import java.util.List;

/**
 *
 * @author XtremeXp
 */
public class TerrainDecoLayer extends T3DObject {

	List<Decoration> decorations;

	class Decoration {

		StaticMeshComponentFactory factory;

		float minScale;
		float maxScale;
		float density;
		float slopeRotationBlend;
		short randSeed;

		Decoration() {
			initialise();
		}

		private void initialise() {
			minScale = 1f;
			maxScale = 1f;
			density = 0.01f;

		}
	}

	static class StaticMeshComponentFactory {

		UPackageRessource staticmesh;
		List<UPackageRessource> materials;
		boolean collideActors;
		boolean blockActors;
		boolean blockZeroExtent;
		boolean blockNonZeroExtent;
		boolean blockRigidBody;
		boolean hiddenGame;
		boolean hiddenEditor;
		boolean castShadow;

		StaticMeshComponentFactory() {
			collideActors = true;
			blockActors = true;
			blockNonZeroExtent = true;
			blockZeroExtent = true;
			blockRigidBody = true;
			castShadow = true;
		}
	}

	public TerrainDecoLayer(MapConverter mc) {
		super(mc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}


}
