package com.uib.web.peptideshaker.model.core;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * This class responsible for calculating matrix for DivaMatrixLayoutChartFilter
 *
 * @author Yehia Farag
 */
public class ModificationMatrix {

    /**
     * Rows data map.
     */
    private final Map<String, Integer> rows;
    /**
     * The full calculated matrix.
     */
    private final Map<String, Set<Comparable>> calculatedMatrix;
    /**
     * Set of keys to sort.
     */
    private final Set<String> keySorter;

    private boolean useVenn = true;

    /**
     * Constructor to initialise the main data structure and the matrix
     *
     * @param data map of data rows as keys and columns as set
     */
    public ModificationMatrix(Map<String, Set<Comparable>> data) {
        rows = new LinkedHashMap<>();
        keySorter = new TreeSet<>();
        calculatedMatrix = calculateMatrix(data);
    }

    /**
     * Get the calculated matrix rows
     *
     * @return map of rows title to number
     */
    public Map<String, Integer> getRows() {
        return rows;
    }

    /**
     * Get the calculated matrix columns
     *
     * @return map of columns
     */
    public Map<String, Set<Comparable>> getCalculatedColumns() {
        return calculatedMatrix;
    }

    /**
     * calculate the matrix rows and columns
     *
     * @param data map of data rows as keys and columns as set
     * @return calculated matrix
     */
    private Map<String, Set<Comparable>> calculateMatrix(Map<String, Set<Comparable>> data) {
        //calculate matrix
        Map<String, Set<Comparable>> matrixData = new LinkedHashMap<>();
        TreeMap<AlphanumComparator, String> sortingMap = new TreeMap<>(Collections.reverseOrder());
        data.keySet().forEach((key) -> {
            AlphanumComparator sortingKey = new AlphanumComparator(data.get(key).size() + "_" + key);
            sortingMap.put(sortingKey, key);
        });
        Map<String, Set<Comparable>> sortedData = new LinkedHashMap<>();
        sortingMap.values().stream().map((key) -> {
            int size = data.get(key).size();
            this.rows.put(key, size);
            return key;
        }).forEachOrdered((key) -> {
            sortedData.put(key, data.get(key));
        });
        Map<String, Set<Comparable>> rowsII = new LinkedHashMap<>(sortedData);
        Map<String, Set<Comparable>> tempColumns = new LinkedHashMap<>();
        tempColumns.putAll(sortedData);
        Map<String, Set<Comparable>> trows = new LinkedHashMap<>(sortedData);
        sortedData.keySet().stream().map((keyI) -> {
            rowsII.keySet().stream().filter((keyII) -> !(keyI.equals(keyII) || keyII.contains(keyI))).forEachOrdered((keyII) -> {
                String key = (keyII + "," + keyI).replace("[", "").replace("]", "");//.replace(" ", "");
                keySorter.addAll(Arrays.asList(key.split(",")));
                key = keySorter.toString();
                keySorter.clear();
                if (trows.containsKey(key)) {
                    Set<Comparable> union = new LinkedHashSet<>();
                    union.addAll(com.google.common.collect.Sets.union(trows.get(key), com.google.common.collect.Sets.intersection(rowsII.get(keyII), sortedData.get(keyI))));
                    trows.put(key, union);
                } else {
                    Set<Comparable> intersection = new LinkedHashSet<>();
                    intersection.addAll(com.google.common.collect.Sets.intersection(rowsII.get(keyII), sortedData.get(keyI)));
                    trows.put(key, intersection);
                    Set<Comparable> tempSetI = new LinkedHashSet<>();
                    tempSetI.addAll(rowsII.get(keyII));
                    tempSetI.removeAll(intersection);
                    rowsII.replace(keyII, tempSetI);
                    Set<Comparable> tempSetII = new LinkedHashSet<>();
                    tempSetII.addAll(sortedData.get(keyI));
                    tempSetII.removeAll(intersection);
                    sortedData.replace(keyI, tempSetII);
                }
            });
            return keyI;
        }).map((_item) -> {
            rowsII.clear();
            return _item;
        }).map((_item) -> {
            rowsII.putAll(trows);
            return _item;
        }).forEachOrdered((_item) -> {
            tempColumns.putAll(trows);
        });
        Map<AlphanumComparator, String> sortingMap2 = new TreeMap<>(Collections.reverseOrder());
        tempColumns.keySet().forEach((key) -> {
            AlphanumComparator sortingKey = new AlphanumComparator(tempColumns.get(key).size() + "_" + key);
            sortingMap2.put(sortingKey, key);
        });
        List<String> sortingKeysList = new ArrayList<>(rows.keySet());
        Map<Integer, String> sortingKysMap = new TreeMap<>();
        sortingMap2.values().forEach((key) -> {
            String[] arr = key.split(",");
            String updatedKey = key;
            if (arr.length > 1) {
                sortingKysMap.clear();
                for (String sub : arr) {
                    sub = sub.replace("]", "").replace("[", "").trim();
                    sortingKysMap.put(sortingKeysList.indexOf(sub), sub);
                }
                updatedKey = sortingKysMap.values().toString();
            }
            if (!tempColumns.get(key).isEmpty()) {
                matrixData.put(updatedKey, tempColumns.get(key));
            }
        });
        matrixData.keySet().forEach((key1) -> {
            matrixData.keySet().forEach((key2) -> {
                HashSet<Comparable> intersction = new HashSet<>();
                intersction.addAll(Sets.intersection(matrixData.get(key2), matrixData.get(key1)));
                if (!intersction.isEmpty() && !key2.equalsIgnoreCase(key1)) {
                    if (key1.split(",").length > key2.split(",").length) {
                        matrixData.get(key2).removeAll(intersction);
                    } else if (key1.split(",").length < key2.split(",").length) {
                        matrixData.get(key1).removeAll(intersction);
                    } else {
                        matrixData.get(key1).removeAll(intersction);
                        matrixData.get(key2).removeAll(intersction);
                    }
                }
            });
        });

        Map<String, Set<Comparable>> tempMatrixData = new LinkedHashMap<>(matrixData);
        tempMatrixData.keySet().stream().filter((key1) -> (matrixData.get(key1).isEmpty())).forEachOrdered((key1) -> {
            matrixData.remove(key1);
        });

        TreeMap<AlphanumComparator, String> sortingKeyColumnsMap = new TreeMap<>(Collections.reverseOrder());
        matrixData.keySet().forEach((key) -> {
            AlphanumComparator sortKey = new AlphanumComparator(matrixData.get(key).size() + "_" + key);
            sortingKeyColumnsMap.put(sortKey, key);
        });
        tempMatrixData.clear();
        sortingKeyColumnsMap.values().forEach((key) -> {
            tempMatrixData.put(key, matrixData.get(key));
        });
        return tempMatrixData;
    }

}
