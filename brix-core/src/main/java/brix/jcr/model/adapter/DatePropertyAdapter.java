package brix.jcr.model.adapter;

import java.util.Calendar;
import java.util.Date;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

class DatePropertyAdapter extends BasePropertyAdapter
{

    public DatePropertyAdapter(String propertyName, Date defaultValue, NodeAdapter nodeAdapter)
    {
        super(propertyName, defaultValue, nodeAdapter);
    }

    @Override
    protected Object extractValue(JcrProperty property)
    {
        Calendar calendar = property.getDate();
        return calendar != null ? calendar.getTime() : null;
    }

    @Override
    protected void setProperty(JcrNode node, String propertyName, Object value)
    {
        Date date = (Date)value;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        node.setProperty(propertyName, calendar);
    }

}
