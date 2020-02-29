package org.xtx.ut4converter.t3d;


import org.xtx.ut4converter.MapConverter;

/**
 * Seen in UE3/UE4
 */
public class Camera extends T3DActor {

    private Float fieldOfView;

    private Float aspectRatio;

    private String ue4CameraClass = "CameraActor";

    public Camera(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);
    }

    @Override
    public boolean analyseT3DData(String line) {

        if (line.startsWith("FOVAngle=")) {
            fieldOfView = T3DUtils.getFloat(line);
        } else if (line.startsWith("AspectRatio=")) {
            aspectRatio = T3DUtils.getFloat(line);
        }

        return super.analyseT3DData(line);
    }

    /**
     * @return
     */
    public String toT3d() {

        sbf.append(IDT).append("Begin Actor Class=").append(ue4CameraClass).append(" Name=").append(name).append("\n");

        sbf.append(IDT).append("\tBegin Object Class=CameraComponent Name=\"CameraComponent\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Class=SceneComponent Name=\"SceneComponent\"\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"CameraComponent\"\n");

        if (fieldOfView != null) {
            sbf.append(IDT).append("\t\tFieldOfView=").append(fieldOfView).append("\n");
        }

        if (aspectRatio != null) {
            sbf.append(IDT).append("\t\tAspectRatio=").append(aspectRatio).append("\n");
        }

        sbf.append(IDT).append("\tAttachParent=SceneComponent\n");
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tBegin Object Name=\"SceneComponent\"\n");
        writeLocRotAndScale();
        sbf.append(IDT).append("\tEnd Object\n");

        sbf.append(IDT).append("\tCameraComponent=CameraComponent\n");
        sbf.append(IDT).append("\tSceneComponent=SceneComponent\n");
        sbf.append(IDT).append("\tRootComponent=SceneComponent\n");

        writeEndActor();

        return sbf.toString();
    }


    @Override
    public void convert() {

        // UT2003/4
        if ("SpectatorCam".equals(this.t3dOriginClass)) {
            this.ue4CameraClass = "UTSpectatorCamera";
        }

        super.convert();
    }
}
