package com.millerding.sankeybackend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.millerding.sankeybackend.service.SankeyService;
import com.millerding.sankeybackend.service.impl.SankeyServiceImpl;
import java.util.*;

@RestController
@RequestMapping("/build")
@CrossOrigin(origins = {"http://localhost:3000", "https://chart.millerding.com"})
public class WorkerController {

    static SankeyService sankeyService = new SankeyServiceImpl();
    
    
    @PostMapping("/budget")
    public String buildSankey(@RequestParam("incomes") String[] incomes,
            @RequestParam("ilabels") String[] incomeLabels, @RequestParam("outputs") String[] outputs,
            @RequestParam("olabels") String[] outputLabels) {

        List<String[]> data = new ArrayList<String[]>(4);
        data.add(incomes);
        data.add(incomeLabels);
        data.add(outputs);
        data.add(outputLabels);
        
        ArrayList<String> lines = sankeyService.parse(1, data);
        sankeyService.write(lines);

        String dimension = "600";
        if(lines.size() < 8) {
            dimension = "400";
        }
        else if(lines.size() < 12) {
            dimension = "500";
        }

        String res = sankeyService.buildSankey(dimension);
        return res.equals("-1") ? "Error!" : "https://api.millerding.com/img/"+res+".png";
    }
}
