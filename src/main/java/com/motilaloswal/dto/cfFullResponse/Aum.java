package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aum{
    public String latestAum;
    public Date latestAumAsOnDt;
    public String optioncode;
    public String plancode;
    public String prodcode;
}
