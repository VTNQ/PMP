package com.qnp.pmp.service;

import com.qnp.pmp.dto.LogDTO;

import java.util.List;

public interface LogService {
    void insert(LogDTO dto);
    List<LogDTO> findByOfficerId(String officerId);
    List<LogDTO> findAll();
    List<LogDTO> findRecent(int limit); // dùng cho /notifications/history
}
