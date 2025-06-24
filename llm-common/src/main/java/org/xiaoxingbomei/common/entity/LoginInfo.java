package org.xiaoxingbomei.common.entity;


import lombok.Data;

import java.util.List;

@Data
public class LoginInfo
{
    private String       loginId;        // 登录id
    private String       loginName;      // 登录用户名
    private String       password;       // 登录密码
    private List<String> roleList;       // 登录用户所有的角色
    private List<String> permissionList; // 登录用户所有的权限
}
