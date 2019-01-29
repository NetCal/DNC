/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2006 Frank A. Zdarsky
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A tiny collection of convenience methods useful in dealing with sets but not
 * provided directly by Java's set classes.
 */
public final class SetUtils {
    /**
     * Returns the set difference between the set <code>s1</code> and the set
     * <code>s2</code>.
     *
     * @param <T> Type of the sets' entries.
     * @param s1  A set.
     * @param s2  Another set.
     * @return The difference set.
     */
    public static <T> Set<T> getDifference(Set<T> s1, Set<T> s2) {
        Set<T> result = new HashSet<T>(s1);
        result.removeAll(s2);
        return result;
    }

    /**
     * Returns the intersection of set <code>s1</code> and set <code>s2</code>.
     * Returns an empty set if the intersection is empty, i.e., does not return
     * null.
     *
     * @param <T> Type of the sets' entries.
     * @param s1  A set.
     * @param s2  Another set.
     * @return The intersection set.
     */
    public static <T> Set<T> getIntersection(Set<T> s1, Set<T> s2) {
        if (s1 == null || s2 == null)
            return new HashSet<T>();

        Set<T> result = new HashSet<T>(s1);
        result.retainAll(s2);
        return result;
    }

    /**
     * Returns the intersection of all sets contained in the list <code>sets</code>.
     *
     * @param <T>  Type of the sets' entries.
     * @param sets A list of sets
     * @return The intersection of all sets
     */
    public static <T> Set<T> getIntersection(List<Set<T>> sets) {
        Set<T> result = new HashSet<T>();
        Iterator<Set<T>> iter = sets.iterator();
        if (iter.hasNext()) {
            result.addAll(iter.next());
            for (; iter.hasNext(); ) {
                result.retainAll((Set<T>) iter.next());
            }
        }
        return result;
    }

    /**
     * Returns the union of set <code>s1</code> and set <code>s2</code>.
     *
     * @param <T> Type of the sets' entries.
     * @param s1  A set.
     * @param s2  Another set.
     * @return The union set.
     */
    public static <T> Set<T> getUnion(Set<T> s1, Set<T> s2) {
        Set<T> result = new HashSet<T>(s1);
        result.addAll(s2);
        return result;
    }

    /**
     * Returns the union of all sets contained in the list <code>sets</code>.
     *
     * @param <T>  Type of the sets' entries.
     * @param sets A list of sets.
     * @return The union of all sets.
     */
    public static <T> Set<T> getUnion(List<Set<T>> sets) {
        Set<T> result = new HashSet<T>();
        for (Set<T> set : sets) {
            result.addAll(set);
        }
        return result;
    }
}
