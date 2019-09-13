package io.jzheaux.springone2019.message;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.core.annotation.CurrentSecurityContext;

/**
 * @author Rob Winch
 */
@Retention(RetentionPolicy.RUNTIME)
@CurrentSecurityContext(expression="authentication.tokenAttributes['user_id']")
public @interface CurrentUserId {
}
