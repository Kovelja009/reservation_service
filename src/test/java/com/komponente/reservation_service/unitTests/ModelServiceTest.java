package com.komponente.reservation_service.unitTests;

import com.komponente.reservation_service.dto.ModelDto;
import com.komponente.reservation_service.exceptions.NotFoundException;
import com.komponente.reservation_service.mapper.VehicleMapper;
import com.komponente.reservation_service.model.Model;
import com.komponente.reservation_service.model.Type;
import com.komponente.reservation_service.repository.ModelRepository;
import com.komponente.reservation_service.repository.TypeRepository;
import com.komponente.reservation_service.service.impl.ModelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ModelServiceTest {
    @MockBean
    private ModelRepository modelRepo;

    @MockBean
    private TypeRepository typeRepo;

    @MockBean
    private VehicleMapper vehicleMapper;

    @Autowired
    ModelServiceImpl modelService;


    @Test
    public void addModelTestNoType(){
        ModelDto modelDto = new ModelDto();


        when(typeRepo.findByType("type")).thenReturn(Optional.empty());
        when(modelRepo.findByModel("model")).thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class, () -> modelService.addModel(modelDto));
        assertEquals("Type null does not exist", e.getMessage());

        verify(modelRepo).findByModel(any());
        verify(modelRepo, never()).save(any());
        verify(vehicleMapper, never()).modelDtoToModel(any());
    }

    @Test
    public void addModelalreadyExistTest(){
        ModelDto modelDto = new ModelDto();
        modelDto.setModel("model");
        modelDto.setType("type");

        Model model = new Model();
        model.setModel("model");
        Type type = new Type();
        type.setType("type");

        when(typeRepo.findByType("type")).thenReturn(Optional.of(type));
        when(modelRepo.findByModel("model")).thenReturn(Optional.of(model));

        Exception e = assertThrows(IllegalArgumentException.class, () -> modelService.addModel(modelDto));
        assertEquals("Model model already exists", e.getMessage());

        verify(modelRepo).findByModel(any());
        verify(modelRepo, never()).save(any());
        verify(vehicleMapper, never()).modelDtoToModel(any());

    }

    @Test
    public void addModelTest(){
        ModelDto modelDto = new ModelDto();
        modelDto.setModel("model");
        modelDto.setType("type");

        Model model = new Model();
        model.setModel("model");
        Type type = new Type();
        type.setType("type");

        when(typeRepo.findByType("type")).thenReturn(Optional.of(type));
        when(modelRepo.findByModel("model")).thenReturn(Optional.empty());
        when(vehicleMapper.modelDtoToModel(modelDto)).thenReturn(model);

        ModelDto test = modelService.addModel(modelDto);
        assertEquals(test, modelDto);

        verify(modelRepo).findByModel(any());
        verify(modelRepo).save(any());
        verify(vehicleMapper).modelDtoToModel(any());
    }


}
