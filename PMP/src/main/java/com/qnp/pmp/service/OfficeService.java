package com.qnp.pmp.service;

import com.qnp.pmp.dto.OfficerViewDTO;
import com.qnp.pmp.entity.Officer;

import java.io.File;
import java.util.List;

public interface OfficeService {
    void saveOfficer( Officer officer);
    void updateOfficer(File Image,String id,Officer officer);
    void deleteOfficer(String id);
    List<OfficerViewDTO> findByName(String name);
    List<OfficerViewDTO>getOfficerAllowanceStatus();

}
