package com.qnp.pmp.service;

import com.qnp.pmp.dto.WorkTimeDTO;

import java.util.List;

public interface WorkTimeService {
    void insert(WorkTimeDTO dto);
    void update(WorkTimeDTO dto);
    void delete(String id);
    WorkTimeDTO findById(String id);
    List<WorkTimeDTO> findByOfficerId(String officerId);
    List<WorkTimeDTO> findAll();
}
