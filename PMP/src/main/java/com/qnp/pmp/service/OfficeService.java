package com.qnp.pmp.service;

import com.qnp.pmp.dto.OfficerDTO;

import java.io.File;
import java.util.List;

public interface OfficeService {
    void saveOfficer(File image, OfficerDTO officer);
    void updateOfficer(File Image,String id,OfficerDTO officer);
    void deleteOfficer(String id);
    List<OfficerDTO> findByNameAndRankAndCodeAndUnitAndWorkingStatus(String name, String code, String rank, String unit, String workingStatus);
}
