package brix.demo.web.tile.stockquote.stateful;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import brix.demo.web.tile.stockquote.StockQuoteRequest;

/**
 * {@link StatefulStockQuoteTile} panel.
 * 
 * @see StatefulStockQuoteTile
 * 
 * @author igor.vaynberg
 * 
 */
public class StatefulStockQuotePanel extends Panel
{
    private static final long serialVersionUID = 1L;

    /** stock symbol */
    private String symbol;

    /** symbol value */
    private String value;

    /**
     * Constructor
     * 
     * @param id
     */
    public StatefulStockQuotePanel(String id)
    {
        super(id);

        // label to display symbol value
        add(new Label("value", new PropertyModel<String>(this, "value")));

        Form<Void> form = new Form<Void>("form")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit()
            {
                // form is submitted, update symbol value
                value = new StockQuoteRequest(symbol).getQuote();
            }
        };
        add(form);

        // symbol name textfield
        form.add(new TextField<String>("symbol", new PropertyModel<String>(this, "symbol")));
    }

}
