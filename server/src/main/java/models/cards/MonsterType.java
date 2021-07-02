package models.cards;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public enum MonsterType {
    seaSerpent,
    spellCaster,
    warrior,
    dragon,
    thunder,
    cyberse,
    beastWarrior,
    machine,
    insect,
    rock,
    fiend,
    pyro,
    aqua,
    beast,
    fairy;

    private static MonsterTypeDeserializer monsterTypeDeserializer = null;
    public static MonsterTypeDeserializer get() {
    if (monsterTypeDeserializer == null)
        monsterTypeDeserializer = new MonsterTypeDeserializer();
        return monsterTypeDeserializer;
    }
}

class MonsterTypeDeserializer implements JsonDeserializer<MonsterType>{

    @Override
    public MonsterType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String monsterTypeString = jsonElement.getAsString();
        for (MonsterType monsterType : MonsterType.values())
            if (monsterType.toString().equalsIgnoreCase(monsterTypeString))
                return monsterType;

        return null;
    }
}
