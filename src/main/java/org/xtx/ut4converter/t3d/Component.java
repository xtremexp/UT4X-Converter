package org.xtx.ut4converter.t3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Component {

    private final T3DActor actor;

    private final String componentClass;

    private final String name;

    private final String objName;

    private final Map<String, Object> properties = new HashMap<>();

    /**
     * *
     *
     * @param componentClass Component class
     * @param actor          Actor this component belongs to
     */
    public Component(String componentClass, final T3DActor actor) {
        this.componentClass = componentClass;
        int idxComp = new Random().nextInt(10000);
        this.name = this.componentClass + idxComp;
        this.objName = this.componentClass + "_" + this.name;
        this.actor = actor;
    }

    public Component addProp(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    /**
     * StaticMeshComponent'StaticMeshComponent_StaticMeshComponent0_21'
     *
     * @return
     */
    public String getReference(int ueVersion) {
        if (ueVersion == 3) {
            return this.componentClass + "'" + this.objName + "'";
        } else {
            return this.objName;
        }
    }

    public String getComponentClass() {
        return componentClass;
    }

    public String getName() {
        return name;
    }

    public String getObjName() {
        return objName;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
