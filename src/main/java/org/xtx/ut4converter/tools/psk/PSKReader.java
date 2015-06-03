package org.xtx.ut4converter.tools.psk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

/**
 * PSK staticmesh file reader.
 * 
 * Code ported to java from actorx importer source code
 * 
 * All credits goes to original author Gildor
 * - http://www.gildor.org/projects/unactorx
 * 
 */
public class PSKReader {

	/**
	 * Reference to .psk file
	 */
	File pskFile;
	
	
	
	public PSKReader(File pskFile) {
		super();
		this.pskFile = pskFile;
		initialise();
		load();
	}
	
	private void initialise(){
		vertices = new ArrayList<>();
		wedges = new ArrayList<>();
		triangles = new ArrayList<>();
		materials = new ArrayList<>();
		bones = new ArrayList<>();
		rawWeights = new ArrayList<>();
	}
	
	List<Vector3d> vertices;
	List<Vertex> wedges;
	List<Triangle> triangles;
	List<Material> materials;
	List<Bone> bones;
	List<RawWeight> rawWeights;


	class ChunkHeader {
		
		public String chunkID;
		public long  typeFlag;
		public long dataSize;
		public long dataCount;
		
		public ChunkHeader(ByteBuffer bf){
			
			chunkID = readString(bf, 20);
			typeFlag = bf.getInt();
			dataSize = bf.getInt();
			dataCount = bf.getInt();
		}
	}
	
	private String readString(ByteBuffer bf, int length){
		
		String s = "";
		
		for(int i=0; i< length ;i ++){
			s += (char) bf.get();
		}
		
		return s;
	}
	
	class Vertex {
		
		long pointIndex;
		float u, v;
		byte matIndex;
		byte reserved;
		short pad;
		
		public Vertex(ByteBuffer bf, boolean x){
			
			if(x){
				pointIndex = bf.getShort();
				bf.getShort();
				u = bf.getFloat();
				v = bf.getFloat();
				matIndex = bf.get();
				reserved = bf.get();
				pad = bf.getShort();
			} else {
				pointIndex = bf.getInt();
				u = bf.getFloat();
				v = bf.getFloat();
				matIndex = bf.get();
				reserved = bf.get();
				pad = bf.getShort();
			}
		}
	}
	
	class Triangle {
		
		long wedge0, wedge1, wedge2;
		byte matIndex, auxMatIndex;
		long smoothingGroups;
		
		public Triangle(ByteBuffer bf){
			wedge0 = bf.getShort();
			wedge1 = bf.getShort();
			wedge2 = bf.getShort();
			matIndex = bf.get();
			auxMatIndex = bf.get();
			smoothingGroups = bf.getInt();
		}
	}
	
	class Material {

		String materialName;
		long textureIndex;
		long polyFlags;
		long auxMaterial;
		long auxFlags;
		long lodBias;
		long lodStyle;
		
		public Material (ByteBuffer bf){
			materialName = readString(bf, 64).trim();
			textureIndex = bf.getInt();
			polyFlags = bf.getInt();
			auxMaterial = bf.getInt();
			auxFlags = bf.getInt();
			lodBias = bf.getInt();
			lodStyle = bf.getInt();
		}
	}
	
	class Bone {

		String name;
		long flags;
		long numChildren;
		long parentIndex;
		Vector4d orientation;
		Vector3d position;
		float lenght;
		Vector3d size;
		
		public Bone(ByteBuffer bf){
			name = readString(bf, 64);
			flags = bf.getInt();
			numChildren = bf.getInt();
			parentIndex = bf.getInt();
			orientation = readVector4d(bf);
			position = readVector3d(bf);
			lenght = bf.getFloat();
			size = readVector3d(bf);
		}
	}
	
	class RawWeight {

		float weight;
		long pointIndex;
		long boneIndex;
		
		public RawWeight(ByteBuffer bf){
			weight = bf.getFloat();
			pointIndex = bf.getInt();
			boneIndex = bf.getInt();
		}
	}
	
	private Vector3d readVector3d(ByteBuffer bf){
		
		Vector3d v = new Vector3d();
    	v.x = bf.getFloat();
    	v.y = bf.getFloat();
    	v.z = bf.getFloat();
    	
    	return v;
	}
	
	private Vector4d readVector4d(ByteBuffer bf){
		
		Vector4d v = new Vector4d();
    	v.x = bf.getFloat();
    	v.y = bf.getFloat();
    	v.z = bf.getFloat();
    	v.w = bf.getFloat();
    	
    	return v;
	}
	
	
	
	public void load(){
		
		FileChannel inChannel = null;
		
		try (FileInputStream fis = new FileInputStream(pskFile))
        {

			inChannel = fis.getChannel();
            ByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY,
    				0, (int) pskFile.length());
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            
            ChunkHeader header = new ChunkHeader(buffer);
            
            if(!"ACTRHEAD".equals(header.chunkID.trim())){
            	throw new Exception("Not a psk file");
            }
            
            
            while(buffer.hasRemaining()){
            	
	            ChunkHeader ch2 = new ChunkHeader(buffer);
	            System.out.println(ch2.chunkID);
	            
		            if("PNTS0000".equals(ch2.chunkID.trim())){
		            	long numVertex = ch2.dataCount;
		            	
		            	
		            	for(int i = 0; i < numVertex; i++){
			            	vertices.add(readVector3d(buffer));
		            	}
		            	
		            } else if("VTXW0000".equals(ch2.chunkID.trim())){
		            	long numWedges = ch2.dataCount;
		            	
		            	boolean x = numWedges <= 65536;
		            	System.out.println(numWedges);
		            	
	            		for(int i = 0; i < numWedges; i++){
	            			wedges.add(new Vertex(buffer, x));
	            		}

		            } else if("FACE0000".equals(ch2.chunkID.trim())){
		            	
		            	long numTriangles = ch2.dataCount;
		            	
	            		for(int i = 0; i < numTriangles; i++){
	            			triangles.add(new Triangle(buffer));
	            		}

		            } else if("FACE3200".equals(ch2.chunkID.trim())){
		            	
		            	long numTriangles = ch2.dataCount;
		            	System.out.println("TODO");

		            } 
		            
		            else if("MATT0000".equals(ch2.chunkID.trim())){
		            	
		            	long numMat = ch2.dataCount;
		            	
		            	for(int i = 0; i < numMat; i++){
	            			materials.add(new Material(buffer));
	            		}
		            } 
		            
		            else if("REFSKELT".equals(ch2.chunkID.trim())){
		            	
		            	long numRefSklt = ch2.dataCount;
		            	
		            	for(int i = 0; i < numRefSklt; i++){
		            		bones.add(new Bone(buffer));
	            		}
		            } 
		            
		            else if("RAWWEIGHTS".equals(ch2.chunkID.trim())){
		            	
		            	long numRawWeights = ch2.dataCount;
		            	
		            	for(int i = 0; i < numRawWeights; i++){
		            		rawWeights.add(new RawWeight(buffer));
	            		}
		            }
		            // TODO EXTRAUV0
		            // TODO FACE3200
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        } finally {
        	try {
				inChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		System.exit(0);
	}
	

	public static void main (String args[]){
		
		File file = new File("Z:\\BarrenHardware_Lights_ELight01BA.psk");
		PSKReader psk = new PSKReader(file);

		
	}
}
