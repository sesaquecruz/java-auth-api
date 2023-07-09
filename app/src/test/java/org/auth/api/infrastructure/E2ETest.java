package org.auth.api.infrastructure;

import org.auth.api.App;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-e2e")
@Tag("e2eTest")
@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
public @interface E2ETest {
}
