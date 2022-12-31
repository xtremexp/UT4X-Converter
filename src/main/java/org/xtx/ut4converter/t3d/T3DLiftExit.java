/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

/**
 * Checked: UT99, U1, U2, UT2004, UT3
 * UT99: DM-Cybrosis][ UT4: DM-Vortex
 * 
 * @author XtremeXp
 */
public class T3DLiftExit extends T3DSound {

	/**
	 * T3DMover (brush)
	 * or T3DMoverSM (staticmesh)
	 */
	private T3DActor linkedLift;

	private Boolean bLiftJump;

	/**
	 * UT4 UT3: bExitOnly
	 */
	private Boolean bLiftExit;

	/**
	 * UT99
	 */
	private String liftTag;

	public T3DLiftExit(MapConverter mc, String t3dClass) {
		super(mc, t3dClass);
	}

	@Override
	public boolean analyseT3DData(String line) {

		// UE1 -> "LiftTag=lastone"
		if (line.contains("LiftTag=")) {
			liftTag = T3DUtils.getString(line);
		}


		// UT2003/4
		else if(line.startsWith("bLiftJumpExit")){
			this.bLiftJump = T3DUtils.getBoolean(line);
		}

		// UT3
		else if (line.contains("bExitOnly=")) {
			bLiftExit = T3DUtils.getBoolean(line);
		} else {
			return super.analyseT3DData(line);
		}

		return true;
	}

	public String toT3d() {
		sbf.append(IDT).append("Begin Actor Class=UTLiftExit Name=").append(name).append("\n");

		sbf.append(IDT).append("\tBegin Object Class=BillboardComponent Name=\"Icon\" Archetype=BillboardComponent'/Script/UnrealTournament.Default__UTLiftExit:Icon'\n");
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tBegin Object Name=\"Icon\"\n");
		writeLocRotAndScale();
		sbf.append(IDT).append("\tEnd Object\n");

		sbf.append(IDT).append("\tIcon=Icon\n");

		if (bLiftJump != null) {
			sbf.append(IDT).append("\tbLiftJump=").append(bLiftJump).append("\n");
		}

		if (bLiftExit != null) {
			sbf.append(IDT).append("\tbLiftExit=").append(bLiftExit).append("\n");
		}

		if (linkedLift != null) {
			String linkedLiftConvertedName = linkedLift.name;

			// converter change the name and add the tag
			if(linkedLift.tag != null) {
				linkedLiftConvertedName += "_" + linkedLift.tag;
			}

			sbf.append(IDT).append("\tMyLift=Generic_Lift_C'").append(linkedLiftConvertedName).append("'\n");
		}

		sbf.append(IDT).append("\tRootComponent=Icon\n");
		writeEndActor();

		return super.toString();
	}

	@Override
	public void convert() {

		// we need to retrieve the linked lift
		// at this stage the T3D level converter
		// may not have yet parsed data of linked lift
		if (linkedTo == null || linkedTo.isEmpty()) {
			T3DLevelConvertor tlc = mapConverter.getT3dLvlConvertor();

			for (T3DActor actor : tlc.getConvertedActors()) {

				// Note in previous UTs lifttag could be link to actor not
				// necessarly movers (e.g: SpecialEvents)
				if ((actor instanceof T3DMover || actor instanceof T3DMoverSM) && this.liftTag != null && this.liftTag.equals(actor.tag)) {
					this.linkedTo.add(actor);
					actor.linkedTo.add(this);
					this.linkedLift = actor;
					break;
				}
			}
		}

		super.convert();
	}
}
