/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.t3d;

import org.apache.commons.math3.util.Pair;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.t3d.iface.T3D;
import org.xtx.ut4converter.tools.HSVColor;
import org.xtx.ut4converter.tools.RGBColor;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.vecmath.Vector3d;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author XtremeXp
 */
public class T3DUtils {

	final static String EQUAL = "=";

	/**
	 * // Begin Polygon Item=Rise Texture=r-plates-g Link=0
	 *
	 * @param line Line
	 * @param split Split char
	 * @return
	 * @deprecated DELETE AND MERGE WITH getString
	 */
	public static String getStringTEMP(String line, String split) {
		if (!line.contains(split)) {
			return null;
		}
		return line.split(split + "=")[1].split(" ")[0];
	}

	/**
	 *
	 * @param line
	 * @param split
	 * @return
	 */
	public static Integer getInteger(String line, String split) {
		if (!line.contains(split)) {
			return null;
		}
		return Integer.valueOf(line.split(split + "=")[1].split(" ")[0]);
	}

	/**
	 * Parses integer from t3d line
	 * @param line Line
	 * @return
	 */
	public static Integer getInteger(String line) {
		return Integer.valueOf(line.split("=")[1]);
	}

	/**
	 *
	 * @param line t3d line
	 * @return
	 */
	public static Short getShort(String line) {
		return Short.valueOf(line.split("=")[1]);
	}

	public static UPackageRessource getUPackageRessource(final MapConverter mapConverter, final String line, final T3DRessource.Type type) {
		if(line.endsWith("=None")){
			return null;
		} else {
			return mapConverter.getUPackageRessource(line.split("'")[1], type);
		}
	}

	/**
	 *
	 * @param line
	 * @return
	 */
	public static Double getDouble(String line) {
		return Double.valueOf(line.split("=")[1]);
	}
	
	/**
	 *
	 * @param line
	 * @return
	 */
	public static Float getFloat(String line) {
		return Float.valueOf(line.split("=")[1]);
	}
	
	/**
	 *
	 * @param line
	 * @param property
	 * @return
	 */
	public static Float getFloat(String line, String property) {
		return Float.valueOf(getPropValue(line, property, null));
	}

	private static String getPropValue(String line, String property, String nextFieldSeparator) {
		if (nextFieldSeparator != null) {
			return line.split(property.concat("="))[1].split(",")[0].split("\\)")[0].split(nextFieldSeparator)[0];
		} else {
			return line.split(property.concat("="))[1].split(",")[0].split("\\)")[0];
		}
	}

	/**
	 * Extra utility function to return null for resource if ends with "=None"
	 * @param line
	 * @return
	 */
	public static String getResourceName(String line) {
		if(line.endsWith("=None")){
			return null;
		} else {
			return getString(line);
		}
	}

	public static String getString(String line) {
		return line.split("=")[1].replaceAll("\"", "").replaceAll("\\)", "");
	}
	
	public static String getString(String line, String property, String nextFieldSeparator) {
		return getPropValue(line, property, nextFieldSeparator);
	}

	public static String getString(String line, String property) {
		return getPropValue(line, property, null);
	}

	/**
	 *
	 * @param line
	 * @param defaut
	 * @return
	 */
	public static Vector3d getVector3d(String line, Double defaut) {
		return getVector3d(clean(line), new String[] { "X", "Y", "Z" }, defaut);
	}

	/**
	 * Remove double whitespaces and trim to t3d line
	 *
	 * @param t3dLine
	 * @return
	 */
	public static String clean(String t3dLine) {
		// as seen in a few maps values like "-00001.#IND00"
		return t3dLine.replaceAll("\\s+", " ").replaceAll("#IND00", "0").trim();
	}

	/**
	 * Transform polygon data into vector E.G:
	 * "Normal   -00001.000000,+00000.000000,+00000.000000"
	 *
	 * @param t3dPolyLine
	 * @param polyParam
	 * @return
	 */
	public static Vector3d getPolyVector3d(String t3dPolyLine, String polyParam) {
		t3dPolyLine = clean(t3dPolyLine);
		String tmp = t3dPolyLine.split(polyParam + " ")[1];
		String[] tmp2 = tmp.split(",");

		Vector3d v = new Vector3d();

		v.x = Double.parseDouble(tmp2[0]);
		v.y = Double.parseDouble(tmp2[1]);
		v.z = Double.parseDouble(tmp2[2]);

		return v;
	}

	/**
	 * Convert 3d vector of poly data to t3d string format
	 *
	 * @param v
	 * @param df
	 * @return // +00001.000000,+00000.000000,+00000.000000
	 */
	public static String toPolyStringVector3d(Vector3d v, DecimalFormat df) {

		return df.format(v.x) + "," + df.format(v.y) + "," + df.format(v.z);
	}

	/**
	 * KeyRot(2)=(Yaw=-16384)
	 * @param line
	 * @return [2, "(Yaw=-16384)"]
	 */
	public static Pair<Integer, String> getArrayEntry(String line){

		final Integer index = Integer.valueOf(line.split("\\)")[0].split("\\(")[1]);

		return new Pair<>(index, line.substring(line.indexOf("=") + 1));
	}

	/**
	 * Get vector3d from rotation data in t3d format E.G:
	 * "Rotation=(Pitch=848,Yaw=-12928,Roll=1208)"
	 *
	 * @param line
	 * @return
	 */
	public static Vector3d getVector3dRot(String line) {
		return getVector3d(line, new String[] { "Pitch", "Yaw", "Roll" }, null);
	}

	/**
	 * Get Vector3d from line E.G:
	 * " Location=(X=3864.000000,Y=-5920.000000,Z=-15776.000000)" E.G:
	 * "Rotation=(Yaw=8160)"
	 *
	 * @param line
	 *            Current line of T3D Level file being analyzed containing
	 *            Location info
	 * @return Vector 3d
	 */
	private static Vector3d getVector3d(String line, String[] s, Double defaut) {

		Vector3d v;

		if (defaut != null) {
			v = new Vector3d(defaut, defaut, defaut);
		} else {
			v = new Vector3d(0D, 0D, 0D);
			defaut = 0D;
		}

		// Location=(X=5632.000000,Z=384.000000)
		// Location=(X=3864.000000,Y=-5920.000000,Z=-15776.000000)
		line = line.substring(line.indexOf("(") + 1);
		line = line.split("\\)")[0];
		// line = line.replaceAll("\\)","");

		String[] fields = line.split(",");

		if (fields.length == 3) {
			v.x = Double.parseDouble(fields[0].split("=")[1]);
			v.y = Double.parseDouble(fields[1].split("=")[1]);
			v.z = Double.parseDouble(fields[2].split("=")[1]);
		} else if (fields.length == 2) {
			// Location=(X=1280.000000,Y=2944.000000)
			if (line.contains(s[0]) && line.contains(s[1])) {
				v.x = Double.parseDouble(fields[0].split("=")[1]);
				v.y = Double.parseDouble(fields[1].split("=")[1]);
				v.z = defaut;
			}
			// Location=(X=1280.000000,Y=2944.000000)
			else if (line.contains(s[0]) && line.contains(s[2])) {
				v.x = Double.parseDouble(fields[0].split("=")[1]);
				v.y = defaut;
				v.z = Double.parseDouble(fields[1].split("=")[1]);
			} else if (line.contains(s[1]) && line.contains(s[2])) {
				v.x = defaut;
				v.y = Double.parseDouble(fields[0].split("=")[1]);
				v.z = Double.parseDouble(fields[1].split("=")[1]);
			}
		} else if (fields.length == 1) {
			if (line.contains(s[0])) {
				v.x = Double.parseDouble(fields[0].split("=")[1]);
			}
			// Location=(X=1280.000000,Y=2944.000000)
			else if (line.contains(s[1])) {
				v.y = Double.parseDouble(fields[0].split("=")[1]);
			} else if (line.contains(s[2])) {
				v.z = Double.parseDouble(fields[0].split("=")[1]);
			}
		}

		return v;
	}

	/**
	 * Remove bad chars from string (basically unsupported chars for UE4 editor)
	 *
	 * @param mapName
	 * @return
	 */
	public static String filterName(String mapName) {

		StringBuilder s = new StringBuilder();

		for (char x : mapName.toCharArray()) {

			int val = x;

			if ((val == 45) // "-"
					|| (48 <= val && val <= 57) // 0 -> 9
					|| (65 <= val && val <= 90) // A -> Z
					|| (97 <= val && val <= 125)) { // a -> z
				s.append((char) val);
			}
		}

		return s.toString();
	}

	/**
	 * null-safe vector scaling
	 *
	 * @param v
	 *            Vector
	 * @param scale
	 */
	public static void scale(Vector3d v, Double scale) {

		if (v != null) {
			v.scale(scale != null ? scale : 1d);
		}
	}

	/**
	 * null-safe double scaling
	 *
	 * @param d
	 * @param scale
	 * @return
	 */
	public static Double scale(Double d, Double scale) {

		if (d != null) {
			return (scale != null ? d * scale : d);
		} else {
			return null;
		}
	}

	public static String getT3DLine(Map<String, Object> props) {

		StringBuilder s = new StringBuilder();
		int count = 0;

		for (String name : props.keySet()) {

			if (name == null || props.get(name) == null) {
				continue;
			}

			Object value = props.get(name);

			s.append(name).append("=");

			if (value instanceof Vector3d) {
				s.append(toStringVec((Vector3d) value));
			} else {
				s.append(value.toString());
			}

			s.append(",");
			count++;
		}

		if (count > 0) {
			s.deleteCharAt(s.length() - 1); // remove the last ","
		}

		return s.toString();
	}

	/**
	 * Convert vector to string
	 *
	 * @param v
	 * @return
	 */
	public static String toStringVec(Vector3d v) {

		if (v == null) {
			return null;
		}

		return "(X=" + v.x + ",Y=" + v.y + ",Z=" + v.z + ")";
	}

	/**
	 * ¨Get rgb values
	 *
	 * @param line
	 *            t3d line
	 * @return RGB values
	 */
	public static RGBColor getRGBColor(String line) {

		RGBColor rgbColor = new RGBColor();
		String s = line.split("\\(")[1].split("\\)")[0];

		String[] s2 = s.split(",");

		for (int i = 0; i < s2.length; i++) {

			String[] s3 = s2[i].split("=");

			switch (s3[0]) {
			case "A":
				rgbColor.A = Float.parseFloat(s3[1]);
				break;
			case "R":
				rgbColor.R = Float.parseFloat(s3[1]);
				break;
			case "G":
				rgbColor.G = Float.parseFloat(s3[1]);
				break;
			case "B":
				rgbColor.B = Float.parseFloat(s3[1]);
				break;
			default:
				break;
			}
		}

		return rgbColor;
	}

	public static Boolean getBoolean(String line) {

		if ("true".equals(line.split("=")[1].toLowerCase())) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public static boolean write(StringBuilder sb, String propName, Object propValue) {
		return write(sb, propName, propValue, null);
	}

	/**
	 * 
	 * @param sb
	 * @param prefix
	 */
	public static boolean write(StringBuilder sb, String propName, Object propValue, String prefix) {

		if (propValue == null) {
			return false;
		}

		if (prefix != null) {
			sb.append(prefix);
		}

		if (propValue instanceof T3D) {
			sb.append(propName).append(EQUAL);
			((T3D) propValue).toT3d(sb, null);
		} else if (propValue instanceof List) {

			sb.append("(");

			List<Object> propValues = (List<Object>) propValue;

			if (!propValues.isEmpty()) {
				for (Object item : (List<Object>) propValue) {
					write(sb, propName, item, prefix);
					sb.append(",");
				}

				sb.deleteCharAt(sb.length() - 1);
			}

			sb.append(")");
		} else {
			sb.append(propName).append(EQUAL).append(propValue.toString());
		}

		return true;
	}

	public static void writeLine(StringBuilder sb, String propName, Object propValue, String prefix) {

		if (write(sb, propName, propValue, prefix)) {
			sb.append("\n");
		}
	}

	public static void writeBeginObj(StringBuilder sb, String name, String prefix) {

		if (prefix != null) {
			sb.append(prefix);
		}

		sb.append("Begin Object Name=\"").append(name).append("\"\n");
	}

	public static void writeEndObj(StringBuilder sb, String prefix) {

		if (prefix != null) {
			sb.append(prefix);
		}

		sb.append("End Object\n");
	}

	/**
	 * E.G: InterpGroups(0)=InterpGroup'InterpGroup_2'
	 * InterpGroups(1)=InterpGroup'InterpGroup_3'
	 * 
	 * @param sb
	 * @param propName
	 * @param t3dObjs
	 * @param prefix
	 */
	public static void writeClassRef(StringBuilder sb, String propName, List<? extends T3DObject> t3dObjs, String prefix) {

		if (t3dObjs == null || t3dObjs.isEmpty()) {
			return;
		}

		int objIdx = 0;

		for (T3DObject object : t3dObjs) {

			writeClassRef(sb, propName + "(" + objIdx + ")=", object, prefix);
			objIdx++;
		}
	}

	/**
	 * E.G: CurveEdSetup=InterpCurveEdSetup'InterpCurveEdSetup_0'
	 * 
	 * @param sb
	 * @param propName
	 * @param t3dObj
	 * @param prefix
	 */
	public static void writeClassRef(StringBuilder sb, String propName, T3DObject t3dObj, String prefix) {

		if (t3dObj == null) {
			return;
		}

		if (prefix != null) {
			sb.append(prefix);
		}

		sb.append(propName).append(EQUAL).append(t3dObj.getClass().getSimpleName()).append("'").append(t3dObj.getName()).append("'\n");
	}

	public static void writeRootComponentAndLoc(T3DActor actor, T3DMatch.UE4_RCType rootType) {

		if (actor != null) {
			StringBuilder sbf = actor.sbf;

			sbf.append(T3DObject.IDT).append("\tBegin Object Class=").append(rootType.name).append(" Name=\"").append(rootType.alias).append("\" \n");

			actor.writeEndObject();

			// Begin Object Name="Sphere"
			sbf.append(T3DObject.IDT).append("\tBegin Object Name=\"").append(rootType.alias).append("\"\n");
			actor.writeLocRotAndScale();
			actor.writeEndObject();
			// RootComponent=Sphere
			sbf.append(T3DObject.IDT).append("\tRootComponent=").append(rootType.alias).append("\n");

		}
	}

	/**
	 * Write rgbcolor to t3d (R=0.828606,G=0.822917,B=1.000000,A=1.000000)
	 * 
	 * @param sb
	 * @param rgbColor
	 *            Rgb Color
	 */
	public static void writeRGBColor(StringBuilder sb, RGBColor rgbColor) {
		if (rgbColor != null) {
			// (R=0.828606,G=0.822917,B=1.000000,A=1.000000)
			sb.append("(R=").append(rgbColor.R).append(",G=").append(rgbColor.G).append(",B=").append(rgbColor.B).append(",A=").append(rgbColor.A).append(")");
		}
	}

	/**
	 * Writes hsvcolor to t3d in RGB format
	 * 
	 * @param sb
	 * @param hsvColor
	 *            HsvColor
	 */
	public static void writeRGBColor(StringBuilder sb, HSVColor hsvColor) {
		if (hsvColor != null) {
			writeRGBColor(sb, hsvColor.toRGBColor(true));
		}
	}

	/**
	 * CullDistances(1)=(Size=64.000000,CullDistance=3000.000000)
	 * 
	 * @param line
	 * @return
	 */
	public static Map<String, String> getProperties(String line) {
		String s = line;
		s = s.split("\\(")[1].split("\\)")[0];

		String[] ss = s.split(",");

		Map<String, String> props = new HashMap<>();

		for (String sss : ss) {
			String[] x = sss.split("=");
			props.put(x[0], x[1]);
		}

		return props;
	}

	/**
	 * Converts an Unreal Engine 1/2/3 rotator in 65536 range
	 * with Unreal Engine 4 values in 360 range
	 * @param rotator UE1/2/3 rotator
	 * @return Converted rotator
	 */
	public static Vector3d convertRotatorTo360Format(final Vector3d rotator){
		double rotFac = 360d / 65536d;

		rotator.x *= rotFac;
		rotator.y *= rotFac;
		rotator.z *= rotFac;

		return rotator;
	}


	/**
	 * Builds a GUID in Unreal Engine format (e.g: 6818E3CE496079535E50108034423FA2)
	 *
	 * @return GUID in Unreal Engine format
	 */
	public static String randomGuid(){

		final Random r = new Random();
		final StringBuilder sb = new StringBuilder();

		while(sb.length() < 32){
			sb.append(Integer.toHexString(r.nextInt()).toUpperCase());
		}

		return sb.toString();
	}
}
