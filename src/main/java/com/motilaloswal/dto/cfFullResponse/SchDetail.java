package com.motilaloswal.dto.cfFullResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchDetail{
    public String nfoStartDate;
    public String cmotsGroupCode;
    public String schemeName;
    public String nfoEndDate;
    @JsonProperty("CMOTSAMCCode")
    public String cMOTSAMCCode;
}
