package com.motilaloswal.dto.cfFilteredResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Return{
    public String oneYear_Ret;
    public String oneYear_marketValue;
    public String threeYear_Ret;
    public String threeYear_marketValue;
    public String inception_Ret;
    public String inception_marketValue;
    public String latNavDate;
    public String prodcode;
    public String plancode;
    public String optioncode;
    public String amfi_schcode;
    public String cmt_schcode;
    public String isin;
}
