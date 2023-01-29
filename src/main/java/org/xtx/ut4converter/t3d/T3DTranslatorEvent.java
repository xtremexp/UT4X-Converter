package org.xtx.ut4converter.t3d;

import org.xtx.ut4converter.MapConverter;
import org.xtx.ut4converter.export.UTPackageExtractor;
import org.xtx.ut4converter.ucore.UPackageRessource;

public class T3DTranslatorEvent extends T3DActor {

    private final static String DEFAULT_NEW_MESSAGE_SOUND = "UnrealShare.Pickups.TransA3";

    private UPackageRessource newMessageSound;


    public T3DTranslatorEvent(MapConverter mc, String t3dClass) {
        super(mc, t3dClass);

        registerSimpleProperty("AltMessage", String.class);
        registerSimpleProperty("bTriggerAltMessage", Boolean.class);
        registerSimpleProperty("bTriggerOnceOnly", Boolean.class);
        registerSimpleProperty("Hint", String.class);
        registerSimpleProperty("M_NewMessage", String.class, "New Translator Message");
        registerSimpleProperty("M_TransMessage", String.class, "Translator Message");
        registerSimpleProperty("M_HintMessage", String.class, "New hint message (press F3 to read).");
        registerSimpleProperty("Message", String.class);
        registerSimpleProperty("ReTriggerDelay", Float.class, 0.25f);

        registerSimpleProperty("CollisionRadius", Float.class, 40).setScalable(true);
        registerSimpleProperty("CollisionHeight", Float.class, 40).setScalable(true);
        registerSimplePropertyRessource("NewMessageSound", T3DRessource.Type.SOUND);
    }

    @Override
    public void scale(double newScale) {
        this.registeredProperties.stream().filter(T3DSimpleProperty::isScalable).distinct().forEach(p -> p.scaleProperty(newScale));

        super.scale(newScale);
    }


    @Override
    public void convert() {

        this.registeredProperties.stream().filter(r -> r.getRessourceType() != null && r.getPropertyValue() != null).distinct().forEach(p -> {
            UPackageRessource ressource = mapConverter.getUPackageRessource(p.getPropertyValue().toString(), p.getRessourceType());
            ressource.export(UTPackageExtractor.getExtractor(mapConverter, ressource));
        });

        if (this.registeredProperties.stream().noneMatch(r -> "NewMessageSound".equals(r.getPropertyName()) && r.getPropertyValue() != null)) {
            newMessageSound = mapConverter.getUPackageRessource(DEFAULT_NEW_MESSAGE_SOUND, T3DRessource.Type.SOUND);

            if (mapConverter.convertSounds()) {
                newMessageSound.export(UTPackageExtractor.getExtractor(mapConverter, newMessageSound));
            }
        }

        super.convert();
    }

    public String toT3d() {
        return writeSimpleActor("UTranslatorEvent_C", "CapsuleComponent", "CollisionComp", "=UTranslatorEvent_C'/Game/UEActors/UTranslatorEvent.Default__UTranslatorEvent_C'");
    }
}
