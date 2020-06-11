package com.uib.web.peptideshaker.model.core;

/*
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

/**
 * This is an updated version with enhancements made by Daniel Migowski, Andre
 * Bogus, and David Koelle
 *
 * To convert to use Templates (Java 1.5+): - Change implements Comparator to
 * implements Comparator - Change compare(Object o1, Object o2) to
 * compare(String s1, String s2) - Remove the type checking and casting in
 * compare().
 *
 * To use this class: Use the static sort method from the java.util.Collections
 * class: Collections.sort(your list, new AlphanumComparator());
 */
public class AlphanumComparator implements Comparable<AlphanumComparator> {

    /**
     * The value to be compared.
     */
    private final String value;

    /**
     * Constructor to initialise main attributes.
     *
     * @param value the value to be compared.
     */
    public AlphanumComparator(String value) {
        this.value = value;
    }

    /**
     * Check if the char is number.
     */
    private boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /**
     * Length of string is passed in for improved efficiency (only need to
     * calculate it once).
     */
    private String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        } else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    /**
     *
     * @param alphanumComparator2
     * @return integer value of the comparisons
     */
    @Override
    public int compareTo(AlphanumComparator alphanumComparator2) {
        if (!(value instanceof String) || !(alphanumComparator2.value instanceof String)) {
            return 0;
        }

        String s2 = (String) alphanumComparator2.value;

        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = value.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(value, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            // If both chunks contain numeric characters, sort them numerically
            int result;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                // Simple chunk comparison by length.
                int thisChunkLength = thisChunk.length();
                result = thisChunkLength - thatChunk.length();
                // If equal, the first different number counts
                if (result == 0) {
                    for (int i = 0; i < thisChunkLength; i++) {
                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
                        if (result != 0) {
                            return result;
                        }
                    }
                }
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0) {
                return result;
            }
        }

        return s1Length - s2Length;
    }

    /**
     *Convert to string value
     * @return The value to be compared
     */
    @Override
    public String toString() {
        return value;
    }
}
