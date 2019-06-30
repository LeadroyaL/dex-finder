package com.leadroyal.dex;

import java.util.HashSet;
import java.util.Set;

public class Config {
    public Set<String> targetClasses = new HashSet<>();
    public Set<String> files = new HashSet<>();
    public Set<String> directories = new HashSet<>();
    public boolean recursive;
}
