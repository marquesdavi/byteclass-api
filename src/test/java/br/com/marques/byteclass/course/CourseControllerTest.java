package br.com.marques.byteclass.course;

import br.com.marques.byteclass.feature.course.adapter.controller.CourseController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(CourseController.class)
class CourseControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private UserRepository userRepository;
//    @MockBean
//    private CourseRepository courseRepository;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void newCourseDTO__should_return_bad_request_when_email_is_invalid() throws Exception {
//
//        CourseRequest newCourseDTO = new CourseRequest();
//        newCourseDTO.setTitle("Java");
//        newCourseDTO.setDescription("Curso de Java");
//        newCourseDTO.setEmailInstructor("paulo@alura.com.br");
//
//        doReturn(Optional.empty()).when(userRepository)
//                .findByEmail(newCourseDTO.getEmailInstructor());
//
//        mockMvc.perform(post("/course/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newCourseDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.field").value("emailInstructor"))
//                .andExpect(jsonPath("$.message").isNotEmpty());
//    }
//
//
//    @Test
//    void newCourseDTO__should_return_bad_request_when_email_is_no_instructor() throws Exception {
//
//        CourseRequest newCourseDTO = new CourseRequest();
//        newCourseDTO.setTitle("Java");
//        newCourseDTO.setDescription("Curso de Java");
//        newCourseDTO.setEmailInstructor("paulo@alura.com.br");
//
//        User user = mock(User.class);
//        doReturn(false).when(user).isInstructor();
//
//        doReturn(Optional.of(user)).when(userRepository)
//                .findByEmail(newCourseDTO.getEmailInstructor());
//
//        mockMvc.perform(post("/course/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newCourseDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.field").value("emailInstructor"))
//                .andExpect(jsonPath("$.message").isNotEmpty());
//    }
//
//    @Test
//    void newCourseDTO__should_return_created_when_new_course_request_is_valid() throws Exception {
//
//        CourseRequest newCourseDTO = new CourseRequest();
//        newCourseDTO.setTitle("Java");
//        newCourseDTO.setDescription("Curso de Java");
//        newCourseDTO.setEmailInstructor("paulo@alura.com.br");
//
//        User user = mock(User.class);
//        doReturn(true).when(user).isInstructor();
//
//        doReturn(Optional.of(user)).when(userRepository).findByEmail(newCourseDTO.getEmailInstructor());
//
//        mockMvc.perform(post("/course/new")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newCourseDTO)))
//                .andExpect(status().isCreated());
//
//        verify(courseRepository, times(1)).save(any(Course.class));
//    }
//
//    @Test
//    void listAllCourses__should_list_all_courses() throws Exception {
//        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);
//
//        Course java = new Course("Java", "Curso de java", paulo);
//        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);
//        Course spring = new Course("Spring", "Curso de spring", paulo);
//
//        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate, spring));
//
//        mockMvc.perform(get("/course/all")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("Java"))
//                .andExpect(jsonPath("$[0].description").value("Curso de java"))
//                .andExpect(jsonPath("$[1].title").value("Hibernate"))
//                .andExpect(jsonPath("$[1].description").value("Curso de hibernate"))
//                .andExpect(jsonPath("$[2].title").value("Spring"))
//                .andExpect(jsonPath("$[2].description").value("Curso de spring"));
//    }

}