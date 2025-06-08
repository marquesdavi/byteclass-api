package br.com.marques.byteclass.user;

import br.com.marques.byteclass.feature.user.adapter.controller.UserController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(UserController.class)
class UserControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void newUser__should_return_bad_request_when_email_is_blank() throws Exception {
//        NewUserDTO newUserDTO = new NewUserDTO();
//        newUserDTO.setEmail("");
//        newUserDTO.setName("Caio Bugorin");
//        newUserDTO.setRole(Role.STUDENT);
//
//        mockMvc.perform(post("/user/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newUserDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$[0].field").value("email"))
//                .andExpect(jsonPath("$[0].message").isNotEmpty());
//    }
//
//    @Test
//    void newUser__should_return_bad_request_when_email_is_invalid() throws Exception {
//        NewUserDTO newUserDTO = new NewUserDTO();
//        newUserDTO.setEmail("caio");
//        newUserDTO.setName("Caio Bugorin");
//        newUserDTO.setRole(Role.STUDENT);
//
//        mockMvc.perform(post("/user/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newUserDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$[0].field").value("email"))
//                .andExpect(jsonPath("$[0].message").isNotEmpty());
//    }
//
//    @Test
//    void newUser__should_return_bad_request_when_email_already_exists() throws Exception {
//        NewUserDTO newUserDTO = new NewUserDTO();
//        newUserDTO.setEmail("caio.bugorin@alura.com.br");
//        newUserDTO.setName("Caio Bugorin");
//        newUserDTO.setRole(Role.STUDENT);
//
//        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(true);
//
//        mockMvc.perform(post("/user/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newUserDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.field").value("email"))
//                .andExpect(jsonPath("$.message").value("Email já cadastrado no sistema"));
//    }
//
//    @Test
//    void newUser__should_return_created_when_user_request_is_valid() throws Exception {
//        NewUserDTO newUserDTO = new NewUserDTO();
//        newUserDTO.setEmail("caio.bugorin@alura.com.br");
//        newUserDTO.setName("Caio Bugorin");
//        newUserDTO.setRole(Role.STUDENT);
//
//        when(userRepository.existsByEmail(newUserDTO.getEmail())).thenReturn(false);
//
//        mockMvc.perform(post("/user/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newUserDTO)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    void listAllUsers__should_list_all_users() throws Exception {
//        User user1 = new User("User 1", "user1@test.com",Role.STUDENT);
//        User user2 = new User("User 2", "user2@test.com",Role.STUDENT);
//        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
//
//        mockMvc.perform(get("/user/all")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("User 1"))
//                .andExpect(jsonPath("$[1].name").value("User 2"));
//    }

}