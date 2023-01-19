package org.xtx.ut4converter.tools.ase;

import org.xtx.ut4converter.MainApp;
import org.xtx.ut4converter.tools.t3dmesh.Triangle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class AseStaticMesh {

    /**
     * Default format for numbers in exported .ase staticmeshes
     */
    public static final DecimalFormat dfAse = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.US));

    public static void writeHeader(FileWriter fw, File importFile) throws IOException {
        fw.write("*3DSMAX_ASCIIEXPORT 200\n");
        fw.write("*COMMENT \"Imported from " + importFile + "\"\n");
        fw.write("*COMMENT \"Exported with " + MainApp.PROGRAM_NAME + " v" + MainApp.VERSION + "\"\n");
    }

    public static void writeMaterials(FileWriter fw, List<String> matNameList) throws IOException {

        int idx = 0;
        fw.write("*MATERIAL_LIST {\n");
        fw.write("\t*MATERIAL_COUNT " + matNameList.size() + "\n");

        for (String m : matNameList) {
            fw.write("\t*MATERIAL " + idx + " {\n");
            fw.write("\t\t*MATERIAL_NAME \"" + m + "\"\n");
            fw.write("\t\t*NUMSUBMTLS 0\n");
            fw.write("\t\t*MAP_DIFFUSE {\n");
            fw.write("\t\t\t*MAP_NAME \"Map #" + idx + "\"\n");
            fw.write("\t\t\t*MAP_CLASS \"Bitmap\"\n");
            fw.write("\t\t\t*BITMAP \"" + m + "\"\n");
            fw.write("\t\t}\n");
            fw.write("\t}\n");
            idx++;
        }

        fw.write("}\n");
    }

    /**
     * Write material info.
     * For UT3 need to use 1 material and X submaterials else UT3 will only import one material data.
     *
     * @param fw          File writer
     * @param matNameList List of distinct material names
     * @throws IOException Error writing file
     */
    public static void writeMaterialWithSubMats(FileWriter fw, List<String> matNameList) throws IOException {

        fw.write("*MATERIAL_LIST {\n");
        fw.write("\t*MATERIAL_COUNT 1\n");
        fw.write("\t*MATERIAL 0 {\n");
        fw.write("\t\t*MATERIAL_NAME \"Default Material\"\n");
        fw.write("\t\t*MATERIAL_CLASS \"Multi/Sub-Object\"\n");
        fw.write("\t\t*MATERIAL_AMBIENT 1.0000   1.0000   1.0000\n");
        fw.write("\t\t*MATERIAL_DIFFUSE 1.0000   1.0000   1.0000\n");
        fw.write("\t\t*MATERIAL_SPECULAR 1.0000   1.0000   1.0000\n");
        fw.write("\t\t*NUMSUBMTLS " + matNameList.size() + "\n");

        int idx = 0;

        for (String m : matNameList) {
            fw.write("\t\t*SUBMATERIAL " + idx + " {\n");
            fw.write("\t\t\t*MATERIAL_NAME \"" + m + "\"\n");
            fw.write("\t\t\t\t*NUMSUBMTLS 0\n");
            fw.write("\t\t\t*MAP_DIFFUSE {\n");
            fw.write("\t\t\t\t*MAP_NAME \"Map #" + idx + "\"\n");
            fw.write("\t\t\t\t*MAP_CLASS \"Bitmap\"\n");
            fw.write("\t\t\t\t*BITMAP \"" + m + "\"\n");
            fw.write("\t\t\t}\n");
            fw.write("\t\t}\n");
            idx++;
        }

        fw.write("\t}\n");
        fw.write("}\n");
    }
}
