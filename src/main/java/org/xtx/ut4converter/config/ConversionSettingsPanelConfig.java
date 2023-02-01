package org.xtx.ut4converter.config;

import org.xtx.ut4converter.ConversionSettings;

import java.util.List;

public class ConversionSettingsPanelConfig {

    private List<Double> scaleFactorList;

    private Double defaultScaleFactor;

    private List<Float> lightRadiusFactorList;

    private Float defaultLightRadiusFactor;

    private List<Integer> lightMapResolutionList;

    private Integer defaultLightMapResolution;

    private List<Float> soundVolumeFactorList;

    private Float defaultSoundVolumeFactor;

    private ConversionSettings.ExportOption defaultExport;


    public ConversionSettingsPanelConfig() {

    }

    public List<Float> getLightRadiusFactorList() {
        return lightRadiusFactorList;
    }

    public void setLightRadiusFactorList(List<Float> lightRadiusFactorList) {
        this.lightRadiusFactorList = lightRadiusFactorList;
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

    public Float getDefaultLightRadiusFactor() {
        return defaultLightRadiusFactor;
    }

    public void setDefaultLightRadiusFactor(Float defaultLightRadiusFactor) {
        this.defaultLightRadiusFactor = defaultLightRadiusFactor;
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

    public ConversionSettings.ExportOption getDefaultExport() {
        return defaultExport;
    }

    public void setDefaultExport(ConversionSettings.ExportOption defaultExport) {
        this.defaultExport = defaultExport;
    }
}
