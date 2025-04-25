package org.xiaoxingbomei.service;

import org.xiaoxingbomei.entity.request.ProgrammerRequest;
import org.xiaoxingbomei.vo.Programmer;

import java.util.List;

public interface ProgrammerService
{
    List<Programmer> getProgrammerInfo(ProgrammerRequest request); // 根据请求条件查询程序员信息

    List<Programmer> getAllProgrammerInfo(); // 查询全部程序员信息
}
