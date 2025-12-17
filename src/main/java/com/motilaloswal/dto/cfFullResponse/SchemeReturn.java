package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemeReturn{
    public String managerId;
    public String schemeName;
    public String period;
    public String schReturnCagr;
    public String schReturnAsOnDt;
    public Object fixedbmreturncagr;
    public Object fixedbmreturnasondt;
    public String prodcode;
    public String schemeCode;
    public String planCode;
}
