package brix.registry;

public interface Point<T>
{
    public static enum Multiplicity {
        SINGLETON,
        COLLECTION;
    }

    Multiplicity getMultiplicity();

    String getUuid();
}
