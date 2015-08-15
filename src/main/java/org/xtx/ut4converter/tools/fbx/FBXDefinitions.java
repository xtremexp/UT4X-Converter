/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools.fbx;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author XtremeXp
 */
public class FBXDefinitions implements FBXWriter {

	static final short DEFAULT_VERSION = 100;

	short version;
	int count;

	static class ObjectTypeCount {

		FBXObjectType objectType;
		int count;

		public ObjectTypeCount(FBXObjectType objectType, int count) {
			this.objectType = objectType;
			this.count = count;
		}

	}

	List<ObjectTypeCount> objectTypeCounts;

	public static FBXDefinitions getInstance(List<FBXObject> objects) {

		FBXDefinitions fbxDefinitions = new FBXDefinitions();
		fbxDefinitions.count = objects != null ? objects.size() : 0;
		fbxDefinitions.objectTypeCounts = new ArrayList<>();
		fbxDefinitions.version = DEFAULT_VERSION;

		if (objects != null) {
			for (FBXObject fbxObject : objects) {

				ObjectTypeCount otc = fbxDefinitions.getObjectTypeCountByObjectType(FBXObjectType.Geometry);

				if (otc == null) {
					fbxDefinitions.objectTypeCounts.add(new ObjectTypeCount(fbxObject.objectType, 0));
				} else {
					otc.count++;
				}
			}
		}

		return fbxDefinitions;
	}

	private ObjectTypeCount getObjectTypeCountByObjectType(FBXObjectType objectType) {

		for (ObjectTypeCount otc : objectTypeCounts) {
			if (otc.objectType == objectType) {
				return otc;
			}
		}

		return null;
	}

	@Override
	public void writeFBX(StringBuilder sb) {

		sb.append("Definitions:  {\n");
		sb.append("\tVersion: ").append(version).append("\n");
		sb.append("\tCount: ").append(count).append("\n");

		for (ObjectTypeCount otc : objectTypeCounts) {
			sb.append("\tObjectType: ").append(otc.objectType.name()).append(" {\n");
			sb.append("\t\tCount: ").append(otc.count).append("\n");
			sb.append("\t}\n");
		}

		sb.append("}\n");
	}
}
