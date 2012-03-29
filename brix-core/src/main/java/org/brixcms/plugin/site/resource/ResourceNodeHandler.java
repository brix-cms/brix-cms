/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package org.brixcms.plugin.site.resource;

import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;
import org.brixcms.Brix;
import org.brixcms.auth.Action;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceNodeHandler implements IRequestHandler {
	// ------------------------------ FIELDS ------------------------------

	public static final String SAVE_PARAMETER = Brix.NS_PREFIX + "save";

	private static final Logger log = LoggerFactory.getLogger(ResourceNodeHandler.class);
	private final IModel<BrixNode> node;
	private final Boolean save;

	// --------------------------- CONSTRUCTORS ---------------------------

	public ResourceNodeHandler(IModel<BrixNode> node) {
		super();
		this.node = node;
		this.save = null;
	}

	public ResourceNodeHandler(IModel<BrixNode> node, boolean save) {
		super();
		this.node = node;
		this.save = save;
	}

	// ------------------------ INTERFACE METHODS ------------------------

	// --------------------- Interface IRequestTarget ---------------------

	@Override
	public void respond(IRequestCycle requestCycle) {
		boolean save = (this.save != null) ? this.save : Strings.isTrue(RequestCycle.get().getRequest().getRequestParameters()
				.getParameterValue(SAVE_PARAMETER).toString());

		BrixFileNode node = (BrixFileNode) this.node.getObject();

		if (!SitePlugin.get().canViewNode(node, Action.Context.PRESENTATION)) {
			throw Brix.get().getForbiddenException();
		}

		WebResponse response = (WebResponse) RequestCycle.get().getResponse();

		response.setContentType(node.getMimeType());

		Date lastModified = node.getLastModified();
		response.setLastModifiedTime(Time.valueOf(lastModified));

		try {
			final HttpServletRequest r = (HttpServletRequest) requestCycle.getRequest().getContainerRequest();
			String since = r.getHeader("If-Modified-Since");
			if (!save && since != null) {
				Date d = new Date(r.getDateHeader("If-Modified-Since"));

				// the weird toString comparison is to prevent comparing
				// milliseconds
				if (d.after(lastModified) || d.toString().equals(lastModified.toString())) {
					response.setContentLength(node.getContentLength());
					response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}
			}
			String fileName = node.getName();
			long length = node.getContentLength();
			HttpServletResponse httpServletResponse = (HttpServletResponse) response.getContainerResponse();
            httpServletResponse.setContentType(node.getMimeType());
			InputStream stream = node.getDataAsStream();

			new Streamer(length, stream, fileName, save, r, httpServletResponse).stream();
		} catch (Exception e) {
			log.error("Error writing resource data to content", e);
		}
	}

	@Override
	public void detach(IRequestCycle requestCycle) {
	}
}
