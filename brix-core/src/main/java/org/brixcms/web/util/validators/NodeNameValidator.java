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

package org.brixcms.web.util.validators;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.util.Arrays;

public class NodeNameValidator implements IValidator<String> {
    public static final char[] forbidden = new char[]{'\\', '/', ':', '?', '<', '>'};

    private static final NodeNameValidator INSTANCE = new NodeNameValidator();

    public static boolean isForbidden(char c) {
        for (int i = 0; i < forbidden.length; ++i) {
            if (c == forbidden[i]) {
                return true;
            }
        }
        return false;
    }

    public static final NodeNameValidator getInstance() {
        return INSTANCE;
    }


    @SuppressWarnings("unchecked")
    public void validate(IValidatable validatable) {
        String s = validatable.getValue().toString();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (isForbidden(c)) {
                String forbiddenStr = Arrays.toString(forbidden);
                forbiddenStr = forbiddenStr.substring(1, forbiddenStr.length() - 1);
                ValidationError error = new ValidationError();
                error
                        .setMessage("Field ${label} may not contain any of the forbidden characters (${forbidden}).");
                error.addMessageKey("NodeNameValidator");
                error.getVariables().put("forbidden", forbiddenStr);
                validatable.error(error);
                return;
            }
        }
    }
}
