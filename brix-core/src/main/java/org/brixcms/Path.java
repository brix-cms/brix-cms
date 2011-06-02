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

package org.brixcms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * The {@link Path} class represents a path that is a string of segments joined together by a <code>/</code> separator.
 * The {@link Path} class provides various operations that can be performed on such a string of segments. Path is
 * immutable.
 *
 * @author igor.vaynberg
 */
public final class Path implements Iterable<String>, Serializable {
    private static final String SEPARATOR = "/";
    public static final Path ROOT = new Path(SEPARATOR);

    public static final Comparator<Path> SMALLEST_FIRST_COMPARATOR = new Comparator<Path>() {
        public int compare(Path o1, Path o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            } else {
                int s1 = o1.size();
                int s2 = o2.size();
                if (s1 == s2) {
                    return o1.path.compareTo(o2.path);
                } else {
                    return o1.size() - o2.size();
                }
            }
        }
    };

    public static final Comparator<Path> LARGEST_FIRST_COMPARATOR = new Comparator<Path>() {
        public int compare(Path o1, Path o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            } else {
                int s1 = o1.size();
                int s2 = o2.size();
                if (s1 == s2) {
                    return o1.path.compareTo(o2.path);
                } else {
                    return o1.size() - o2.size();
                }
            }
        }

    };
    private final String path;

    public Path(String path) {
        this(path, true);
    }

    public Path(String path, boolean canonize) {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("Argument 'path' cannot be null or empty");
        }

        if (canonize) {
            path = new Path(path, false).canonical().path;
        }

        if (!path.equals("/") && path.endsWith(SEPARATOR)) {
            this.path = path.substring(0, path.length() - 1);
        } else {
            this.path = path;
        }
    }

    public Path canonical() {
        if (isCanonical()) {
            return this;
        } else {
            return doCanonical();
        }
    }

    public boolean isCanonical() {
        int offset = 0;

        // whether a text segment (not "..") has been found
        boolean text = false;

        // until empty paths are supported
        if (path.equals(".")) {
            return true;
        }

        if (!isRoot() && path.endsWith("/")) {
            return false;
        }

        while (offset < path.length()) {
            String sub = path.substring(offset);

            if (sub.equals("/") || sub.equals("")) {
                break;
            }

            if (sub.startsWith("./") || sub.equals(".")) {
                return false;
            }

            boolean up = sub.startsWith("../") || sub.equals("..");

            if (text && up) {
                return false;
            }

            if (up == false) {
                text = true;
            }

            int next = sub.indexOf("/");
            if (next == -1) {
                break;
            } else if (next == 0 && offset != 0) {
                // situation when there are // in the middle of string
                return false;
            } else {
                offset += next + 1;
            }
        }

        return true;
    }

    public boolean isRoot() {
        return path.equals(SEPARATOR);
    }

    private Path doCanonical() {
        boolean absolute = isAbsolute();
        int prepend = 0;

        List<String> segments = new ArrayList<String>(10);

        for (String segment : this) {
            if (segment.equals("") || segment.equals(".")) {
            } else if (segment.equals("..")) {
                if (segments.size() > 0) {
                    segments.remove(segments.size() - 1);
                } else {
                    ++prepend;
                }
            } else {
                segments.add(segment);
            }
        }

        StringBuilder res = new StringBuilder(path.length());

        while (absolute == false && prepend > 0) {
            res.append("../");
            --prepend;
        }

        if (absolute == true) {
            res.append("/");
        }

        for (int i = 0; i < segments.size(); ++i) {
            res.append(segments.get(i));
            if (i != segments.size() - 1) {
                res.append("/");
            }
        }

        if (res.length() == 0) {
            return new Path(".");
        } else {
            return new Path(res.toString());
        }
    }

    public boolean isAbsolute() {
        return path.startsWith("/");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Path other = (Path) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else {
            return path.equals(other.path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return path;
    }


    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int idx = 0;

            public boolean hasNext() {
                return idx < size();
            }

            public String next() {
                return part(idx++);
            }

            public void remove() {
                throw new UnsupportedOperationException("Path parts iterator is read only");
            }
        };
    }

    public String getName() {
        if (path.equals(SEPARATOR)) {
            return path;
        } else {
            int last = path.lastIndexOf(SEPARATOR);
            return path.substring(last + 1);
        }
    }

    public boolean isAncestorOf(Path other) {
        return other.isDescendantOf(this);
    }

    public boolean isDescendantOf(Path other) {
        if (other.isRoot()) {
            if (isRoot()) {
                return false;
            } else if (isAbsolute()) {
                return true;
            } else {
                return false;
            }
        } else {
            return path.startsWith(other.path + SEPARATOR) && path.length() > other.path.length();
        }
    }

    public boolean isParentOf(Path other) {
        return other.isChildOf(this);
    }

    public boolean isChildOf(Path other) {
        return isDescendantOf(other) && size() == other.size() + 1;
    }

    public int size() {
        if (isRoot()) {
            return 0;
        }

        int size = 0;
        int pos = 0;
        while (pos >= 0) {
            size++;
            pos = path.indexOf(SEPARATOR, pos + 1);
        }
        return size;
    }

    public Path parent() {
        if (isRoot()) {
            return null;
        } else {
            return new Path(path + "/..");
        }
    }

    public String part(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }

        int start = isAbsolute() ? 1 : 0;

        for (int i = 0; i < index; i++) {
            start = path.indexOf(SEPARATOR, start);
            if (start < 0) {
                throw new IndexOutOfBoundsException();
            }
            start++;
        }

        int end = path.indexOf(SEPARATOR, start);
        if (end < 0) {
            end = path.length();
        }

        return path.substring(start, end);
    }

    public Path subpath(int idx) {
        if (idx <= 0) {
            throw new IndexOutOfBoundsException();
        }

        final int size = size();
        final boolean abs = isAbsolute();
        int chunks = (abs) ? size + 1 : size;

        if (idx > chunks) {
            throw new IndexOutOfBoundsException();
        }

        if (idx == chunks) {
            // shortcut in case we want the entire path
            return this;
        }

        int iterations = ((abs) ? idx - 1 : idx);
        int end = 1;

        for (int i = 0; i < iterations; i++) {
            end = path.indexOf(SEPARATOR, end + 1);
        }

        if (end == 0) {
            return new Path("/");
        }

        String newPath = path.substring(0, end);

        return new Path(newPath);
    }

    public Path toAbsolute() {
        if (!isAbsolute()) {
            return ROOT.append(this);
        } else {
            return this;
        }
    }

    public Path append(Path relative) {
        if (relative == null) {
            throw new IllegalArgumentException("Argument 'relative' cannot be null");
        }
        if (relative.isAbsolute()) {
            throw new IllegalArgumentException("Cannot append an absolute path");
        }

        StringBuilder appended = new StringBuilder(path.length() + 1 + relative.path.length());
        appended.append(path);
        if (!path.endsWith(SEPARATOR)) {
            appended.append("/");
        }
        appended.append(relative.path);
        return new Path(appended.toString());
    }

    public Path toRelative(Path ancestor) {
        if (isRoot()) {
            throw new IllegalStateException("Cannot make root path relative");
        }
        if (!isDescendantOf(ancestor)) {
            throw new IllegalArgumentException("Cannot create relative path because this path: " +
                    this + " is not descendant of ancestor argument: " + ancestor);
        }

        Path fragment = new Path(path.substring(ancestor.path.length()), false);
        if (fragment.isAbsolute()) {
            fragment = fragment.toRelative(new Path("/"));
        }
        return fragment;
    }
}
