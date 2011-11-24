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

package org.brixcms.demo.web.tile.stockquote.stateless;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.demo.web.tile.stockquote.StockQuoteRequest;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.PageParametersAware;
import org.brixcms.web.nodepage.PageParametersForm;

/**
 * {@link StatelessStockQuoteTile} panel. This panel demonstrates Brix's stateless tile functionality.
 * <p/>
 * The state of this panel is the {@link #symbol} variable and so this panel must encode and decode this variable into
 * the url. To facilitate that this panel implements {@link PageParametersAware} which is a Brix interface that includes
 * url related callbacks to make manual state management easier.
 * <p/>
 * See {@link #initializeFromPageParameters(BrixPageParameters)} and {@link #contributeToPageParameters(BrixPageParameters)}
 * to see how this panel keeps the value of {@link #symbol} variable synchronized with the url.
 *
 * @author igor.vaynberg
 */
public class StatelessStockQuotePanel extends Panel implements PageParametersAware {
    private static final long serialVersionUID = 1L;

    /**
     * stock symbol
     */
    private String symbol;

    /**
     * value of stock symbol
     */
    private String value;

    /**
     * Constructor
     *
     * @param id
     */
    public StatelessStockQuotePanel(String id) {
        super(id);

        // display value of stock symbol
        add(new Label("value", new PropertyModel(this, "value")));

        /*
         * notice we use PageParametersForm instead of the regular Form. PageParametersForm is
         * Brix's variant of Wicket's StatelessForm that works with PageParametersAware interface.
         * Also notice that we do not update the value variable inside the onsubmit method, this
         * form will submit and immediately redirect to a url with submitted form values appended
         * into it to keep the url looking clean.
         */
        Form<?> form = new PageParametersForm("form");
        add(form);

        // symbol text field
        form.add(new TextField<String>("symbol", new PropertyModel(this, "symbol")));
    }


    /**
     * {@inheritDoc}
     */
    public void contributeToPageParameters(BrixPageParameters params) {
        // store the symbol into the url
        params.set("symbol", symbol);
    }

    /**
     * {@inheritDoc}
     */
    public void initializeFromPageParameters(BrixPageParameters params) {
        // restore symbol from url
        symbol = params.get("symbol").toString(null);

        // restore value by looking it up
        value = new StockQuoteRequest(symbol).getQuote();
    }
}
