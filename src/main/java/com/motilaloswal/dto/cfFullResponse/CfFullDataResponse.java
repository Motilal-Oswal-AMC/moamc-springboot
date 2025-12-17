package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CfFullDataResponse {
    public ArrayList<String> mostSearchedFunds;
    public ArrayList<String> trendingFunds;
    public ArrayList<String> mostBoughtFunds;
    public ArrayList<CfDataObj> cfDataObjs;
}
