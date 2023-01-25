package org.xtx.ut4converter.t3d;

import java.util.*;


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
     * If true this component should not be referenced in actor properties
     */
    protected boolean noListInActorComponends;

    private Component parentComponent;

    private final List<Component> subComponents = new ArrayList<>();


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
        //this.objName = this.componentClass + "_" + this.name;
        this.objName = this.componentClass + "_" + new Random().nextInt(10000);
        this.actor = actor;
    }

    /**
     * Adds a property to component
     *
     * @param name  Property name
     * @param value Property value
     */
    public Component addProp(String name, Object value) {
        this.properties.put(name, value);
        return this;
    }

    public String toT3D(int ueVersion) {
        final StringBuilder sb = new StringBuilder();
        String IDT = this.parentComponent == null ? "" : "\t";

        if (ueVersion >= 4) {
            sb.append(IDT).append("\t\tBegin Object Class=").append(this.getComponentClass()).append(" Name=\"").append(this.getName()).append("\"\n");
            sb.append(IDT).append("\t\tEnd Object\n");
        }

        // Archetype=StaticMeshComponent'Engine.Default__StaticMeshActor:StaticMeshComponent0'
        final String compArchetype = this.getComponentClass() + "'Engine.Default__" + this.actor.getT3dClass() + ":" + this.getName() + "'";

        sb.append(IDT).append("\t\tBegin Object Class=").append(this.getComponentClass()).append(" Name=").append(this.getName());

        if (ueVersion == 3) {
            sb.append(" ObjName=").append(this.getObjName()).append(" Archetype=").append(compArchetype);
        }
        sb.append("\n");

        for (final Component subComp : subComponents) {
            sb.append(subComp.toT3D(ueVersion));
        }

        for (Map.Entry<String, Object> entry : this.getProperties().entrySet()) {
            sb.append(IDT).append("\t\t\t").append(entry.getKey()).append("=").append(entry.getValue() != null ? entry.getValue().toString() : "None").append("\n");
        }

        // location is inside component for UE4, write location only for the root component (level 0 = no parent component)
        if (ueVersion >= 4 && parentComponent == null) {
            sb.append(this.actor.writeLocRotAndScaleAsString());
        }

        if (ueVersion == 3) {
            sb.append(IDT).append("\t\t\tName=\"").append(this.getObjName()).append("\"\n");
            sb.append(IDT).append("\t\t\tObjectArchetype=").append(compArchetype).append("\n");
        }

        sb.append(IDT).append("\t\tEnd Object\n");

        return sb.toString();
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



    public void addSubComponent(final Component component) {
        if (component != null) {
            component.parentComponent = this;
            this.subComponents.add(component);
        }
    }
}
