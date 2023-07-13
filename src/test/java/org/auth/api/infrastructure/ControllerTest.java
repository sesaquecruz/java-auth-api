package org.auth.api.infrastructure;


import org.auth.api.infrastructure.config.ObjectMapperConfig;
import org.auth.api.infrastructure.config.SecurityConfig;
import org.auth.api.infrastructure.config.UserDetailsConfig;
import org.auth.api.infrastructure.services.security.AuthTokenService;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@Tag("integrationTest")
@Import({SecurityConfig.class, ObjectMapperConfig.class, AuthTokenService.class, UserDetailsConfig.class})
@WebMvcTest
public @interface ControllerTest {
    @AliasFor(annotation = WebMvcTest.class , attribute = "controllers")
    Class<?>[] controllers() default {};
}
