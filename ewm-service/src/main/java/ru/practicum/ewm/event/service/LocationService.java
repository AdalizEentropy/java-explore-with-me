package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Location;

public interface LocationService {

    Location addLocation(LocationDto locationDto);
}
