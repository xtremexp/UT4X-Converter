package org.xtx.ut4converter.t3d;

import lombok.Getter;
import lombok.Setter;
import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.vecmath.Vector3d;
import java.util.*;

/**
 *
 */
public class T3DSimpleProperty {

    /**
     * Original property name
     */
    @Getter
    @Setter
    private final String propertyName;

    /**
     * Group this property belong to
     */
    private String propertyGroup;

    /**
     * Converted property.
     * By default is the original property name.
     */
    private final String propertyNameConverted;

    /**
     * Used if we want to copy this property with another name.
     * Used on write
     */
    private String clonePropertyName;

    /**
     * E.G: Float, Integer, Enum
     */
    private final Class propertyClass;

    /**
     * If true then this property is scalable. (e.g: 'Radius' property)
     * Used for map scale.
     */
    private boolean scalable;

    /**
     * Value.
     * Is a map of [Index, Value] if isList is true
     */
    @Getter
    private Object propertyValue;

    /**
     * If true the given property is an array
     */
    private final boolean isList;

    /**
     * If propertyClass is an UPackageRessource,
     * this is the type of ressources asssociated with. (texture, sound, ...)
     */
    @Getter
    private T3DRessource.Type ressourceType;

    /**
     * Default value
     */
    @Getter
    @Setter
    private Object defaultValue;

    public T3DSimpleProperty(final String propertyName, final String propertyGroup, final Class<?> propertyClass, final Object defaultValue, boolean isList) {
        this.propertyName = propertyName;
        this.propertyNameConverted = this.propertyName;
        this.propertyGroup = propertyGroup;
        this.propertyClass = propertyClass;
        this.isList = isList;
        this.defaultValue = defaultValue;
    }

    /**
     * Register simple property from single float, string, ...
     *
     * @param propertyName  Property name
     * @param propertyClass Property class
     */
    public T3DSimpleProperty(final String propertyName, final Class<?> propertyClass, final Object defaultValue, boolean isList) {
        this.propertyName = propertyName;
        this.propertyNameConverted = this.propertyName;
        this.propertyClass = propertyClass;
        this.isList = isList;
        this.defaultValue = defaultValue;
    }

    /**
     * Register simple property ressource
     *
     * @param propertyName  Property name
     * @param ressourceType Ressource type
     */
    public T3DSimpleProperty(final String propertyName, final String group, final T3DRessource.Type ressourceType, boolean isList) {
        this.propertyName = propertyName;
        this.propertyNameConverted = this.propertyName;
        this.propertyGroup = group;
        this.propertyClass = UPackageRessource.class;
        this.ressourceType = ressourceType;
        this.isList = isList;
    }

    public boolean readPropertyFromT3dLine(final String line, final MapConverter mapConverter){

        // property ever parsed before and not a list
        if (this.propertyValue != null && !this.isList) {
            return false;
        }

        boolean hasLineProp;

        if (this.isList) {
            if (this.propertyValue == null) {
                this.propertyValue = new HashMap<>();
            }
            hasLineProp = line.toLowerCase().startsWith(this.propertyName.toLowerCase() + "(");
        } else {
            hasLineProp = line.toLowerCase().startsWith(this.propertyName.toLowerCase() + "=");
        }

        Object value = null;

        if (hasLineProp) {
            if(propertyClass == String.class){
                value = T3DUtils.getString(line);
            }
            else if(propertyClass == Short.class){
                value = T3DUtils.getShort(line);
            }
            else if(propertyClass == Float.class){
                value = T3DUtils.getFloat(line);
            }
            else if(propertyClass == Boolean.class){
                value = T3DUtils.getBoolean(line);
            }
            else if(propertyClass == Integer.class){
                value = T3DUtils.getInteger(line);
            }
            else if(propertyClass == Double.class){
                value = T3DUtils.getDouble(line);
            }
            else if(propertyClass == Vector3d.class){
                value = T3DUtils.getVector3d(line, 0d);
            }
            else if(propertyClass == Rotator.class){
                value = T3DUtils.getVector3dRot(line);
            }
            else if (propertyClass.isEnum()) {
                value = Enum.valueOf(propertyClass, T3DUtils.getString(line));
            }
            else if(propertyClass == UPackageRessource.class){
                value = T3DUtils.getUPackageRessource(mapConverter, line, this.ressourceType);
            }
        }

        if (value != null) {
            if (this.propertyValue instanceof HashMap hashMap) {
                int index = T3DUtils.parseArrayIndex(line);
                hashMap.put(index, value);
            } else {
                this.propertyValue = value;
            }
        }


        return value != null;
    }

    void writeProperty(final StringBuilder sbf) {

        if (this.propertyValue != null) {

            if (this.propertyValue instanceof HashMap hashMap) {

                for (Object index : hashMap.keySet()) {
                    sbf.append("\t\t").append(propertyNameConverted).append("(").append(index).append(")=");
                    writeValueProperty(sbf, hashMap.get(index));
                }
            } else {
                sbf.append("\t\t").append(propertyNameConverted).append("=");
                writeValueProperty(sbf, this.propertyValue);

                if (clonePropertyName != null) {
                    sbf.append("\t\t").append(clonePropertyName).append("=");
                    writeValueProperty(sbf, this.propertyValue.toString());
                }
            }
        }
    }

    void scaleProperty(double scale) {

        if (!this.isScalable()) {
            return;
        }

        if (this.propertyValue == null) {
            this.propertyValue = defaultValue;
        }

        if (this.propertyValue != null) {
            if (this.getPropertyValue() instanceof Float f) {
                this.propertyValue = scale * f;
            } else if (this.getPropertyValue() instanceof Integer i) {
                this.propertyValue = scale * i;
            } else if (this.getPropertyValue() instanceof Double d) {
                this.propertyValue = scale * d;
            } else if (this.getPropertyValue() instanceof Short s) {
                this.propertyValue = scale * s;
            } else if (this.getPropertyValue() instanceof Vector3d v) {
                v.scale(scale);
            }
        }
    }

    private void writeValueProperty(StringBuilder sbf, Object value) {
        if (value instanceof String) {
            sbf.append("\"").append(value).append("\"\n");
        } else if (value instanceof final UPackageRessource packageRessource) {

            if (ressourceType == T3DRessource.Type.SOUND) {
                sbf.append("SoundCue'").append((packageRessource).getConvertedName()).append("'\n");
            }
            // MESHES will be converted to staticmeshes
            else if (ressourceType == T3DRessource.Type.STATICMESH || ressourceType == T3DRessource.Type.MESH) {
                sbf.append("StaticMesh'").append((packageRessource).getConvertedName()).append("'\n");
            } else if (ressourceType == T3DRessource.Type.TEXTURE) {
                sbf.append("Material'").append((packageRessource).getConvertedName()).append("'\n");
            }
        } else if (value instanceof Enum<?> enumValue) {
            sbf.append("NewEnumerator").append(enumValue.ordinal()).append("\n");
        } else {
            sbf.append(value).append("\n");
        }
    }

    public void setScalable(boolean scalable) {
        this.scalable = scalable;
    }

    public boolean isScalable() {
        return scalable;
    }


    public T3DSimpleProperty clonePropertyAs(String clonePropertyName) {
        this.clonePropertyName = clonePropertyName;
        return this;
    }


    @Override
    public String toString() {
        return "T3DSimpleProperty{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyClass=" + propertyClass +
                '}';
    }
}
