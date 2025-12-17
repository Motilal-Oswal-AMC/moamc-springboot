package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundManagerDetails{
    public ArrayList<MangerDetail> mangerDetails;
    public ArrayList<SchemeReturn> schemeReturns;
}
