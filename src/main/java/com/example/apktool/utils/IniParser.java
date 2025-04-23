package com.example.apktool.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IniParser {
    
    private static final Pattern SECTION_PATTERN = Pattern.compile("\\s*\\[([^]]+)\\]\\s*");
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("\\s*([^=]+)=(.*)");
    
    /**
     * Parse an INI file into a map of section -> key -> value
     *
     * @param filePath Path to the INI file
     * @return Map of sections and their key-value pairs
     */
    public Map<String, Map<String, String>> parse(String filePath) throws IOException {
        Map<String, Map<String, String>> result = new HashMap<>();
        String currentSection = null;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";") || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                Matcher sectionMatcher = SECTION_PATTERN.matcher(line);
                if (sectionMatcher.matches()) {
                    currentSection = sectionMatcher.group(1).trim();
                    result.put(currentSection, new HashMap<>());
                } else if (currentSection != null) {
                    Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(line);
                    if (keyValueMatcher.matches()) {
                        String key = keyValueMatcher.group(1).trim();
                        String value = keyValueMatcher.group(2).trim();
                        result.get(currentSection).put(key, value);
                    }
                }
            }
        }
        
        return result;
    }
}
