package org.xtx.ut4converter.t3d;

import java.util.HashMap;
import java.util.Map;


public class Component {

    /**
     * Actor this component belongs to
     */
    private final T3DActor actor;

    /**
     * Component class
     * E.g: 'StaticMeshComponent'
     */
    private final String componentClass;

    /**
     * Component name (automatically set)
     */
    private final String name;

    /**
     * Obj name of component (automatically set).
     * UE3 property only
     */
    private final String objName;

    /**
     * Maps property name<->values
     */
    private final Map<String, Object> properties = new HashMap<>();

    /**
     * Creates a component given class
     *
     * @param componentClass Component class
     * @param actor          Actor this component belongs to
     */
    public Component(String componentClass, final T3DActor actor) {
        this.componentClass = componentClass;
        //int idxComp = new Random().nextInt(10000);
        this.name = this.componentClass + "0";
        this.objName = this.componentClass + "_" + this.name;
        this.actor = actor;
    }

    /**
     * Creates a component given class and name
     *
     * @param componentClass Component class
     * @param name           Name of component
     * @param actor          Actor this compoennt belongs to
     */
    public Component(final String componentClass, final String name, final T3DActor actor) {
        this.componentClass = componentClass;
        this.name = name;
        this.objName = this.componentClass + "_" + this.name;
        this.actor = actor;
    }

    /**
     * Adds a property to component
     *
     * @param name  Property name
     * @param value Property value
     */
    public void addProp(String name, Object value) {
        this.properties.put(name, value);
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
            return this.name;
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
