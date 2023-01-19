package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.TypeDto;
import com.komponente.reservation_service.mapper.VehicleMapper;
import com.komponente.reservation_service.model.Type;
import com.komponente.reservation_service.repository.TypeRepository;
import com.komponente.reservation_service.service.TypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TypeServiceImpl implements TypeService {
    private VehicleMapper vehicleMapper;
    private TypeRepository typeRepo;

    @Override
    public TypeDto addType(TypeDto typDto) {
        Optional<Type> typeOptional = typeRepo.findByType(typDto.getType());
        if(typeOptional.isPresent())
            throw new IllegalArgumentException("Type " + typDto.getType() + " already exists");
        Type type = vehicleMapper.typeDtoToType(typDto);
        typeRepo.save(type);
        return typDto;
    }
}
