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

package org.brixcms.demo.web;

/**
 * Hybrid strategy that encodes parameters into the query string instead of the request path.
 *
 * @author igor.vaynberg
 */
//@Deprecated
//public class QueryStringHybridUrlCodingStrategy extends HybridUrlCodingStrategy {
//// --------------------------- CONSTRUCTORS ---------------------------
//
//    /**
//     * {@inheritDoc}
//     */
//    public QueryStringHybridUrlCodingStrategy(String mountPath, Class<? extends Page> pageClass) {
//        super(mountPath, pageClass);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public QueryStringHybridUrlCodingStrategy(String mountPath, Class<? extends Page> pageClass,
//                                              boolean redirectOnBookmarkableRequest) {
//        super(mountPath, pageClass, redirectOnBookmarkableRequest);
//    }
//
//// -------------------------- OTHER METHODS --------------------------
//
//    /**
//     * Encodes Map into a url fragment and appends that to the provided url buffer.
//     *
//     * @param url        url so far
//     * @param parameters Map object to be encoded
//     */
//    @Override
//    protected void appendParameters(AppendingStringBuffer url, Map<String, ?> parameters) {
//        if (parameters != null && parameters.size() > 0) {
//            for (Entry<?, ?> entry1 : parameters.entrySet()) {
//                Object value = ((Entry<?, ?>) entry1).getValue();
//                if (value != null) {
//                    if (value instanceof String[]) {
//                        String[] values = (String[]) value;
//                        for (String value1 : values) {
//                            appendValue(url, ((Entry<?, ?>) entry1).getKey().toString(), value1);
//                        }
//                    } else {
//                        appendValue(url, ((Entry<?, ?>) entry1).getKey().toString(), value
//                                .toString());
//                    }
//                }
//            }
//        }
//    }
//
//    private void appendValue(AppendingStringBuffer url, String key, String value) {
//        final String escapedKey = urlEncodePathComponent(key);
//        final String escapedValue = urlEncodePathComponent(value);
//
//        if (!Strings.isEmpty(escapedValue)) {
//            url.append((url.indexOf("?") < 0) ? "?" : "&");
//            url.append(escapedKey).append("=").append(escapedValue);
//        }
//    }
//}
