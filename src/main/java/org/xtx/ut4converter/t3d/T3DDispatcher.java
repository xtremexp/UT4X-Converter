package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dispatcher trigger class as found in Unreal Engine 1
 */
public class T3DDispatcher extends T3DActor {


    /**
     * Map as [Index, ActorTag]
     */
    private final Map<Integer, String> outEvents;

    private final Map<Integer, Float> outDelays;

    public T3DDispatcher(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
        outEvents = new LinkedHashMap<>();
        outDelays = new LinkedHashMap<>();
    }

    @Override
    public boolean analyseT3DData(String line) {
        if (line.startsWith("OutDelays(")) {

            // OutDelays(7)=4.000000
            final Integer index = Integer.valueOf(line.split("\\(")[1].split("\\)")[0]);
            outDelays.put(index, T3DUtils.getFloat(line));
        }
        else if (line.startsWith("OutEvents(")) {
            // OutEvents(4)="ssHOUND"
            final Integer index = Integer.valueOf(line.split("\\(")[1].split("\\)")[0]);
            outEvents.put(index, T3DUtils.getString(line));
        }

        else {
            return super.analyseT3DData(line);
        }

        return true;
    }

    public String toT3d() {

        sbf.append(IDT).append("Begin Actor Class=Dispatcher_C \n");
        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"DefaultSceneRoot\"\n");
        sbf.append(IDT).append("\tEnd Object\n");
        sbf.append(IDT).append("\tBegin Object Name=\"DefaultSceneRoot\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");
        sbf.append(IDT).append("\tDefaultSceneRoot=DefaultSceneRoot\n");

        for(final Map.Entry<Integer, Float> outDelay : outDelays.entrySet()){
            sbf.append(IDT).append("\tOutDelays(").append(outDelay.getKey()).append(")=").append(outDelay.getValue()).append("\n");
        }

        for(final Map.Entry<Integer, String> outEvent : outEvents.entrySet()){
            sbf.append(IDT).append("\tOutEvents(").append(outEvent.getKey()).append(")=\"").append(outEvent.getValue()).append("\"\n");
        }

        sbf.append(IDT).append("\tRootComponent=DefaultSceneRoot\n");

        writeEndActor();

        return super.toString();
    }
}
