package models.cards;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public enum SpellProperty {
    normal,
    counter,
    continuous,
    quickPlay,
    field,
    equip,
    ritual;

    private static SpellPropertyDeserializer spellPropertyDeserializer = null;

    public static SpellPropertyDeserializer get() {
        if (spellPropertyDeserializer == null)
            spellPropertyDeserializer = new SpellPropertyDeserializer();
        return spellPropertyDeserializer;
    }
}

class SpellPropertyDeserializer implements JsonDeserializer<SpellProperty> {

    @Override
    public SpellProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String spellPropertyString = jsonElement.getAsString();
        for (SpellProperty spellProperty : SpellProperty.values())
            if (spellProperty.toString().equalsIgnoreCase(spellPropertyString))
                return spellProperty;

        return null;
    }
}