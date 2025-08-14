package com.qnp.pmp.service;

import com.qnp.pmp.dto.AllowanceDTO;
import com.qnp.pmp.dto.BenefitDetailDTO;
import com.qnp.pmp.entity.Allowance;

import java.util.List;

public interface AllowanceService {
    void insert(Allowance allowance);
    List<BenefitDetailDTO> getBenefitDetails(int id);
    void update(Allowance allowance,int id);
}
