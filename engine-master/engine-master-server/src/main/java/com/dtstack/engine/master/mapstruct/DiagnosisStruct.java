package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.api.vo.diagnosis.JobDiagnosisInformationVO;
import com.dtstack.engine.master.bo.JobDiagnosisInformation;
import org.mapstruct.Mapper;

/**
 * @author yuebai
 * @date 2021-09-07
 */
@Mapper(componentModel = "spring")
public interface DiagnosisStruct {

    JobDiagnosisInformationVO toInformationVO(JobDiagnosisInformation JobDiagnosisInformation);

}
