package brix.web.nodepage;

/**
 * Interface implemented by components that want to store certain state in page
 * URL.
 * <p>
 * Used by the PageParameters* components when generating URL. All components
 * implementing this interface are visited and asked to contribute their state
 * to {@link BrixPageParameters}.
 * 
 * @see PageParametersDropDownChoice
 * @see PageParametersForm
 * @see PageParametersLink
 * 
 * @author Matej Knopp
 */
public interface PageParametersAware
{
	/**
	 * Called when new URL is being constructed. Component should contribute the
	 * state it wants to store in URL to the <code>params</code> object
	 * 
	 * @param params
	 */
	public void contributeToPageParameters(BrixPageParameters params);

	/**
	 * Called before component's onBeforeRender. This method allows component
	 * get state from the given <code>params</code> object.
	 * 
	 * @param params
	 */
	public void initializeFromPageParameters(BrixPageParameters params);
}
