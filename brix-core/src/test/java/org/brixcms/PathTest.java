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

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PathTest {
    @Test
    public void append() {
        assertEquals(new Path("/").append(new Path("foo")), new Path("/foo"));
        assertEquals(new Path("/").append(new Path("foo/bar")), new Path("/foo/bar"));
        assertEquals(new Path("/foo").append(new Path("bar")), new Path("/foo/bar"));
        assertEquals(new Path("/foo").append(new Path("../bar")), new Path("/bar"));
        assertEquals(new Path("/foo").append(new Path("../../bar")), new Path("/bar"));
        assertEquals(new Path("foo").append(new Path("bar")), new Path("foo/bar"));
        assertEquals(new Path("foo").append(new Path("../bar")), new Path("bar"));
        assertEquals(new Path("foo").append(new Path("../../bar")), new Path("../bar"));

        try {
            new Path("/foo").append(new Path("/"));
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void cannonical() {
        assertEquals(new Path("/a/b/c"), new Path("/a/b/c"));
        assertEquals(new Path("/a/b/./c"), new Path("/a/b/c"));
        assertEquals(new Path("."), new Path("."));
        assertEquals(new Path("./././."), new Path("."));
        assertEquals(new Path("././../."), new Path(".."));
        assertEquals(new Path("././../.."), new Path("../.."));
        assertEquals(new Path("./abc/../.."), new Path(".."));
        assertEquals(new Path("abc/../.."), new Path(".."));
        assertEquals(new Path("/abc/../.."), new Path("/"));
        assertEquals(new Path("abc/def/../fgh/.."), new Path("abc"));
        assertEquals(new Path("abc/def/../fgh/"), new Path("abc/fgh"));
        assertEquals(new Path("abc/def/../fgh/../../../../"), new Path("../.."));
        assertEquals(new Path("/abc/def/../fgh/../../../../"), new Path("/"));
        assertEquals(new Path("/abc/def/../fgh/../../../../xyz"), new Path("/xyz"));
        assertEquals(new Path("a//b"), new Path("a/b"));
        assertEquals(new Path("//a/b"), new Path("/a/b"));
        assertEquals(new Path("a/b//"), new Path("a/b"));
    }

    @Test
    public void construction() {
        assertEquals(new Path("/").toString(), "/");
        assertEquals(new Path("/foo").toString(), "/foo");
        assertEquals(new Path("/foo/").toString(), "/foo");
        assertEquals(new Path("/foo/bar").toString(), "/foo/bar");
        assertEquals(new Path("/foo/bar/").toString(), "/foo/bar");

        assertEquals(new Path("foo").toString(), "foo");
        assertEquals(new Path("foo/").toString(), "foo");
        assertEquals(new Path("foo/bar").toString(), "foo/bar");
        assertEquals(new Path("foo/bar/").toString(), "foo/bar");

        try {
            new Path("");
            fail();
        } catch (IllegalArgumentException e) {
            // noop
        }

        try {
            new Path(null);
            fail();
        } catch (IllegalArgumentException e) {
            // noop
        }
    }

    @Test
    public void getName() {
        assertEquals(new Path("/").getName(), "/");
        assertEquals(new Path("/foo").getName(), "foo");
        assertEquals(new Path("/foo/bar").getName(), "bar");
        assertEquals(new Path("foo").getName(), "foo");
        assertEquals(new Path("foo/bar").getName(), "bar");
    }

    @Test
    public void isAbsolute() {
        assertTrue(new Path("/").isAbsolute());
        assertTrue(new Path("/foo").isAbsolute());
        assertTrue(new Path("/foo/bar").isAbsolute());

        assertTrue(!new Path("foo").isAbsolute());
        assertTrue(!new Path("foo/bar").isAbsolute());
    }

    @Test
    public void isCannonical() {
        assertTrue(new Path("/", false).isCanonical());
        assertTrue(new Path("a", false).isCanonical());
        assertTrue(new Path("a/b", false).isCanonical());
        assertTrue(new Path("a/b/c", false).isCanonical());
        assertTrue(new Path("a/..b/c", false).isCanonical());
        assertTrue(new Path("a/b/c..", false).isCanonical());
        assertTrue(new Path("..", false).isCanonical());
        assertTrue(new Path("../", false).isCanonical());
        assertTrue(new Path("../..", false).isCanonical());
        assertTrue(new Path("../../", false).isCanonical());
        assertTrue(new Path("../a", false).isCanonical());
        assertTrue(new Path(".", false).isCanonical());

        assertTrue(!new Path("./.", false).isCanonical());
        assertTrue(!new Path("a//b", false).isCanonical());
        assertTrue(!new Path("a/b/c//", false).isCanonical());
        assertTrue(!new Path("/.", false).isCanonical());
        assertTrue(!new Path("./a", false).isCanonical());
        assertTrue(!new Path("a/.", false).isCanonical());
        assertTrue(!new Path("a/./b", false).isCanonical());
        assertTrue(!new Path("./a/b/c", false).isCanonical());
        assertTrue(!new Path("a/..b/./c", false).isCanonical());
        assertTrue(!new Path("a/b/c../.", false).isCanonical());
        assertTrue(!new Path("./..", false).isCanonical());
        assertTrue(!new Path(".././", false).isCanonical());
        assertTrue(!new Path(".././..", false).isCanonical());
        assertTrue(!new Path("../.././", false).isCanonical());
        assertTrue(!new Path("./../a", false).isCanonical());

        assertTrue(!new Path("/..", false).isCanonical());
        assertTrue(!new Path("/../..", false).isCanonical());
        assertTrue(!new Path("a/..", false).isCanonical());
        assertTrue(!new Path("a/../", false).isCanonical());

        assertTrue(!new Path("../a/..", false).isCanonical());
        assertTrue(!new Path("../../a/..", false).isCanonical());
        assertTrue(!new Path("../../../a/..", false).isCanonical());
    }

    @Test
    public void isChildOf() {
        assertChild(new Path("/"), new Path("/foo"));
        assertNotChild(new Path("/"), new Path("/foo/bar"));

        assertChild(new Path("/foo/bar"), new Path("/foo/bar/baz"));
        assertNotChild(new Path("/foo/bar"), new Path("/foo/baz/bar"));

        assertNotChild(new Path("/"), new Path("/"));
        assertNotChild(new Path("/foo"), new Path("/"));
    }

    private void assertChild(Path parent, Path child) {
        assertTrue(parent.isParentOf(child));
        assertTrue(child.isChildOf(parent));
    }

    private void assertNotChild(Path parent, Path child) {
        assertTrue(!parent.isParentOf(child));
        assertTrue(!child.isChildOf(parent));
    }

    @Test
    public void isDescendantOf() {
        assertDescendant(new Path("/"), new Path("/foo"));
        assertDescendant(new Path("/"), new Path("/foo/bar"));
        assertDescendant(new Path("/foo"), new Path("/foo/bar"));

        assertNotDescendant(new Path("/foo"), new Path("/bar/foo"));
        assertNotDescendant(new Path("/foo"), new Path("/"));

        assertNotDescendant(new Path("/"), new Path("relative"));

        assertDescendant(new Path("foo/bar"), new Path("foo/bar/baz"));
        assertNotDescendant(new Path("foo/bar"), new Path("bar"));

        assertNotDescendant(new Path("/"), new Path("/"));
        assertNotDescendant(new Path("/foo"), new Path("/foo"));
    }

    private void assertDescendant(Path parent, Path child) {
        assertTrue(child.isDescendantOf(parent));
        assertTrue(parent.isAncestorOf(child));
    }

    private void assertNotDescendant(Path parent, Path child) {
        assertTrue(!child.isDescendantOf(parent));
        assertTrue(!parent.isAncestorOf(child));
    }

    @Test
    public void isRoot() {
        assertTrue(new Path("/").isRoot());
        assertTrue(!new Path("foo").isRoot());
        assertTrue(!new Path("/foo").isRoot());
    }

    @Test
    @SuppressWarnings("unused")
    public void iterator() {
        Path path = new Path("/foo/bar/baz");
        Set<String> parts = new HashSet<String>(Arrays.asList(new String[]{"foo", "bar", "baz"}));
        for (String part : path) {
            assertTrue(parts.remove(part));
        }
        assertTrue(parts.isEmpty());

        for (String part : new Path("/")) {
            fail();
        }
    }

    @Test
    public void parent() {
        assertTrue(new Path("/").parent() == null);
        assertEquals(new Path("/foo").parent(), new Path("/"));
        assertEquals(new Path("/foo/bar").parent(), new Path("/foo"));
        assertEquals(new Path("foo").parent(), new Path("."));
        assertEquals(new Path("foo/bar").parent(), new Path("foo"));
        assertEquals(new Path(".").parent(), new Path(".."));
        assertEquals(new Path("../foo").parent(), new Path(".."));
        assertEquals(new Path("..").parent(), new Path("../.."));
    }

    @Test
    public void part() {
        Path path = new Path("/foo/bar/baz");
        assertEquals("foo", path.part(0));
        assertEquals("bar", path.part(1));
        assertEquals("baz", path.part(2));

        path = new Path("foo/bar/baz");
        assertEquals("foo", path.part(0));
        assertEquals("bar", path.part(1));
        assertEquals("baz", path.part(2));
    }

    @Test
    public void size() {
        assertEquals(new Path("/").size(), 0);
        assertEquals(new Path("/foo").size(), 1);
        assertEquals(new Path("/foo/bar").size(), 2);
        assertEquals(new Path("/foo/bar/baz").size(), 3);
    }

    @Test
    public void subpath() {
        // test absolute path

        Path path = new Path("/foo/bar/baz");
        assertEquals(new Path("/foo/bar/baz"), path.subpath(4));
        assertEquals(new Path("/foo/bar"), path.subpath(3));
        assertEquals(new Path("/foo"), path.subpath(2));
        assertEquals(new Path("/"), path.subpath(1));
        try {
            path.subpath(5);
            fail("expect index out of bounds exception");
        } catch (IndexOutOfBoundsException e) {// noop
        }
        try {
            path.subpath(0);
            fail("expect index out of bounds exception");
        } catch (IndexOutOfBoundsException e) {// noop
        }
        try {
            path.subpath(-1);
            fail("expect index out of bounds exception");
        } catch (IndexOutOfBoundsException e) {// noop
        }

        // test relative path

        path = new Path("foo/bar/baz");
        assertEquals(new Path("foo/bar/baz"), path.subpath(3));
        assertEquals(new Path("foo/bar"), path.subpath(2));
        assertEquals(new Path("foo"), path.subpath(1));
        try {
            path.subpath(4);
            fail("expect index out of bounds exception");
        } catch (IndexOutOfBoundsException e) {// noop
        }
        try {
            path.subpath(0);
            fail("expect index out of bounds exception");
        } catch (IndexOutOfBoundsException e) {// noop
        }
        try {
            path.subpath(-1);
            fail("expect index out of bounds exception");
        } catch (IndexOutOfBoundsException e) {// noop
        }
    }

    @Test
    public void toRelative() {
        try {
            // test argument must be ancestor
            new Path("/").toRelative(new Path("/"));
            fail();
        } catch (IllegalStateException e) {
            // noop
        }

        // test arg must be ancestor
        try {
            // path is absolute but arg is relative
            new Path("/foo/bar").toRelative(new Path("/bar"));
            fail();
        } catch (IllegalArgumentException e) {
            // noop
        }

        assertEquals(new Path("foo"), new Path("/foo").toRelative(new Path("/")));

        assertEquals(new Path("bar"), new Path("/foo/bar").toRelative(new Path("/foo")));

        assertEquals(new Path("bar"), new Path("foo/bar").toRelative(new Path("foo")));
    }
}
