package eu.planlos.p2ncintegrator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthcheckController.class)
class HealthcheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthEndpoint_returnsOK() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/health"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().string("OK"));
    }
}