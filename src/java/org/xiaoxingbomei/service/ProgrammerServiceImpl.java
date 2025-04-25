package org.xiaoxingbomei.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiaoxingbomei.dao.localhost.ProgrammerMapper;
import org.xiaoxingbomei.entity.request.ProgrammerRequest;
import org.xiaoxingbomei.vo.Programmer;

import java.util.List;

@Service
public class ProgrammerServiceImpl implements ProgrammerService
{
    @Autowired
    ProgrammerMapper programmerMapper;

    // =============================

    @Override
    public List<Programmer> getProgrammerInfo(ProgrammerRequest request)
    {
        return List.of();
    }

    @Override
    public List<Programmer> getAllProgrammerInfo()
    {
        List<Programmer> allProgrammerInfo = programmerMapper.getAllProgrammerInfo();
        if (allProgrammerInfo != null)
        {
            return allProgrammerInfo;
        }
        return List.of();
    }
}
