    package com.example.computerweb.exceptions;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.Date;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public class ErrorResponse {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime timestamp = LocalDateTime.now();
        @Schema(type = "int" , example = "500")
        private int status;
        private String error;

        private String path;

        private String message;


    }
