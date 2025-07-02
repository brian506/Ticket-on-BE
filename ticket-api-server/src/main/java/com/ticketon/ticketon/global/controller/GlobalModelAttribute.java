package com.ticketon.ticketon.global.controller;

import com.ticketon.ticketon.global.constants.Urls;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    // 모든 뷰에서 url을 사용할 수 있게 설정
    @ModelAttribute("urls")
    public Class<?> urls() {
        return Urls.class;
    }

}
