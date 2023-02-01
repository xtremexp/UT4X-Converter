package org.xtx.ut4converter;

import lombok.Getter;
import lombok.Setter;
import org.xtx.ut4converter.t3d.T3DUtils;
import org.xtx.ut4converter.ucore.UnrealGame;

import java.io.File;
import java.nio.file.Path;

/**
 * Settings for map conversion
 */
public class ConversionSettings {

    /**
     * Input map. Can be either a map (.unr, ...) or unreal text map (.t3d)
     */
    @Getter
    @Setter
    private File inputMapFile;

    /**
     * Input t3d map file. Both inputMapFile and inputT3DMapFile are used by UT3
     */
    @Getter
    @Setter
    private File inputT3DMapFile;

    /**
     * Unreal game of input map
     */
    @Getter
    private final UnrealGame inputGame;

    /**
     * Desired output game conversion
     */
    @Getter
    private final UnrealGame outputGame;

    @Getter
    @Setter
    private String outputMapName;

    /**
     * Where all converted stuff will be converted
     */
    @Getter
    @Setter
    private Path outputFolder;

    /**
     *
     */
    @Getter
    @Setter
    private ExportOption exportOption = ExportOption.BY_TYPE;

    /**
     * Map scale factor
     */
    @Getter
    @Setter
    private double scaleFactor = 1d;

    /**
     * Map light actors radius scale factor
     */
    @Getter
    @Setter
    private float lightRadiusFactor = 1f;

    /**
     * Default light map resolution for brushes
     */
    @Getter
    @Setter
    private int lightMapResolution = 64;

    /**
     * Sound actor volume scale factor
     */
    @Getter
    @Setter
    private float soundVolumeFactor = 1f;

    /**
     * If true will export staticmeshes ressources
     */
    @Getter
    @Setter
    private boolean convertStaticMeshes = true;

    /**
     * If true will export texture files (.bmp, .png, ...)
     */
    @Getter
    @Setter
    private boolean convertTextures = true;

    /**
     * If true will export sound files (.wav)
     */
    @Getter
    @Setter
    private boolean convertSounds = true;

    /**
     * If true will export music files (.umx, .ogg, ...)
     */
    @Getter
    @Setter
    private boolean convertMusic = true;

    /**
     * If true will use Unreal 1 blueprints actor classes for UT4
     * for some actors
     */
    @Getter
    @Setter
    private boolean useU1BPActorClasses;

    /**
     * getMapConvertFolder().getAbsolutePath() + File.separator + ressource.getType().getName() + File.separator
     */
    @Getter
    @Setter
    private String ut4ReferenceBaseFolder;

    /**
     * Actor classes that should be converted. If null or empty then all classes
     * will be converted.
     */
    @Getter
    @Setter
    private String[] filteredClasses;

    /**
     *
     */
    public enum ExportOption {

        /**
         * All resources might be imported for each type
         * E.g:
         * \Content\Converted\DM-Arcane-UT99\Textures
         * \Content\Converted\DM-Arcane-UT99\Textures
         */
        BY_TYPE("<MapName>/<Type>"),
        /**
         * All resources might be imported by package
         * E.g:
         * \Content\Converted\DM-Arcane-UT99\GenEarth
         * \Content\Converted\DM-Arcane-UT99\GenFluid
         */
        BY_PACKAGE("<MapName>/<Package>");

        private final String label;


        ExportOption(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public ConversionSettings(UnrealGame inputGame, UnrealGame outputGame){
        this.inputGame = inputGame;
        this.outputGame = outputGame;
    }

    public ConversionSettings(File inputMapFile, UnrealGame inputGame, UnrealGame outputGame, String outputMapName, ExportOption exportOption) {
        this.inputMapFile = inputMapFile;
        this.inputGame = inputGame;
        this.outputGame = outputGame;
        this.outputMapName = outputMapName;
        this.exportOption = exportOption;
    }

    public void refreshOutputMapNameAndUT4RefBaseFolder() {
        if (this.outputMapName == null) {
            this.outputMapName = this.getInputMapFile().getName().split("\\.")[0] + "-" + this.getInputGame().getShortName();

            // Remove bad chars from name (e.g: DM-Cybrosis][ -> DM-Cybrosis)
            // else ue4 editor won't be able to set sounds or textures to actors
            this.outputMapName = T3DUtils.filterName(outputMapName);
        }

        this.setUt4ReferenceBaseFolder(UTGames.UE4_FOLDER_MAP + "/" + this.outputMapName);
    }
}
