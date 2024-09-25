package com.millerding.sankeybackend.service.impl;

import java.io.*;
import java.util.*;

import com.millerding.sankeybackend.service.SankeyService;

public class SankeyServiceImpl implements SankeyService {
    public void write(ArrayList<String> input) {
        String filePath = "scripts/input.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : input) {
                if (!line.trim().isEmpty()) {
                    writer.write(line.trim());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    public boolean buildSankey() {
        return (runNodeScript("scripts/sankey.js") == 0);
    }

    public int runNodeScript(String scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder("node", scriptPath);
        processBuilder.redirectErrorStream(true);
        
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
