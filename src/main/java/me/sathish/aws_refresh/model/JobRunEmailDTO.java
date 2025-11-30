package me.sathish.aws_refresh.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class JobRunEmailDTO {

    private Long id;

    @NotNull
    @Size(max = 320)
    private String recipientEmail;

    @Size(max = 200)
    private String recipientName;

    @NotNull
    private String subject;

    @NotNull
    private String body;

    @NotNull
    @Size(max = 30)
    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime sentAt;

    private String errorMessage;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;

    @NotNull
    private Long jobRun;

    private Long template;

}
