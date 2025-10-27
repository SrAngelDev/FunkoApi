package srangeldev.funkoapi.notifications.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FunkoNotification<T>{
    String entidad;
    Tipo tipo;
    T data;
    LocalDateTime createdAt;
}
