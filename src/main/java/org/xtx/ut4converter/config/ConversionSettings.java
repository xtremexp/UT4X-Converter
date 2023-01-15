package org.xtx.ut4converter.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.File;
import java.util.Objects;

/**
 * Conversion settings for recent file conversion in menu.
 * Might be integrated in mapconverter class later.
 */
public class ConversionSettings {

    /**
     * Input game
     */
    @Size(min = 2)
    @NotBlank
    private String inputGameId;

    /**
     * Output game
     */
    @Size(min = 2)
    @NotBlank
    private String outputGameId;

    /**
     * Input file map
     */
    @NotBlank
    private File inputMap;

    /**
     * Output map name
     * E.g: CTF-Face-UT99
     */
    @NotBlank
    private String outputMapName;

    /**
     * Map scale factor
     */
    @NotNull
    @Min(0)
    private Double scaleFactor;

    /**
     * UE4 reference path (for output ut4 maps)
     */
    private String ue4RefBaseFolder;

    /**
     * Export hierarchy
     */
    @NotNull
    private String exportOption;

    public String getInputGameId() {
        return inputGameId;
    }

    public void setInputGameId(String inputGameId) {
        this.inputGameId = inputGameId;
    }

    public String getOutputGameId() {
        return outputGameId;
    }

    public void setOutputGameId(String outputGameId) {
        this.outputGameId = outputGameId;
    }

    public File getInputMap() {
        return inputMap;
    }

    public void setInputMap(File inputMap) {
        this.inputMap = inputMap;
    }

    public String getOutputMapName() {
        return outputMapName;
    }

    public void setOutputMapName(String outputMapName) {
        this.outputMapName = outputMapName;
    }

    public Double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(Double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public String getUe4RefBaseFolder() {
        return ue4RefBaseFolder;
    }

    public void setUe4RefBaseFolder(String ue4RefBaseFolder) {
        this.ue4RefBaseFolder = ue4RefBaseFolder;
    }

    public String getExportOption() {
        return exportOption;
    }

    public void setExportOption(String exportOption) {
        this.exportOption = exportOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversionSettings that = (ConversionSettings) o;
        return Objects.equals(inputGameId, that.inputGameId) && Objects.equals(outputGameId, that.outputGameId) && Objects.equals(inputMap, that.inputMap) && Objects.equals(outputMapName, that.outputMapName) && Objects.equals(scaleFactor, that.scaleFactor) && Objects.equals(ue4RefBaseFolder, that.ue4RefBaseFolder) && Objects.equals(exportOption, that.exportOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputGameId, outputGameId, inputMap, outputMapName, scaleFactor, ue4RefBaseFolder, exportOption);
    }
}
