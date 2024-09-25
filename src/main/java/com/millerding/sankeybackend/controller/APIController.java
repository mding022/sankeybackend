package com.millerding.sankeybackend.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.millerding.sankeybackend.service.SankeyService;
import com.millerding.sankeybackend.service.impl.SankeyServiceImpl;

@Controller
public class APIController {

    static SankeyService sankeyService = new SankeyServiceImpl();

    @GetMapping("/")
    public static String auth() {
        return "Successfully connected";
    }

    @PostMapping("/build")
    public static String buildSankey(@RequestParam("incomes") String[] incomes,
            @RequestParam("ilabels") String[] incomeLabels, @RequestParam("outputs") String[] outputs,
            @RequestParam("olabels") String[] outputLabels) {

        ArrayList<String> lines = new ArrayList<String>();
        double budget = 0;
        for(int i = 0; i < incomes.length; ++i) {
            budget += Double.valueOf(incomes[i]);
            lines.add(String.format("%s [%s] Budget", incomeLabels[i], incomes[i]));
        }
        for(int i = 0; i < outputs.length; ++i) {
            budget -= Double.valueOf(outputs[i]);
            lines.add(String.format("Budget [%s] %s", outputs[i], outputLabels[i]));
        }
        if(budget > 0) {
            lines.add(String.format("Budget [%s] Surplus Budget", String.format("%.2f", budget)));
        }
        sankeyService.write(lines);

        return sankeyService.buildSankey() ? "Success" : "Error";
    }
}
