package com.cricket.fantasy.model.domain.cricsheet; 
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Over{
    private int over;
    private ArrayList<Delivery> deliveries;
}
