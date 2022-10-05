package com.ey.todo;

import com.ey.todo.dto.TodoDto;
import com.ey.todo.entites.Status;
import com.ey.todo.entites.Todo;
import com.ey.todo.repositories.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TodoControllerTest {
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private MockMvc mockMvc;
    private static final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setup() {
        todoRepository.deleteAll();
    }

    @Test
    public void testTodoEndpointWithGETList() throws Exception {
        List<TodoDto> todoDtos = List.of(exampleTodoDto(), exampleTodoDto());

        List<Todo> expectedRecords = new ArrayList<>();

        for (TodoDto todoDto : todoDtos) {
            expectedRecords.add(om.readValue(mockMvc.perform(post("/todos")
                            .contentType("application/json")
                            .content(om.writeValueAsString(todoDto)))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class));
        }

        mockMvc.perform(get("/todos"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(expectedRecords.size())))
                .andExpect(status().isOk());
    }

    @Test
    public void testTodoEndpointWithGETById() throws Exception {
        Todo expectedRecord = om.readValue(mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(exampleTodoDto())))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class);

        Todo actualRecord = om.readValue(mockMvc.perform(get("/todos/" + expectedRecord.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Todo.class);

        Assert.assertTrue(new ReflectionEquals(expectedRecord).matches(actualRecord));

        mockMvc.perform(get("/todos/" + Integer.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testTodoEndpointWithPOST() throws Exception {

        TodoDto todoDto = exampleTodoDto();

        Todo actualRecord = om.readValue(mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(todoDto)))
                .andDo(print())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class);

        assertTrue(todoRepository.findById(actualRecord.getId()).isPresent());
    }

    @Test
    public void testTodoEndpointWithPOSTBadRequest() throws Exception {

        TodoDto todoDto = exampleTodoDto();
        todoDto.setName(null);

        mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(todoDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testTodoEndpointWithDelete() throws Exception {
        Todo expectedRecord = om.readValue(mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(exampleTodoDto())))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class);

        mockMvc.perform(delete("/todos/" + expectedRecord.getId())
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todos/" + expectedRecord.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testTodoEndpointWithPut() throws Exception {
        Todo expectedRecord = om.readValue(mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(exampleTodoDto())))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class);

        TodoDto updatedTodoDto = exampleTodoDto();
        updatedTodoDto.setName("Updated Name");

        Todo updatedTodo = om.readValue(mockMvc.perform(put("/todos/" + expectedRecord.getId())
                        .contentType("application/json")
                        .content(om.writeValueAsString(updatedTodoDto))
                )
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Todo.class);

        assertEquals(updatedTodo.getName(), updatedTodo.getName());
    }

    @Test
    public void testTodoEndpointWithPutNotFound() throws Exception {
        mockMvc.perform(put("/todos/" + Integer.MAX_VALUE)
                        .contentType("application/json")
                        .content(om.writeValueAsString(exampleTodoDto()))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testTodoEndpointWithPatch() throws Exception {
        Todo expectedRecord = om.readValue(mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(exampleTodoDto())))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class);


        Todo updatedStatusTodo = om.readValue(
                mockMvc.perform(patch("/todos/" + expectedRecord.getId() + "/status/" + Status.DONE.name()))
                        .andDo(print())
                        .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), Todo.class);

        assertEquals(Status.DONE, updatedStatusTodo.getStatus());
    }

    @Test
    public void testTodoEndpointWithPatchBadRequest() throws Exception {
        Todo expectedRecord = om.readValue(mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content(om.writeValueAsString(exampleTodoDto())))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Todo.class);

        mockMvc.perform(patch("/todos/" + expectedRecord.getId() + "/status/RANDOM"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private TodoDto exampleTodoDto() {
        TodoDto todoDto = new TodoDto();
        todoDto.setName("Example Name");
        todoDto.setDescription("Example Description");
        return todoDto;
    }

}
