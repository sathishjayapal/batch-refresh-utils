package me.sathish.aws_refresh.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import me.sathish.aws_refresh.validation.ValidCron;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class BatchJobDTO {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    private String description;

    @Size(max = 100)
    @ValidCron(allowNull = true)
    private String scheduleCron;

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
