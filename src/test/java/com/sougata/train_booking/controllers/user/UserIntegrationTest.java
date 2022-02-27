package com.sougata.train_booking.controllers.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sougata.train_booking.models.authentication.AuthenticationRequest;
import com.sougata.train_booking.models.entities.User;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    //@WithMockUser("/sougata")
    @Test
    public void createAndReadAndUpdateAndDestroyUser() throws Exception {
        //////////////////////////// creating ///////////////////////////////////////
        User user = new User();
        user.setName("Peter Parker");
        user.setUserName("peterParker");
        user.setPassword("qaz123zxc@PP");
        user.setRoles("ROLE_USER");

        String jsonRequest = mapper.writeValueAsString(user);

        MvcResult result = mockMvc.perform(post("/create_user")
                .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        assertEquals(201, result.getResponse().getStatus());

        ///////////////////////////// authenticating /////////////////////////////////////////

        String bearer;
        AuthenticationRequest request = new AuthenticationRequest("peterParker", "qaz123zxc@PP");
        String jsonRequest2 = mapper.writeValueAsString(request);
        MvcResult result1 = mockMvc.perform(post("/authenticate")
                .content(jsonRequest2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result1.getResponse().getStatus());

        JSONObject jsonObject = new JSONObject(result1.getResponse().getContentAsString());
        bearer = "Bearer " + jsonObject.get("jwt");

        //////////////////////////////////////  Reading  ///////////////////////////////////////////////////

        MvcResult result2 = mockMvc.perform(get("/get_my_profile")
                .header("Authorization", bearer))
                .andExpect(status().isOk())
                .andReturn();

        String map = result2.getResponse().getContentAsString();
        JSONObject jsonObject2 = new JSONObject(map);
        Gson gson = new Gson();
        User userRead = gson.fromJson(jsonObject2.toString(), User.class);
        assertEquals(200, result2.getResponse().getStatus());
        assertEquals(userRead.getUserName(), user.getUserName());

        //////////////////////////////////////  Updating  //////////////////////////////////////////

        User newMe = new User();
        newMe.setName("Peter Parkerr");

        String jsonRequest3 = mapper.writeValueAsString(newMe);

        MvcResult result3 = mockMvc.perform(
                put("/edit_my_profile")
                .content(jsonRequest3).contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", bearer)
                ).andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result3.getResponse().getStatus());

        String map2 = result3.getResponse().getContentAsString();
        JSONObject jsonObject3 = new JSONObject(map2);
        User userAfterUpdate = gson.fromJson(jsonObject3.toString(), User.class);
        assertEquals("Peter Parkerr", userAfterUpdate.getName());

        //////////////////////////////////////  Destroying /////////////////////////////////////////

        /// need to authenticate again after updating ///
        String bearer2;
        AuthenticationRequest request2 = new AuthenticationRequest("peterParker", "qaz123zxc@PP");
        String jsonRequest4 = mapper.writeValueAsString(request2);
        MvcResult result4 = mockMvc.perform(post("/authenticate")
                .content(jsonRequest4).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, result4.getResponse().getStatus());

        JSONObject jsonObject4 = new JSONObject(result4.getResponse().getContentAsString());
        bearer2 = "Bearer " + jsonObject4.get("jwt");
        /////////////////

        MvcResult result5 = mockMvc.perform(delete("/remove_user")
                .header("Authorization", bearer2))
                .andExpect(status().isNoContent()).andReturn();

        assertEquals(204, result5.getResponse().getStatus());

        ////////////////////////////////////////////////////////////////////////////////////
    }
}
