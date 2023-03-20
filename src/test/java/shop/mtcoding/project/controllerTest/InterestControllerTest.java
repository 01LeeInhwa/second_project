package shop.mtcoding.project.controllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.mtcoding.project.dto.interest.InterestReq.InterestChangeReqDto;
import shop.mtcoding.project.model.user.User;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class InterestControllerTest {
    
    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc mvc;

    private MockHttpSession mockSession;

    @BeforeEach
    private void mockUserSession() {
        User mockUser = new User(
                1,
                "ssar@nate.com",
                "1234",
                "ssar",
                "2000-01-01",
                "010-1234-1234",
                "/images/default_profile.png",
                "부산시 부산진구",
                new Timestamp(System.currentTimeMillis()));
        mockSession = new MockHttpSession();
        mockSession.setAttribute("principal", mockUser);
    }

    @Test
    public void 관심수정_test() throws Exception {
        // given
        InterestChangeReqDto iDto = new InterestChangeReqDto();
        List<String> inter = new ArrayList<>();
        inter.add("백엔드 개발자");
        inter.add("풀스택 개발자");
        inter.add("임베디드 개발자");
        iDto.setUserId(1);
        iDto.setUserId(1);
        iDto.setInterestList(inter);
        String insert = om.writeValueAsString(iDto);
    
        // when
        ResultActions rs = mvc.perform(put("/user/interest/change").content(insert).contentType(MediaType.APPLICATION_JSON_VALUE).session(mockSession));
    
        // then
        System.out.println("테스트 : "+ rs.andReturn().getResponse().getContentAsString());
    }
}
