package com.scheible.risingempire.web.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author sj
 */
@Controller
public class CsrfController {
	
	@RequestMapping("/csrf")
	public @ResponseBody
	CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
