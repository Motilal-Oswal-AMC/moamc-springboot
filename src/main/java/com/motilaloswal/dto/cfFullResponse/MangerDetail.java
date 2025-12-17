package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MangerDetail{
    public String managerId;
    public String managerName;
    public String managerDesignation;
    public Object managingSince;
    public String managerDescription;
    public String managerImageUrl;
    public String managerSchcode;
}
