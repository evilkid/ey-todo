package com.ey.todo.dto;

import javax.validation.constraints.NotEmpty;

public class TodoDto {
    @NotEmpty(message = "Name can not be empty")
    private String name;
    @NotEmpty(message = "Description can not be empty")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
