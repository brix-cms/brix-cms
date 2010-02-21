package brix.plugin.site.resource.managers.text;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.jcr.wrapper.BrixFileNode;

public class TextMimeTypeValidator implements IValidator<String>
{

	public void validate(IValidatable<String> validatable)
	{
		final String value = validatable.getValue();
		if (!BrixFileNode.isText(value))
		{
			ValidationError error = new ValidationError();
			error.setMessage("Only text mime types are allowed (text/* or application/xml)");
			error.addMessageKey(getClass().getSimpleName());
			validatable.error(error);
		}

	}

}
