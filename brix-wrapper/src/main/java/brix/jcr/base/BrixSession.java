package brix.jcr.base;

import java.util.Map;

import javax.jcr.Session;

import brix.jcr.base.action.AbstractActionHandler;
import brix.jcr.base.event.EventsListener;

public interface BrixSession extends Session
{
	public Map<String, Object> getAttributesMap();
	
	public void addActionHandler(AbstractActionHandler handler);
	
	public void addEventsListener(EventsListener listener);
}
