package eu.planlos.p2ncintegrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HealthcheckController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("Health endpoint is invoked");
        return ResponseEntity.ok("OK");
    }
}
