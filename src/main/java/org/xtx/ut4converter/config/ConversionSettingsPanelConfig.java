package org.xtx.ut4converter.config;

import org.xtx.ut4converter.MapConverter;

import java.util.List;

public class ConversionSettingsPanelConfig {

    private List<Double> scaleFactorList;

    private Double defaultScaleFactor;

    private List<Float> lightBrightnessFactorList;

    private Float defaultLightBrightnessFactor;

    private List<Integer> lightMapResolutionList;

    private Integer defaultLightMapResolution;

    private List<Float> soundVolumeFactorList;

    private Float defaultSoundVolumeFactor;

    private MapConverter.ExportOption defaultExport;


    public ConversionSettingsPanelConfig() {

    }

    public List<Float> getLightBrightnessFactorList() {
        return lightBrightnessFactorList;
    }

    public void setLightBrightnessFactorList(List<Float> lightBrightnessFactorList) {
        this.lightBrightnessFactorList = lightBrightnessFactorList;
    }

    public List<Integer> getLightMapResolutionList() {
        return lightMapResolutionList;
    }

    public void setLightMapResolutionList(List<Integer> lightMapResolutionList) {
        this.lightMapResolutionList = lightMapResolutionList;
    }

    public List<Float> getSoundVolumeFactorList() {
        return soundVolumeFactorList;
    }

    public void setSoundVolumeFactorList(List<Float> soundVolumeFactorList) {
        this.soundVolumeFactorList = soundVolumeFactorList;
    }

    public Float getDefaultLightBrightnessFactor() {
        return defaultLightBrightnessFactor;
    }

    public void setDefaultLightBrightnessFactor(Float defaultLightBrightnessFactor) {
        this.defaultLightBrightnessFactor = defaultLightBrightnessFactor;
    }

    public Integer getDefaultLightMapResolution() {
        return defaultLightMapResolution;
    }

    public void setDefaultLightMapResolution(Integer defaultLightMapResolution) {
        this.defaultLightMapResolution = defaultLightMapResolution;
    }

    public Float getDefaultSoundVolumeFactor() {
        return defaultSoundVolumeFactor;
    }

    public void setDefaultSoundVolumeFactor(Float defaultSoundVolumeFactor) {
        this.defaultSoundVolumeFactor = defaultSoundVolumeFactor;
    }

    public List<Double> getScaleFactorList() {
        return scaleFactorList;
    }

    public void setScaleFactorList(List<Double> scaleFactorList) {
        this.scaleFactorList = scaleFactorList;
    }

    public Double getDefaultScaleFactor() {
        return defaultScaleFactor;
    }

    public void setDefaultScaleFactor(Double defaultScaleFactor) {
        this.defaultScaleFactor = defaultScaleFactor;
    }

    public MapConverter.ExportOption getDefaultExport() {
        return defaultExport;
    }

    public void setDefaultExport(MapConverter.ExportOption defaultExport) {
        this.defaultExport = defaultExport;
    }
}
