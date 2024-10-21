package com.myprog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    // from org.apache.commons.commons-lang3
    public static int countMatches(CharSequence str, CharSequence sub) {
        int count = 0;

        for (int idx = 0; (idx = indexOf(str, sub, idx)) != -1; idx += sub.length()) {
            ++count;
        }

        return count;
    }

    // from org.apache.commons.commons-lang3
    static int indexOf(CharSequence cs, CharSequence searchChar, int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new NullPointerException("filename parameter is not set");
        }
        String filename = args[0];
        long startTime = System.currentTimeMillis();
        Map<String, Integer> keysMap = new HashMap<>();

        Map<Integer, LinkedHashSet<String>> groupsMap = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            //int counter = 0;
            int groupsNumber = 0;
            String line = reader.readLine();
            while (line != null) {
                //counter++;
                //System.out.println(counter);
                String[] points = line.split(";");
                if (Arrays.stream(points).anyMatch(s -> countMatches(s, "\"") != 2 && s.length() != 0)) {
                    break;
                }
                List<String> keys = new ArrayList<>();
                int pointsCounter = 0;
                int currentGroupNumber = -1;
                for (String point : points) {
                    pointsCounter++;
                    String key = point.replaceAll("\"", "");
                    if (key.isEmpty()) {
                        continue;
                    }
                    key += "_" + pointsCounter;
                    keys.add(key);
                    // могут быть и совпадения в других группах?
                    if (currentGroupNumber == -1 && keysMap.containsKey(key)) {
                        currentGroupNumber = keysMap.get(key);
                    }
                }
                if (currentGroupNumber == -1) {
                    currentGroupNumber = ++groupsNumber;
                }
                for (String key : keys) {
                    if (!keysMap.containsKey(key)) {
                        keysMap.put(key, currentGroupNumber);
                    }
                }
                if (!groupsMap.containsKey(currentGroupNumber)) {
                    LinkedHashSet<String> newGroup = new LinkedHashSet<>();
                    newGroup.add(line);
                    groupsMap.put(currentGroupNumber, newGroup);
                } else {
                    groupsMap.get(currentGroupNumber).add(line);
                }

                line = reader.readLine();
            }

            List<Integer> sortedGroupMapKeys =
                    groupsMap.entrySet().stream()
                            .sorted(Comparator.comparingInt(f -> f.getValue().size()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());
            Collections.reverse(sortedGroupMapKeys);

            int c = 0;
            for (LinkedHashSet<String> groupLines : groupsMap.values()) {
                if (groupLines.size() > 1) {
                    c++;
                }
            }

            PrintStream fileOut = new PrintStream("out.txt");
            fileOut.println("The number of groups with more than one element: " + c);
            int newGroupNumber = 0;
            for (Integer groupNumber : sortedGroupMapKeys) {
                fileOut.println("Group " + ++newGroupNumber);
                //fileOut.println("количество строк " + groupsMap.get(groupNumber).size());
                for (String outLine : groupsMap.get(groupNumber)) {
                    fileOut.println(outLine);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\nSpend seconds: " + TimeUnit.MILLISECONDS.toSeconds(endTime - startTime));
    }
}
