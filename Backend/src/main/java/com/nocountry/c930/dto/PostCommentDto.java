package com.nocountry.c930.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class PostCommentDto {

    @NotBlank(message = "The comment cannot be blank")
    private String text;

}
