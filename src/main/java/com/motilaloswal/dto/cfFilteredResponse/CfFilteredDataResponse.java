package com.motilaloswal.dto.cfFilteredResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CfFilteredDataResponse {
    public String schemeName;
    public Risk risk;
    public ArrayList<PlanList> planList;
    public ArrayList<Nav> nav;
    public ArrayList<Return> returns;
    public String schcode;
}
