package org.xiaoxingbomei.entity.request;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

/**
 * 程序员信息的 请求体
 */
@Data
public class ProgrammerRequest
{
    @ToolParam(required=false,description = "程序员类型:全栈、前端、后端、数据、测试、运维、其他")
    private String programmerType;

    @ToolParam(required=false,description = "学历要求:无、初中、高中、大专、本科、硕士、博士")
    private String education;

    @ToolParam(required=false,description = "排序方式")
    private List<Sort> sorts;

    @Data
    public static class Sort
    {
        @ToolParam(required = false, description = "排序字段: salary或experience")
        private String field;
        @ToolParam(required = false, description = "是否是升序: true/false")
        private String IsAsc;
    }

}
