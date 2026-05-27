package com.zafta.client.agent.mappers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenericMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMappings() {

    }

    // Convert entity to DTO
    public <D, T> D convertToDto(T entity, Class<D> dtoClass) {
        return modelMapper.map(entity, dtoClass);
    }

    // Convert DTO to entity
    public <D, T> T convertToEntity(D dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    // Convert list of entities to list of DTOs
    public <D, T> List<D> convertToDtoList(List<T> entities, Class<D> dtoClass) {
        return entities.stream()
                .map(entity -> convertToDto(entity, dtoClass))
                .collect(Collectors.toList());
    }

    // Convert list of DTOs to list of entities
    public <D, T> List<T> convertToEntityList(List<D> dtos, Class<T> entityClass) {
        return dtos.stream()
                .map(dto -> convertToEntity(dto, entityClass))
                .collect(Collectors.toList());
    }
}
