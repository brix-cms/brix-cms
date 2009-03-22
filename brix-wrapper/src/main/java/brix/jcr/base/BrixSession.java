package brix.jcr.base;

import java.util.Map;

import javax.jcr.Session;

import brix.jcr.base.action.AbstractActionHandler;
import brix.jcr.base.event.EventsListener;
import brix.jcr.base.filter.ValueFilter;

public interface BrixSession extends Session
{
	public Map<String, Object> getAttributesMap();
	
	public void addActionHandler(AbstractActionHandler handler);
	
	public void addEventsListener(EventsListener listener);
	
	public void setValueFilter(ValueFilter valueFilter);
	
	public ValueFilter getValueFilter();
}
