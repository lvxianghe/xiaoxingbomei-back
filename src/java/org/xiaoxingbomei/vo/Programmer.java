package org.xiaoxingbomei.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 程序员
 */
@Data
public class Programmer implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String programmerName; // 程序员的姓名
    private String education;      // 程序员的学历  0-无 1-初中 2-高中 3-大专 4-本科 5-硕士 6-博士
    private String programmerType; // 程序员类型    0-全栈 1-前端 2-后端 3-数据 4-测试 5-运维 6-其他
    private String salary;         // 程序员薪资
    private String experience;     // 程序员经验（年）

}
