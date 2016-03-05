package com.zuehlke.carrera.comp.service;

import java.util.List;

public interface BroadCaster {

    void broadcast ( List<String> recipients, String message );
}
