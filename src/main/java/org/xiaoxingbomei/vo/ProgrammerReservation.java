package org.xiaoxingbomei.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 程序员预约
 */
@Data
public class ProgrammerReservation implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String programmer;      // 程序员
    private String reservationName; // 预约的人
    private String contact;         // 联系方式
    private String company;         // 预约的公司
    private String remark;          // 备注
}
