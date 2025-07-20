package com.qnp.pmp.service;

import com.qnp.pmp.dto.StudyRoundViewDTO;
import com.qnp.pmp.entity.StudyTime;

import java.util.List;

public interface StudyTimeService {
    void saveStudyTime(StudyTime studyTime);
    List<StudyRoundViewDTO>getByOfficeId(int officeId);
}
