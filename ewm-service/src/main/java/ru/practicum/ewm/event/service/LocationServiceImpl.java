package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dao.LocationRepository;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Location;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public Location addLocation(LocationDto locationDto) {
        var location = locationRepository.save(locationMapper.toLocation(locationDto));
        log.debug("Location saved: {}", location);
        return location;
    }
}
