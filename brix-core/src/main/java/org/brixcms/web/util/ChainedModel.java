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

package org.brixcms.web.util;

import org.apache.wicket.model.IModel;

public abstract class ChainedModel implements IModel {
    private final IModel chained;

    public ChainedModel(IModel model) {
        super();
        this.chained = model;
    }



    public void detach() {
        chained.detach();
    }

    public final Object getObject() {
        return getObject(chained);
    }

    public void setObject(Object object) {
        setObject(object, chained);
    }

    protected abstract Object getObject(IModel chained);

    protected abstract void setObject(Object object, IModel chained);
}
