package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundManager{
    public String description;
    public String designation;
    public String picture;
    public String fundManagerName;
    public String type;
    public String cfReferencePath;
}
