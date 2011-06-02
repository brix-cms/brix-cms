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

import org.brixcms.Path;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PathSetTest {
    private PathSet data;

    @Test
    public void containsAncestor() {
        assertTrue(data.remove(new Path("/")));

        assertTrue(data.containsAncestor(new Path("/foo/baz")));
        assertTrue(data.containsAncestor(new Path("/bar/baz")));
        assertTrue(!data.containsAncestor(new Path("/foo")));
        assertTrue(!data.containsAncestor(new Path("/baz")));
    }

    @Test
    public void containsParent() {
        assertTrue(!data.containsParent(new Path("/")));
        assertTrue(data.containsParent(new Path("/foo")));
        assertTrue(data.containsParent(new Path("/foo/baz")));
        assertTrue(data.containsParent(new Path("/foo/bar/baz")));
        assertTrue(data.containsParent(new Path("/foo/bar/baz/boz")));
        assertTrue(!data.containsParent(new Path("/foo/baz/bar")));
    }

    @Before
    public void initData() {
        data = new PathSet();
        data.add(new Path("/"));
        data.add(new Path("/bar"));
        data.add(new Path("/foo"));
        data.add(new Path("/foo/bar"));
        data.add(new Path("/foo/bar/baz"));
    }

    @Test
    public void removeDescendants() {
        data.removeDescendants(new Path("/foo"));
        assertTrue(data.size() == 3);
        assertTrue(data.contains(new Path("/")));
        assertTrue(data.contains(new Path("/bar")));
        assertTrue(data.contains(new Path("/foo")));

        data.removeDescendants(new Path("/"));
        assertTrue(data.size() == 1);
        assertTrue(data.contains(new Path("/")));
    }

    @Test
    public void removeWithDescendants() {
        data.removeWithDescendants(new Path("/foo"));
        assertTrue(data.size() == 2);
        assertTrue(data.contains(new Path("/")));
        assertTrue(data.contains(new Path("/bar")));

        data.removeWithDescendants(new Path("/"));
        assertTrue(data.isEmpty());
    }
}
