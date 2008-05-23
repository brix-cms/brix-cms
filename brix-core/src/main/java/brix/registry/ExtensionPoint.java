package brix.registry;

public interface ExtensionPoint<T>
{
    public static enum Multiplicity {
        SINGLETON,
        COLLECTION;
    }

    Multiplicity getMultiplicity();

    String getUuid();
}
