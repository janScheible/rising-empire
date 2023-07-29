package com.scheible.risingempire.webapp.adapter.frontend.annotation;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author sj
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@RequestMapping(path = "/game/games/{gameId}/{player}", produces = APPLICATION_JSON_VALUE)
public @interface FrontendController {

}
