package brix.web.nodepage;

public interface PageParametersAware
{
    public void contributeToPageParameters(BrixPageParameters params);

    public void initializeFromPageParameters(BrixPageParameters params);
}
