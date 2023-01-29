package ru.practicum.stat.hit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HitDtoReq {
    @NotBlank(message = "Empty app")
    private String app;

    @NotBlank(message = "Empty uri")
    private String uri;

    @NotNull(message = "Empty ip")
    @Pattern(regexp = "^(\\d{1,3}\\.){3}\\d{1,3}$", message = "Incorrect ip address")
    private String ip;

    @NotNull(message = "Empty timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
