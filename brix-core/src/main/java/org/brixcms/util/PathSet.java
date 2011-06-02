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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PathSet implements Set<Path> {
    private final Set<Path> delegate;

    public PathSet() {
        this(new HashSet<Path>());
    }

    public PathSet(Set<Path> delegate) {
        this.delegate = delegate;
    }

    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    public int hashCode() {
        return delegate.hashCode();
    }



    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public Object[] toArray() {
        return delegate.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    public boolean add(Path o) {
        return delegate.add(o);
    }

    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    public boolean addAll(Collection<? extends Path> c) {
        return delegate.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean containsAncestor(Path path) {
        for (Path p : this) {
            if (p.isAncestorOf(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsParent(Path path) {
        for (Path p : this) {
            if (p.isParentOf(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeWithDescendants(Path path) {
        boolean ret = remove(path);
        removeDescendants(path);
        return ret;
    }

    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    public void removeDescendants(Path path) {
        Iterator<Path> i = iterator();
        while (i.hasNext()) {
            Path p = i.next();
            if (p.isDescendantOf(path)) {
                i.remove();
            }
        }
    }

    public Iterator<Path> iterator() {
        return delegate.iterator();
    }
}
