package com.qnp.pmp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllowanceDTO {
 private LocalDate startDate;
 private LocalDate endDate;
}
