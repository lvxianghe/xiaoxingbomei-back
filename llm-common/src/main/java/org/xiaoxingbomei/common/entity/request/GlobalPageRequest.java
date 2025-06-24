package org.xiaoxingbomei.common.entity.request;

import lombok.Data;

/**
 * 通用 分页 请求体
 */
@Data
public class GlobalPageRequest extends GlobalRequest
{
    private static final long serialVersionUID = 1L;

    private int currentPage;  // 当前页
    private int pageSize;     // 每页结果数
}
