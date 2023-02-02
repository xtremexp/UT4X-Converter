package org.xtx.ut4converter;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.Getter;
import lombok.Setter;
import org.xtx.ut4converter.controller.ConversionViewController;

public class MapConverterService extends Service<Object> {

    @Getter
    private final ConversionSettings conversionSettings;

    private MapConverter mapConverterTask;

    @Setter
    private ConversionViewController conversionViewController;

    public MapConverterService(final ConversionSettings conversionSettings){
        this.conversionSettings = conversionSettings;
    }

    @Override
    protected Task<Object> createTask() {
        this.mapConverterTask = new MapConverter(conversionSettings);
        this.mapConverterTask.setConversionViewController(this.conversionViewController);
        return this.mapConverterTask.call();
    }

    @Override
    public boolean cancel() {

        this.mapConverterTask.cancel(true);
        return super.cancel();
    }
}
