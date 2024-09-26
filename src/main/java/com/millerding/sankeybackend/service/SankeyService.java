package com.millerding.sankeybackend.service;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public interface SankeyService {
    public void write(ArrayList<String> input);

    public String buildSankey(String dimension);

    public ArrayList<String> parse(int type, List<String[]> data);
}
