package models.cards;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public enum CardType {
    normal,
    effect,
    ritual;


    private static CardTypeDeserializer cardTypeDeserializer = null;
    public static CardTypeDeserializer get() {
        if (cardTypeDeserializer == null)
            cardTypeDeserializer = new CardTypeDeserializer();
        return cardTypeDeserializer;
    }
}

class CardTypeDeserializer implements JsonDeserializer<CardType> {

    @Override
    public CardType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String cardTypeString = jsonElement.getAsString();
        for (CardType cardType : CardType.values())
            if (cardType.toString().equalsIgnoreCase(cardTypeString))
                return cardType;

        return null;
    }
}
