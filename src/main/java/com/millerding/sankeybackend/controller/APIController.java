package com.millerding.sankeybackend.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.millerding.sankeybackend.service.SankeyService;
import com.millerding.sankeybackend.service.impl.SankeyServiceImpl;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class APIController {

    static SankeyService sankeyService = new SankeyServiceImpl();

    @GetMapping("/")
    public String auth() {
        return "Successfully connected";
    }

    @GetMapping("/img/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource file = new ClassPathResource("public/images/" + filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(file);
    }

    @PostMapping("/build")
    public String buildSankey(@RequestParam("incomes") String[] incomes,
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
            lines.add(String.format("Budget [%s] Surplus", String.format("%.2f", budget)));
            lines.add(":Surplus #66ee71");
        }
        if(budget < 0) {
            budget *= -1;
            lines.add(String.format("Shortfall [%s] Budget", String.format("%.2f", budget)));
            lines.add(":Shortfall #f54e42");
        }
        sankeyService.write(lines);

        String dimension = "600";
        if(lines.size() < 8) {
            dimension = "400";
        }
        else if(lines.size() < 12) {
            dimension = "500";
        }

        String res = sankeyService.buildSankey(dimension);
        return res.equals("-1") ? "Error!" : "http://localhost:8080/img/"+res+".png";
    }
}
