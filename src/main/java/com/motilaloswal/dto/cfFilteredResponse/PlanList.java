package com.motilaloswal.dto.cfFilteredResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanList{
    public String sixth_char_name;
    public String planNameOriginal;
    public String groupedCode;
    public String groupedName;
    public String optionCode;
    public String optionName;
    public String planCode;
    public String planName;
    public String cfReferencePath;
}
