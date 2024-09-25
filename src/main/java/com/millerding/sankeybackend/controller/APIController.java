package com.millerding.sankeybackend.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class APIController {
    @GetMapping("/")
    public static String auth() {
        return "Successfully connected";
    }

    @GetMapping("/example")
    public static String examplePush() {
        ArrayList<String> lines = new ArrayList<String>();
        Scanner reader = new Scanner(System.in);
        System.out.println("enter income");
        lines.add(String.format("Income [%s] Budget", reader.nextLine()));
        System.out.println("enter investments");
        lines.add(String.format("Investments [%s] Budget", reader.nextLine()));
        System.out.println("enter rent");
        lines.add(String.format("Budget [%s] Rent", reader.nextLine()));
        cleanAndWriteToFile(lines, "scripts/input.txt");
        runNodeScript("scripts/sankey.js");
        return "Success";
    }

    public static void main(String[] args) {
        examplePush();
    }

    public static void cleanAndWriteToFile(ArrayList<String> lines, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    writer.write(line.trim());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    public static void runNodeScript(String scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder("node", scriptPath);
        processBuilder.redirectErrorStream(true);
        
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Node script exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
