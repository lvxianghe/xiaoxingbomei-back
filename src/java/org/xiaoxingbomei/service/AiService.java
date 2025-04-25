package org.xiaoxingbomei.service;


import org.xiaoxingbomei.entity.response.ResponseEntity;
import reactor.core.publisher.Flux;

public interface AiService
{
    ResponseEntity chat_for_string(String paramString);
    Flux<String>   chat_for_stream(String paramString);
}
