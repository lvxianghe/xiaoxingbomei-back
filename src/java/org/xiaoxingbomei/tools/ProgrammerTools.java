package org.xiaoxingbomei.tools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.xiaoxingbomei.entity.request.ProgrammerRequest;
import org.xiaoxingbomei.vo.Company;
import org.xiaoxingbomei.vo.Programmer;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ProgrammerTools
{


    // ===================================================================================

    @Tool(name = "获取程序员信息", description = "根据条件查询程序员信息")
    public List<Programmer> getProgrammerInfo(@ToolParam(description = "查询的条件",required = false)ProgrammerRequest request)
    {
        log.info("获取程序员信息:{}",request);
        // 造一些数据
        List<Programmer> programmerList = new ArrayList<>();
        Programmer programmer1 = new Programmer();
        programmer1.setProgrammerName("小博美");
        programmer1.setEducation("本科");
        programmer1.setProgrammerType("前端");
        programmer1.setSalary("100000");
        programmer1.setExperience("10年");
        programmerList.add(programmer1);
        Programmer programmer2 = new Programmer();
        programmer2.setProgrammerName("大博美");
        programmer2.setEducation("硕士");
        programmer2.setProgrammerType("后端");
        programmer2.setSalary("200000");
        programmer2.setExperience("20年");
        programmerList.add(programmer2);
        return programmerList;
    }

    @Tool(name = "查询全部公司", description = "查询全部公司")
    public List<Company> getAllCompanyInfo()
    {
        log.info("查询全部公司");
        List<Company> companyList = new ArrayList<>();
        Company company1 = new Company();
        company1.setCompanyName("腾讯");
        company1.setCompanyField("互联网");
        companyList.add(company1);
        Company company2 = new Company();
        company2.setCompanyName("阿里");
        company2.setCompanyField("互联网");
        companyList.add(company2);
        return companyList;
    }

    @Tool(name = "生成预约单", description = "生成预约单，返回预约单号")
    public String getProgrammerReservationInfo
            (
                    @ToolParam(description = "程序员")      String programmer,
                    @ToolParam(description = "预约校区")    String reservationName,
                    @ToolParam(description = "联系方式")    String contact,
                    @ToolParam(description = "预约的公司")  String company,
                    @ToolParam(description = "备注", required = false ) String remark
            )
    {
        log.info("生成预约单");
        return "777";
    }


}
