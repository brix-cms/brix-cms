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

package brix.jcr.jackrabbit;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.htmlparser.Node;
import org.htmlparser.Text;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Html text extractor that will not choke on an undeclared namespace and log an ugly stacktrace
 * like the one that comes with jackrabbit.
 * 
 * TODO: skip text sections such as javascript and css style blocks
 */
public class HtmlTextExtractor extends AbstractTextExtractor
{

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(HtmlTextExtractor.class);

    /**
     * Constructor
     */
    public HtmlTextExtractor()
    {
        super(new String[] { "text/html" });
    }

    /** {@inheritDoc} */
    public Reader extractText(InputStream stream, String type, String encoding) throws IOException
    {
        try
        {
            StringBuilder textBuffer = new StringBuilder();

            // parse html using lexer
            Lexer lexer = new Lexer(new Page(stream, encoding));
            for (Node node = lexer.nextNode(); node != null; node = lexer.nextNode())
            {
                if (node instanceof Text)
                {
                    // text nodes are the ones containing text
                    final String text = ((Text)node).getText().trim();
                    if (text.length() > 0)
                    {
                        // if text has non-whitespace chars append them
                        textBuffer.append(((Text)node).getText()).append(" ");
                    }
                }
            }
            return new StringReader(textBuffer.toString());
        }
        catch (ParserException e)
        {
            logger.warn("Failed to extract HTML text content", e);
            return new StringReader("");
        }
        finally
        {
            stream.close();
        }
    }
}
