package com.leadroyal.dex;

import java.util.HashSet;
import java.util.Set;

public class ScanResult {
    public ScanResult(Set<String> targetClasses) {
        this.targetClasses = new HashSet<>(targetClasses);
    }

    public static class Position {
        public String targetClass;
        public String filePath;
        public String innerPath;

        public Position() {
        }

        public Position(String targetClass, String filePath, String innerPath) {
            this.targetClass = targetClass;
            this.filePath = filePath;
            this.innerPath = innerPath;
        }

        @Override
        public String toString() {
            return String.format("%s found @ %s->%s", targetClass, filePath, innerPath);
        }
    }

    public Set<Position> results = new HashSet<>();
    public Set<String> targetClasses;


    public boolean shouldFinish() {
        return targetClasses.isEmpty();
    }


    public void show() {
        System.out.println("This is report!");
        System.out.println("====Known classes====");
        for (Position position : results)
            System.out.println(position);
        System.out.println("====Unknown classes====");
        for (String targetClass : targetClasses)
            System.out.println(targetClass);
    }
}

