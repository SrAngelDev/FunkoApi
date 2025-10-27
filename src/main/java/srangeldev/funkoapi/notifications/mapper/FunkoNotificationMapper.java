package srangeldev.funkoapi.notifications.mapper;

import lombok.NoArgsConstructor;
import srangeldev.funkoapi.Funko.models.Funko;
import srangeldev.funkoapi.notifications.dto.FunkoNotificationDto;
import srangeldev.funkoapi.notifications.models.FunkoNotification;

@NoArgsConstructor
public class FunkoNotificationMapper {
    public static FunkoNotificationDto toDto(Funko funko) {
        var funkoNotification = new FunkoNotificationDto();

        funkoNotification.setId(funko.getId());
        funkoNotification.setUuid(funko.getUuid().toString());
        funkoNotification.setNombre(funko.getNombre());
        funkoNotification.setPrecio(funko.getPrecio());
        funkoNotification.setCategoria(funko.getCategoria().toString());
        funkoNotification.setFechaLanzamiento(funko.getFechaLanzamiento().toString());
        funkoNotification.setCreatedAt(funko.getCreatedAt().toString());
        funkoNotification.setUpdatedAt(funko.getUpdatedAt().toString());

        return funkoNotification;
    }
}
