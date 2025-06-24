package org.xiaoxingbomei.common.entity;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统
 */
@Data
public class SystemEntity implements Serializable
{
    private static final long serialVersionUID = 1L;


    @ExcelProperty("系统编号")
    @Schema(description = "系统编号")
    private String systemNumber;

    @ExcelProperty("系统中文名称")
    @Schema(description = "系统中文名称")
    private String systemChineseName;

    @ExcelProperty("系统英文名称")
    @Schema(description = "系统英文名称")
    private String systemEnglishName;

    @ExcelProperty("系统类型")
    @Schema(description = "系统类型")
    private String systemType;

    @ExcelProperty("系统等级")
    @Schema(description = "系统等级")
    private String systemLevel;

    @ExcelProperty("系统版本")
    @Schema(description = "系统版本")
    private String systemVersion;

    @ExcelProperty("系统描述")
    @Schema(description = "系统描述")
    private String systemDescription;

    @ExcelProperty("系统负责人")
    @Schema(description = "系统负责人")
    private String systemAuthor;

    @ExcelProperty("系统负责部门")
    @Schema(description = "系统负责部门")
    private String systemDepartment;

    @ExcelProperty("系统联系方式-邮件")
    @Schema(description = "系统联系方式-邮件")
    private String systemEmail;

    @ExcelProperty("系统联系方式-电话号")
    @Schema(description = "系统联系方式-电话号")
    private String systemPhone;

    @ExcelProperty("系统联系方式-地址")
    @Schema(description = "系统联系方式-地址")
    private String systemAddress;

    @ExcelProperty("系统创建时间")
    @Schema(description = "系统创建时间")
    private String systemCreateTime;

    @ExcelProperty("系统更新时间")
    @Schema(description = "系统更新时间")
    private String systemUpdateTime;

    @ExcelProperty("系统状态")
    @Schema(description = "系统状态 0-未启用 1-启用")
    private String systemStatus;

}
