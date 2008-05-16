package brix.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class IntersectTest
{
    /**
     * Advanced intersection algorithm as described in
     * www.dcc.uchile.cl/~asalinge/ps/paper-spire.pdf .
     * 
     * @author Matej Knopp
     * 
     * @param <T>
     */
    static class Intersection<T extends Comparable<T>>
    {
        List<T> l1;
        List<T> l2;
        List<T> result;

        public Intersection(List<T> l1, List<T> l2, List<T> result)
        {
            this.l1 = l1;
            this.l2 = l2;
            this.result = result;
        }

        private boolean anyIntersection()
        {
            if (l1.isEmpty() || l2.isEmpty())
            {
                return false;
            }
            T min1 = l1.get(0);
            T max2 = l2.get(l2.size() - 1);
            if (max2.compareTo(min1) < 0)
            {
                return false;
            }
            T max1 = l1.get(l1.size() - 1);
            T min2 = l2.get(0);
            if (max1.compareTo(min2) < 0)
            {
                return false;
            }
            return true;
        }

        private void ensureL1Smaller()
        {
            if (l1.size() > l2.size())
            {
                List<T> tmp = l1;
                l1 = l2;
                l2 = tmp;
            }
        }

        private boolean shrinkLists()
        {
            T min1 = l1.get(0);
            T min2 = l2.get(0);

            if (min1.compareTo(min2) > 0)
            {
                int pos = Collections.binarySearch(l2, min1);
                if (pos == -l2.size() - 1)
                {
                    return true;
                }
                if (pos < 0)
                {
                    pos = -pos - 1;
                }
                l2 = l2.subList(pos, l2.size());
            }
            else if (min2.compareTo(min1) > 0)
            {
                int pos = Collections.binarySearch(l1, min2);
                if (pos == -l1.size() - 1)
                {
                    return true;
                }
                if (pos < 0)
                {
                    pos = -pos - 1;
                }
                l1 = l1.subList(pos, l1.size());
            }

            T max1 = l1.get(l1.size() - 1);
            T max2 = l2.get(l2.size() - 1);

            if (max1.compareTo(max2) < 0)
            {
                int pos = Collections.binarySearch(l2, max1);
                if (pos == -1)
                {
                    return true;
                }
                if (pos < 0)
                {
                    pos = -pos - 1;
                }
                else
                {
                    pos = pos + 1;
                }
                l2 = l2.subList(0, pos);
            }
            else if (max2.compareTo(max1) < 0)
            {
                int pos = Collections.binarySearch(l1, max2);
                if (pos == -1)
                {
                    return true;
                }
                if (pos < 0)
                {
                    pos = -pos - 1;
                }
                else
                {
                    pos = pos + 1;
                }
                l1 = l1.subList(0, pos);
            }

            return false;
        }

        private boolean checkForOne()
        {
            if (l1.size() == 1)
            {
                T first = l1.get(0);
                if (Collections.binarySearch(l2, first) >= 0)
                {
                    result.add(first);
                }
                return true;
            }
            else
            {
                return false;
            }
        }

        private void intersect()
        {
            if (anyIntersection())
            {
                ensureL1Smaller();

                if (checkForOne())
                    return;

                if (shrinkLists())
                {
                    return;
                }

                ensureL1Smaller();

                if (checkForOne())
                    return;
                
                int median = l1.size() / 2 + (l1.size() % 2);
                T value = l1.get(median);

                int pos = Collections.binarySearch(l2, value);

                if (pos >= 0)
                {
                    result.add(value);
                    ++pos;
                }
                else
                {
                    pos = -pos - 1;
                }

                if (median > 0 && pos > 0)
                {
                    Intersection<T> i = new Intersection<T>(l1.subList(0, median), l2.subList(0,
                        pos), result);
                    i.intersect();
                }
                if (median < (l1.size() - 1) && pos < l2.size())
                {
                    Intersection<T> i = new Intersection<T>(l1.subList(median + 1, l1.size()), l2
                        .subList(pos, l2.size()), result);
                    i.intersect();
                }
            }
        }
    }
    
    static class Intersect2<T>
    {
        private final List<T> l1;
        private final List<T> l2;
        private final Set<T> s2;
        private final int maxIterationSize = 100;
        
        public Intersect2(List<T> l1, List<T> l2, Set<T> s2)
        {
            this.l1 = l1;
            this.l2 = l2;
            this.s2 = s2;
        }
        
        public List<T> intersect()
        {
            List<T> result = new ArrayList<T>();
            
            for (int i = 0; i < l1.size(); ++i)
            {
                T value = l1.get(i);
                if (s2.contains(value))
                {
                    result.add(value);
                }
            }
            
            return result;
        }
    }
    
    private static List<Integer> generateList(int count, int maxStep, int start)
    {
        List<Integer> res = new ArrayList<Integer>(count);
        int current = start;
        while (count > 0)
        {
            current += new Random().nextInt(maxStep - 1) + 1;
            res.add(current);
            --count;
        }
        return res;
    }
    
    private static List<Integer> intersect2(List<Integer> l1, List<Integer> l2)
    {
        List<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < l1.size(); ++i)
        {
            int value = l1.get(i);
            if (l2.contains(value))
                res.add(value);
        }
        return res;
    }
    
    private static List<Integer> intersect3(List<Integer> l1, Set<Integer> l2)
    {
        List<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < l1.size(); ++i)
        {
            int value = l1.get(i);
            if (l2.contains(value))
                res.add(value);
        }
        return res;
    }

    private static long took1 = 0;
    private static long took2 = 0;
    
    private static void testIntersect(int count, int maxStep)
    {
        List<Integer> l1 = generateList(count, maxStep, 0);
        List<Integer> l2 = generateList(count, maxStep, 0);
        
        List<Integer> res1 = new ArrayList<Integer>();
    
        long current = System.currentTimeMillis();
        new Intersection<Integer>(l1, l2, res1).intersect();
        took1 += (System.currentTimeMillis() - current);
        
        Collections.sort(res1);
        
        Set<Integer> s2 = new HashSet<Integer>(l2);
        current = System.currentTimeMillis();
        List<Integer> res2 = intersect3(l1, s2);
        took2 += (System.currentTimeMillis() - current);
        
        if (!res1.equals(res2))
        {
            System.out.println("Problem!");
            System.out.println(res1);
            System.out.println(res2);
        }
        
        System.out.println(res1.size());
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        for (int i = 0; i < 100; ++i)
        {
            testIntersect(100000, 20);
        }
        
        System.out.println("1: " + took1);
        System.out.println("2: " + took2);
        
    }

}
