package org.xtx.ut4converter.ucore.ue4.matinee;

import java.util.ArrayList;
import java.util.List;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.T3DObject;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.tools.RGBColor;

public class InterpGroup extends T3DObject implements T3D {

	/**
	 * Ref to interp data
	 */
	InterpData interpData;

	/**
	 * Group Name
	 */
	private String groupName;

	/**
	 * List of tracks this group have
	 */
	public List<InterpTrack> interpTracks;

	/**
	 * Group color (how it's the track bar is rendered in matinee scene)
	 */
	RGBColor groupColor;

	public InterpGroup(MapConverter mc, InterpData interpData, String groupName) {
		super(mc);
		this.interpData = interpData;
		this.groupName = groupName;
	}

	@Override
	public void scale(Double newScale) {

		if (interpTracks != null && !interpTracks.isEmpty()) {
			for (InterpTrack track : interpTracks) {
				track.scale(newScale);
			}
		}

	}

	@Override
	public void convert() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean analyseT3DData(String line) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toT3d(StringBuilder sb) {

		if (interpTracks != null && !interpTracks.isEmpty()) {
			for (InterpTrack track : interpTracks) {
				track.toT3d(sb);
			}

			int idx = 0;

			for (InterpTrack track : interpTracks) {
				// InterpTracks(0)=InterpTrackToggle'InterpTrackToggle_0'
				sb.append("\tInterpTracks(").append(idx).append(")=").append(track.getClass().getName()).append("'").append(track.getName()).append("'\n");
			}

			T3DUtils.writeLine(sb, "GroupName", groupName, "\t");
			T3DUtils.writeLine(sb, "GroupColor", groupColor, "\t");
		}

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Adds a track
	 * 
	 * @param track
	 *            Track
	 */
	public void addTrack(InterpTrack track) {

		if (interpTracks == null) {
			interpTracks = new ArrayList<>();
		}

		//track.setName(track.getName() + "_" + interpTracks.size());
		interpTracks.add(track);
	}

	public InterpData getInterpData() {
		return interpData;
	}

}
