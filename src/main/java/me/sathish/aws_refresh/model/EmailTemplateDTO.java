package me.sathish.aws_refresh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class EmailTemplateDTO {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String code;

    @NotNull
    private String subjectTemplate;

    @NotNull
    private String bodyTemplate;

    @NotNull
    @JsonProperty("isActive")
    private Boolean isActive;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

}
