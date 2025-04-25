package org.xiaoxingbomei.service;

import org.xiaoxingbomei.Enum.HttpMethodEmun;

public interface Client {


    void setMethod(HttpMethodEmun httpMethodEmun);

    Object send(Object... arms);
}