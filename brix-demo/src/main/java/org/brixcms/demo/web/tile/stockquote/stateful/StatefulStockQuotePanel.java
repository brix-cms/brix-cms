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

package org.brixcms.demo.web.tile.stockquote.stateful;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.demo.web.tile.stockquote.StockQuoteRequest;

/**
 * {@link StatefulStockQuoteTile} panel.
 *
 * @author igor.vaynberg
 * @see StatefulStockQuoteTile
 */
public class StatefulStockQuotePanel extends Panel {
    private static final long serialVersionUID = 1L;

    /**
     * stock symbol
     */
    private String symbol;

    /**
     * symbol value
     */
    private String value;

    /**
     * Constructor
     *
     * @param id
     */
    public StatefulStockQuotePanel(String id) {
        super(id);

        // label to display symbol value
        add(new Label("value", new PropertyModel<String>(this, "value")));

        Form<Void> form = new Form<Void>("form") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                // form is submitted, update symbol value
                value = new StockQuoteRequest(symbol).getQuote();
            }
        };
        add(form);

        // symbol name textfield
        form.add(new TextField<String>("symbol", new PropertyModel<String>(this, "symbol")));
    }
}
