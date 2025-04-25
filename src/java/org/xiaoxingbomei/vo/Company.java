package org.xiaoxingbomei.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 公司
 */
@Data
public class Company implements Serializable
{

    private String companyName;  // 公司名称
    private String companyField; // 公司领域 如互联网、金融、教育、医疗、游戏等

}
