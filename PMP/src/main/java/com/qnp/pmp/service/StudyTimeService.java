package com.qnp.pmp.service;

import com.qnp.pmp.dto.StudyRoundViewDTO;
import com.qnp.pmp.entity.StudyTime;

import java.time.LocalDate;
import java.util.List;

public interface StudyTimeService {
    void saveStudyTime(StudyTime studyTime);
    List<StudyRoundViewDTO>getByOfficeId(int officeId);
    LocalDate getLastEndDateByOfficerId(int officerId);
}
