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

package org.brixcms.web;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.resource.loader.ComponentStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.brixcms.registry.ExtensionPoint;
import org.brixcms.registry.ExtensionPointRegistry.Listener;

import java.util.Locale;
import java.util.WeakHashMap;

/**
 * {@link IStringResourceLoader} that checks extension point classes for
 * contributed resource bundles
 * 
 * @author igor.vaynberg
 */
public class BrixExtensionStringResourceLoader extends
		ComponentStringResourceLoader implements Listener {

	/**
	 * The application we are loading for.
	 */
	private final WeakHashMap<Class<?>, Object> classes = new WeakHashMap<Class<?>, Object>();

	@Override
	public String loadStringResource(final Class<?> clazz, final String key,
			final Locale locale, final String style, String variation) {
		for (Class<?> cl : classes.keySet()) {
			String value = super.loadStringResource(cl, key, locale, style,
					variation);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final String loadStringResource(Component component, String key,
			Locale locale, String style, String variation) {
		if (component == null) {
			return loadStringResource((Class<?>) null, key, locale, style,
					variation);
		}
		return super.loadStringResource(component, key, locale, style,
				variation);
	}

	/**
	 * @see Listener#registered(ExtensionPoint, Object)
	 */
	public void registered(ExtensionPoint<?> point, Object extension) {
		classes.put(extension.getClass(), null);
	}

	/**
	 * @see Listener#unregistered(ExtensionPoint, Object)
	 */
	public void unregistered(ExtensionPoint<?> point, Object extension) {
		classes.remove(extension.getClass());
	}
}