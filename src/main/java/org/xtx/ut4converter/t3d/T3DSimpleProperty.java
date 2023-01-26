package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.geom.Rotator;
import org.xtx.ut4converter.ucore.UPackageRessource;

import javax.vecmath.Vector3d;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class T3DSimpleProperty {

    private final String propertyName;

    /**
     * Default will be originalName
     * but we might need to have name different
     * than original
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
     * If true then this property is scalable.
     */
    private boolean scalable;

    /**
     * Value
     */
    private Object propertyValue;

    private final boolean isList;
    /**
     *
     */
    private T3DRessource.Type ressourceType;

    /**
     * Default value
     */
    private Object defaultValue;


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
    public T3DSimpleProperty(final String propertyName, final T3DRessource.Type ressourceType, boolean isList) {
        this.propertyName = propertyName;
        this.propertyNameConverted = this.propertyName;
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
                this.propertyValue = new LinkedList<>();
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
            if (this.propertyValue instanceof List) {
                final List<Object> theList = (List<Object>) this.propertyValue;
                theList.add(value);
            } else {
                this.propertyValue = value;
            }
        }


        return value != null;
    }

    void writeProperty(final StringBuilder sbf) {

        if (this.propertyValue != null) {

            if (this.propertyValue instanceof List) {
                final List<Object> values = (List<Object>) this.propertyValue;

                int idx = 0;

                for (final Object value : values) {
                    sbf.append("\t\t").append(propertyNameConverted).append("(").append(idx).append(")=");
                    writeValueProperty(sbf, value);
                    idx++;
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

    public Object getPropertyValue() {
        return propertyValue;
    }

    public T3DRessource.Type getRessourceType() {
        return ressourceType;
    }

    public void clonePropertyAs(String clonePropertyName) {
        this.clonePropertyName = clonePropertyName;
    }
}
