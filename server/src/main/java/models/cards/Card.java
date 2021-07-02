package models.cards;


import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import controller.ActionExecutor;
import controller.EventHandler;
import models.Database;
import models.Deck;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Card implements Cloneable {
    @SerializedName("Name")
    protected String name;
    @SerializedName("Type")
    protected String typeCard;
    protected String overriddenName;
    @SerializedName("Card Type")
    protected CardType type;
    @SerializedName("Description")
    protected String description;
    protected String overriddenDescription;
    protected Deck currentDeck;
    protected ArrayList<PlayType> possiblePlays = new ArrayList<>();
    protected transient ArrayList<Card> horcruxOf = new ArrayList<>();
    protected CardPlacement cardPlacement;
    protected ArrayList<ActionExecutor> effectedCards;
    private int numberOfSummonTimeEffectLeft;
    private int numberOfDeathTimeEffectLeft;
    private int numberOfFlipTimeEffectLeft;
    private int numberOfEndOfTurnTimeEffectLeft;
    @SerializedName("Price")
    protected int price;

    public Card(String name) {
        this.name = name;
        setPrice(this.price);
    }

    public CardPlacement getCardPlacement() {
        return cardPlacement;
    }

    public static CardSerializerForDeckDatabase cardSerializerForDeckDatabase = null;

    public static CardSerializerForDeckDatabase getCardSerializerForDeck() {
        if (cardSerializerForDeckDatabase == null)
            cardSerializerForDeckDatabase = new CardSerializerForDeckDatabase();
        return cardSerializerForDeckDatabase;
    }

    public static CardDeserializerForDeckDatabase cardDeserializerForDeckDatabase = null;

    public static CardDeserializerForDeckDatabase getCardDeserializerForDeck() {
        if (cardDeserializerForDeckDatabase == null)
            cardDeserializerForDeckDatabase = new CardDeserializerForDeckDatabase();
        return cardDeserializerForDeckDatabase;
    }

    public void beACardsHorcrux(Card card) {
        horcruxOf.add(card);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeCard() {
        return typeCard;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public void setCurrentDeck(Deck currentDeck) {
        this.currentDeck = currentDeck;
    }

    public void setPossiblePlays(ArrayList<PlayType> possiblePlays) {
        this.possiblePlays = possiblePlays;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Deck getCurrentDeck() {
        return currentDeck;
    }

    public void setCardPlacement(CardPlacement cardPlacement) {
        if (this instanceof Trap)
            if (cardPlacement == CardPlacement.faceDown)
                EventHandler.assignWaitingEffect(((Trap) this).trigger, this);

        this.cardPlacement = cardPlacement;
    }

    public void consumeEffect(String whichEffect) {
        switch (whichEffect) {
            case "summon-time":
                numberOfSummonTimeEffectLeft--;
                break;
            case "death-time":
                numberOfDeathTimeEffectLeft--;
                break;
            case "flip-time":
                numberOfFlipTimeEffectLeft--;
                break;
            case "end-of-turn":
                numberOfEndOfTurnTimeEffectLeft--;
                break;
        }

    }

    public void goTo(Deck deck) {
        setCurrentDeck(deck);
    }

    public Boolean hasAttributes(String attributeList) {
        if (this instanceof Monster)
            return ((Monster) this).isLike(attributeList);
        else if (this instanceof Spell)
            return ((Spell) this).isLike();
        else
            return ((Trap) this).isLike();
    }

    public Matcher getActionMatcher(String action) {
        Matcher matcher = Pattern.compile("\\*(?<condition>.*)\\*+(?<action>.+)").matcher(action);
        matcher.find();
        return matcher;
    }

    @Override
    public String toString() {
        return name + ':' + description + '\n';
    }

    @Override
    public Card clone() throws CloneNotSupportedException {
        Card card = (Card) super.clone();
        card.setName(this.name);
        card.setType(this.type);
        card.setDescription(this.description);
        card.setPrice(this.price);
        return card;
    }

    public ArrayList<Card> getHrocruxes() {
        if (horcruxOf == null)
            horcruxOf = new ArrayList<>();
        return horcruxOf;
    }
}

class CardSerializerForDeckDatabase implements JsonSerializer<Card> {

    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(card.getName());
    }
}

class CardDeserializerForDeckDatabase implements JsonDeserializer<Card> {

    @Override
    public Card deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Card card;
        String cardName = jsonElement.getAsString();
        card = Database.getInstance().getMonsterByName(cardName);
        if (card != null) {
            return card;
        }
        card = Database.getInstance().getSpellByName(cardName);
        if (card != null) {
            return card;
        }
        card = Database.getInstance().getTrapByName(cardName);
        return card;
    }
}