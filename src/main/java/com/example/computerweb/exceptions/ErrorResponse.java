    package com.example.computerweb.exceptions;

    import com.fasterxml.jackson.annotation.JsonFormat;
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

        private int status;
        private String error;

        private String path;

        private String message;


    }
