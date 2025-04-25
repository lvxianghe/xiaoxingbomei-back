package org.xiaoxingbomei.dao.localhost;

import org.apache.ibatis.annotations.Mapper;
import org.xiaoxingbomei.entity.request.ProgrammerRequest;
import org.xiaoxingbomei.vo.Programmer;

import java.util.List;

@Mapper
public interface ProgrammerMapper
{
    List<Programmer> getAllProgrammerInfo();
    List<Programmer> getProgrammerInfo(ProgrammerRequest request);
}
