package brix.registry;

public interface Point<T>
{
    public static enum Multiplicity {
        SINGLETON_REQUIRED,
        SINGLETON,
        COLLECTION_NOT_EMPTY,
        COLLECTION;
    }

    Multiplicity getMultiplicity();

    String getUuid();
}
