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

package org.brixcms.plugin.site.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Responds stream with support for Content-Range header.
 *
 * @author Matej Knopp
 */
class Streamer {
    private final long length;
    private final InputStream inputStream;
    private final String fileName;
    private final boolean attachment;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public Streamer(long length, InputStream inputStream, String fileName, boolean attachment,
                    HttpServletRequest request, HttpServletResponse response) {
        this.length = length;
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.response = response;
        this.request = request;
        this.attachment = attachment;
    }

    public void stream() {
        Range range = parseRange(request.getHeader("Range"), length);
        Long first = range.start;
        Long last = range.end;
        long contentLength = length;

        if (first != null && last != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.addHeader("Content-Range", "bytes " + first + "-" + last + "/" + length);

            contentLength = last - first + 1;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            first = 0l;
            last = length - 1;
        }

        response.addHeader("Content-Length", "" + contentLength);

        if (!attachment) {
            response.addHeader("Content-Disposition", "inline; filename=\"" + fileName + "\";");
        } else {
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        }
        response.addHeader("Accept-Range", "bytes");
        response.addHeader("Connection", "close");

        InputStream s = null;

        try {
            s = new BufferedInputStream(inputStream);

            s.skip(first);

            final int bufferSize = 1024 * 10;
            long left = contentLength;
            while (left > 0) {
                int howMuch = bufferSize;
                if (howMuch > left) {
                    howMuch = (int) left;
                }

                byte[] buf = new byte[howMuch];
                int numRead = s.read(buf);

                response.getOutputStream().write(buf, 0, numRead);
                response.flushBuffer();

                if (numRead == -1) {
                    break;
                }

                left -= numRead;
            }
        } catch (Exception e) {
            if (e.getClass().getName().contains("Eof")) {
                // ignore
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private Range parseRange(String range, long length) {
        if (isEmpty(range)) {
            return new Range(null, null);
        }
        String p[] = range.split("=");

        if (p.length != 1 && (p.length != 2 || !"bytes".equals(p[0]))) {
            return new Range(0l, length - 1);
        } else {
            p = p[p.length - 1].split("-");
            if (p.length == 1) {
                p = new String[]{p[0], ""};
            }
            if (p.length != 2) {
                return new Range(0l, length - 1);
            }
            if (isEmpty(p[0]) && isEmpty(p[1])) {
                return new Range(0l, length - 1);
            } else if (isEmpty(p[0])) {
                return new Range(length - Long.valueOf(p[1]), length - 1);
            } else if (isEmpty(p[1])) {
                return new Range(Long.valueOf(p[0]), length - 1);
            } else {
                return new Range(Long.valueOf(p[0]), Long.valueOf(p[1]));
            }
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    private static class Range {
        final Long start;
        final Long end;

        public Range(Long start, Long end) {
            this.start = start;
            this.end = end;
        }
    }
}
