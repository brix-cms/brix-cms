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

package org.brixcms.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringInputStream extends InputStream {
    private StringReader in;

    public StringInputStream(String source) {
        in = new StringReader(source);
    }


    public void close() throws IOException {
        in.close();
    }

    public synchronized void mark(final int limit) {
        try {
            in.mark(limit);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public int read() throws IOException {
        return in.read();
    }

    public synchronized void reset() throws IOException {
        in.reset();
    }
}
