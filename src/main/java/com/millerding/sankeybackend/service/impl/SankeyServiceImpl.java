package com.millerding.sankeybackend.service.impl;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import com.millerding.sankeybackend.service.SankeyService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public String buildSankey(String dimension) {
        if (runNodeScript("scripts/sankey.js", dimension) != 0) {
            return "-1";
        }
        String uuid = UUID.randomUUID().toString();
        Path sourcePath = Paths.get("scripts/");
        Path targetPath = Paths.get("src/main/resources/public/images", uuid + ".png");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath, "*.png")) {
            for (Path entry : stream) {
                Files.move(entry, targetPath, StandardCopyOption.REPLACE_EXISTING);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        runNodeScript("scripts/process.js", uuid);

        return uuid + "p";
    }

    public int runNodeScript(String scriptPath, String dimension) {
        ProcessBuilder processBuilder = new ProcessBuilder("node", scriptPath, dimension);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line); // log script status msg's
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public ArrayList<String> parse(int type, List<String[]> data) {
        ArrayList<String> lines = new ArrayList<String>();
        switch (type) {
            case 1:
                String[] incomes = data.get(0);
                String[] incomeLabels = data.get(1);
                String[] outputs = data.get(2);
                String[] outputLabels = data.get(3);
                double budget = 0;
                for (int i = 0; i < incomes.length; ++i) {
                    budget += Double.valueOf(incomes[i]);
                    lines.add(String.format("%s [%s] Budget", incomeLabels[i], incomes[i]));
                }
                for (int i = 0; i < outputs.length; ++i) {
                    budget -= Double.valueOf(outputs[i]);
                    lines.add(String.format("Budget [%s] %s", outputs[i], outputLabels[i]));
                }
                if (budget > 0) {
                    lines.add(String.format("Budget [%s] Surplus", String.format("%.2f", budget)));
                    lines.add(":Surplus #66ee71");
                }
                if (budget < 0) {
                    budget *= -1;
                    lines.add(String.format("Deficit [%s] Budget", String.format("%.2f", budget)));
                    lines.add(":Deficit #f54e42");
                }
                break;
        }
        return lines;
    }
}
