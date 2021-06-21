package services;

import domain.AdmissionYear;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AdmissionYearListService {
    public Set<AdmissionYear> getListOfYear(List<List<Object>> sheet, int pkColumnNumber) {
        Set<AdmissionYear> admissionYearList = new LinkedHashSet<>();
        for (List<Object> row : sheet) {
            AdmissionYear year = new AdmissionYear();
            year.setYear(Integer.parseInt(row.get(pkColumnNumber).toString().substring(0, 2)));
            admissionYearList.add(year);
        }
        return admissionYearList;
    }
}