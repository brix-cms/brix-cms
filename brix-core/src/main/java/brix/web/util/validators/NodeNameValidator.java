package brix.web.util.validators;

import java.util.Arrays;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

public class NodeNameValidator implements IValidator
{

    public static final char[] forbidden = new char[] { '\\', '/', ':', '?', '<', '>' };

    public static boolean isForbidden(char c)
    {
        for (int i = 0; i < forbidden.length; ++i)
        {
            if (c == forbidden[i])
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void validate(IValidatable validatable)
    {

        String s = validatable.getValue().toString();
        for (int i = 0; i < s.length(); ++i)
        {
            char c = s.charAt(i);
            if (isForbidden(c))
            {
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

    private static final NodeNameValidator INSTANCE = new NodeNameValidator();

    public static final NodeNameValidator getInstance()
    {
        return INSTANCE;
    }

}
