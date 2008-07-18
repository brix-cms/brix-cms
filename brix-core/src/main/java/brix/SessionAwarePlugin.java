package brix;

import brix.jcr.base.BrixSession;

public interface SessionAwarePlugin extends Plugin
{

	public void onWebDavSession(BrixSession session);
	
}
