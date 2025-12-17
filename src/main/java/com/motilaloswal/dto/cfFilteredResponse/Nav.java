package com.motilaloswal.dto.cfFilteredResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nav{
    public String latnav;
    public String optioncode;
    public String plancode;
    public String prodcode;
    public String nav_date;
    public String nav_amount;
    public String recdt;
    public String navchng;
    public String navchngper;
}
