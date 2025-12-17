package com.motilaloswal.dto.cfFullResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CfDataObj{
    public String indexFundType;
    public String sebiCategory;
    public String nfoFaceValue;
    public String dateOfAllotment;
    public String sebiSubCategory;
    public String objective;
    public String fundSubCategorisation;
    public String portfolioTurnoverRatio;
    public String methodology;
    public String expenseRatioRegular;
    public String standardDeviation;
    public ArrayList<FundManager> fundManager;
    public String displalyOnIfHome;
    public String typeOfScheme;
    public ArrayList<String> fundsTaggingSection;
    public String cmotsProductCode;
    public String beta;
    public String investmentObjective;
    public String periodicReturnsTc;
    public String benchmark;
    public String expenseRatioDirect;
    public String moSchemeCode;
    public String indexMethodology;
    public String fundCategorisation;
    public String fundIcon;
    public String fundType;
    public String moAmcCode;
    public String periodicReturns;
    public String redemptionProceeds;
    public String continuousOffer;
    public SchDetail schDetail;
    public Risk risk;
    public ArrayList<PlanList> planList;
    public String schcode;
    public ArrayList<Aum> aum;
    public ArrayList<Nav> nav;
    public ArrayList<Benchmarkreturn> benchmarkreturns;
    public ArrayList<Return> returns;
    public ArrayList<Minamount> minamount;
    public ArrayList<Sector> sector;
    public Portfolio portfolio;
    public FundManagerDetails fundManagerDetails;
}
