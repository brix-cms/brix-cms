package brix.registry;

public interface Point<T>
{
    /*
     * FIXME implement multiplicities: EXACTLY_ONE, AT_MOST_ONE, AT_LEAST_ONE
     */
    public static enum Multiplicity {
        SINGLETON,
        COLLECTION
    }

    Multiplicity getMultiplicity();

    String getUuid();
}
