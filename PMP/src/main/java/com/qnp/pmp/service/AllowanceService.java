package com.qnp.pmp.service;

import com.qnp.pmp.dto.AllowanceDTO;

import java.util.List;

public interface AllowanceService {
    void insert(AllowanceDTO allowance);
    List<AllowanceDTO>findByOfficerId(int OfficerId);
    void update(String id, AllowanceDTO allowance);
    void delete(String id);
}
