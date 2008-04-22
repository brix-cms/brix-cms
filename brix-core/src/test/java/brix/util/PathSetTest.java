package brix.util;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import brix.Path;
import brix.util.PathSet;

public class PathSetTest
{

    private PathSet data;

    @Before
    public void initData()
    {
        data = new PathSet();
        data.add(new Path("/"));
        data.add(new Path("/bar"));
        data.add(new Path("/foo"));
        data.add(new Path("/foo/bar"));
        data.add(new Path("/foo/bar/baz"));
    }

    @Test
    public void removeDescendants()
    {
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
    public void removeWithDescendants()
    {
        data.removeWithDescendants(new Path("/foo"));
        assertTrue(data.size() == 2);
        assertTrue(data.contains(new Path("/")));
        assertTrue(data.contains(new Path("/bar")));

        data.removeWithDescendants(new Path("/"));
        assertTrue(data.isEmpty());
    }

    @Test
    public void containsAncestor()
    {
        assertTrue(data.remove(new Path("/")));

        assertTrue(data.containsAncestor(new Path("/foo/baz")));
        assertTrue(data.containsAncestor(new Path("/bar/baz")));
        assertTrue(!data.containsAncestor(new Path("/foo")));
        assertTrue(!data.containsAncestor(new Path("/baz")));
    }

    @Test
    public void containsParent()
    {
        assertTrue(!data.containsParent(new Path("/")));
        assertTrue(data.containsParent(new Path("/foo")));
        assertTrue(data.containsParent(new Path("/foo/baz")));
        assertTrue(data.containsParent(new Path("/foo/bar/baz")));
        assertTrue(data.containsParent(new Path("/foo/bar/baz/boz")));
        assertTrue(!data.containsParent(new Path("/foo/baz/bar")));
    }

}
