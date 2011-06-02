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

package org.brixcms.plugin.site.resource.managers.text;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.brixcms.jcr.wrapper.BrixFileNode;

public class TextMimeTypeValidator implements IValidator<String> {

    public void validate(IValidatable<String> validatable) {
        final String value = validatable.getValue();
        if (!BrixFileNode.isText(value)) {
            ValidationError error = new ValidationError();
            error.setMessage("Only text mime types are allowed (text/* or application/xml)");
            error.addMessageKey(getClass().getSimpleName());
            validatable.error(error);
        }
    }
}
