package com.example.chatapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService {
    public void logInfo(String msg) {
        log.info("");
        log.info("");
        log.info(msg);
        log.info("");
        log.info("");
    }

    public void logError(String msg) {
        log.error("");
        log.error("");
        log.error(msg);
        log.error("");
        log.error("");
    }
}
