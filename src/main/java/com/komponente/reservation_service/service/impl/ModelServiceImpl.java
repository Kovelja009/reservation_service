package com.komponente.reservation_service.service.impl;

import com.komponente.reservation_service.dto.ModelDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.VehicleMapper;
import com.komponente.reservation_service.model.Model;
import com.komponente.reservation_service.model.Type;
import com.komponente.reservation_service.repository.ModelRepository;
import com.komponente.reservation_service.repository.TypeRepository;
import com.komponente.reservation_service.service.ModelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@AllArgsConstructor
@Service
public class ModelServiceImpl implements ModelService {
    private ModelRepository modelRepo;
    private TypeRepository typeRepo;
    private VehicleMapper vehicleMapper;

    @Override
    public ModelDto addModel(ModelDto modelDto) {
        Optional<Model> modelOptional = modelRepo.findByModel(modelDto.getModel());
        if(modelOptional.isPresent())
            throw new IllegalArgumentException("Model " + modelDto.getModel() + " already exists");

        typeRepo.findByType(modelDto.getType()).orElseThrow(() -> new NotFoundException("Type " + modelDto.getType() + " does not exist"));

        Model model = vehicleMapper.modelDtoToModel(modelDto);
        modelRepo.save(model);
        return modelDto;
    }
}
