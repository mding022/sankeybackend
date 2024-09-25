package com.millerding.sankeybackend.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

@Service
public interface SankeyService {
    public void write(ArrayList<String> input);

    public boolean buildSankey();
}
