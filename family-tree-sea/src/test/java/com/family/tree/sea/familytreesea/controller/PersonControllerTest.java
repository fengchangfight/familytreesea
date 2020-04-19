package com.family.tree.sea.familytreesea.controller;

import com.family.tree.sea.familytreesea.entity.*;
import com.family.tree.sea.familytreesea.service.*;
import com.family.tree.sea.familytreesea.utils.RedisUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

    private MockMvc mvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private FamilyTreeService familyTreeService;

    @MockBean
    private AccessRightService accessRightService;

    @MockBean
    private RedisUtil redisUtil;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private UserService userService;

    @MockBean
    private LogService logService;

    @MockBean
    private RelationshipService relationshipService;

    @MockBean
    private ImageUploadHistoryService imageUploadHistoryService;

    @Before
    public void setup(){

        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }


    @WithMockUser(value = "18888888888")
    @Test
    public void testGetPersonById() throws Exception
    {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("18888888888");


        Person fakePerson = createFakePerson();
        when(personService.findOne(anyLong())).thenReturn(fakePerson);

        when(userService.findByUsername(anyString())).thenReturn(createFakeUser());

        AccessRight accessRight = createFakeAccessRight();
        when(accessRightService.findByUserIdAndFamilyTreeId(anyLong(), anyLong())).thenReturn(accessRight);

        mvc.perform( MockMvcRequestBuilders
                .get("/api/v1/person/123").header("Origin", "http://localhost:8865")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lifeDescription").exists());
    }

    private Person createFakePerson(){
        Person p = new Person();
        p.setAddress("Shanghai");
        p.setBorn("2000-12-12");
        p.setDeath("3000-12-12");
        p.setFamilyTree(createFakeFamilyTree());
        p.setId(-1L);
        p.setZibei("汉");
        p.setShortDescription("short desc");
        p.setSex(1);
        p.setName("胡汉三");
        p.setLifeDescription("非常长的一段");
        p.setPhone("1888888888");
        p.setRank(3);

        return p;
    }

    private AccessRight createFakeAccessRight(){
        AccessRight accessRight = new AccessRight();

        accessRight.setAccessRight("admin");
        accessRight.setEntityId(-1L);
        accessRight.setEntityType("person");
        accessRight.setUser(createFakeUser());
        return accessRight;
    }

    private FamilyTree createFakeFamilyTree(){
        FamilyTree ft = new FamilyTree();
        ft.setCreatedBy(createFakeUser());
        ft.setCreatedOn(new Date());
        ft.setId(-1L);
        ft.setName("Fake tree");
        ft.setDescription("Fake ft descr");
        return ft;
    }

    private User createFakeUser(){
        User user = new User();
        user.setPhone("18888888888");
        user.setEmail("fake@fake.com");
        user.setNickname("Nick");
        user.setPassword("Passw0rd");
        user.setPasswordConfirm("Passw0rd");
        user.setSalt("dfsdfsdf");
        user.setUsername("18888888888");
        user.setRegisterDate(new Date());
        user.setId(-1L);
        Set<Role> roles = new HashSet<>(Arrays.asList(createFakeRole()));
        user.setRoles(roles);
        return user;
    }

    private Role createFakeRole(){
        Role role = new Role();
        role.setId(-1L);
        role.setName("admin");
        return role;
    }


}
