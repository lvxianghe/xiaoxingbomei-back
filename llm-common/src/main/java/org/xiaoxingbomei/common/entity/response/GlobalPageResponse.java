package org.xiaoxingbomei.common.entity.response;

import lombok.Data;

import java.util.List;

/**
 * 通用 分页 响应体
 */
@Data
public class GlobalPageResponse<T> extends GlobalResponse<T>
{
    private static final long serialVersionUID = 1L;

    private int currentPage; // 当前页
    private int pageSize;    // 每页结果数
    private int totalPage;   // 总页数
    private int total;       // 总结果数

    /**
     *
     */
//    public static <T> GlobalPageResponse<T> toPageResponse(List<T> datas,int total,int pageSize)
//    {
//        GlobalPageResponse multiResponse = new GlobalPageResponse();
//        multiResponse.setData(datas);
//        multiResponse.setTotal(total);
//        multiResponse.setPageSize(pageSize);
//        multiResponse.setTotalPage((pageSize+total-1)/pageSize);
//        return multiResponse;
//    }

}
