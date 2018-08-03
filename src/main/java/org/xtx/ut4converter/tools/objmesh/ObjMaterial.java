package org.xtx.ut4converter.tools.objmesh;


/**
 * TODO handle properties of .obj material info
 */
public class ObjMaterial {

    /**
     * fw.write("newmtl "+ mat.getMaterialName()+ " \n");
     fw.write("Ns 96.078431\n");
     fw.write("Ka 1.000000 1.000000 1.000000\n");
     fw.write("Kd 0.640000 0.640000 0.640000\n");
     fw.write("Ks 0.500000 0.500000 0.500000\n");
     fw.write("Ke 0.000000 0.000000 0.000000\n");
     fw.write("Ni 1.000000\n");
     fw.write("d 1.000000\n");
     fw.write("illum 2\n");
     */

    private String materialName;


    public ObjMaterial(final String materialName){
        this.materialName = materialName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
