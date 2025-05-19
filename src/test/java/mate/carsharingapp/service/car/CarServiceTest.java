package mate.carsharingapp.service.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.carsharingapp.config.TestUtil;
import mate.carsharingapp.dto.car.CarDetailsInfoDto;
import mate.carsharingapp.dto.car.CarDto;
import mate.carsharingapp.dto.car.CreateCarRequestDto;
import mate.carsharingapp.exception.EntityNotFoundException;
import mate.carsharingapp.mapper.CarMapper;
import mate.carsharingapp.model.Car;
import mate.carsharingapp.repository.car.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @InjectMocks
    private CarServiceImpl carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;

    @DisplayName("Verify save() method works")
    @Test
    void save_ValidCarRequestDto_ReturnCarDto() {
        CreateCarRequestDto requestDto = TestUtil.createCarRequestDto();
        Car car = TestUtil.createFirstCar();
        CarDetailsInfoDto carDetailsInfoDto = TestUtil.createCarDetailsInfoDto();

        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDetailsInfoDto(car)).thenReturn(carDetailsInfoDto);

        CarDetailsInfoDto actual = carService.save(requestDto);

        assertThat(actual).isEqualTo(carDetailsInfoDto);
        verify(carMapper).toModel(requestDto);
        verify(carRepository).save(car);
        verify(carMapper).toDetailsInfoDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @DisplayName("Verify findAll() method works")
    @Test
    void findAll_ValidPageable_ReturnListOfAllCars() {
        Car car = TestUtil.createFirstCar();
        CarDto carDto = TestUtil.createCarDto();

        Pageable pageable = PageRequest.of(0, 10);
        List<Car> bookList = List.of(car);
        Page<Car> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        when(carRepository.findAll(pageable)).thenReturn(bookPage);
        when(carMapper.toDto(car)).thenReturn(carDto);

        List<CarDto> actual = carService.findAll(pageable);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(carDto);
        verify(carRepository).findAll(pageable);
        verify(carMapper).toDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @DisplayName("Verify findById() method works with existing car id")
    @Test
    void findById_ExistingId_ReturnCarDetailsInfoDto() {
        Long carId = 1L;
        Car car = TestUtil.createFirstCar();
        CarDetailsInfoDto carDetailsInfoDto = TestUtil.createCarDetailsInfoDto();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toDetailsInfoDto(car)).thenReturn(carDetailsInfoDto);

        CarDetailsInfoDto actual = carService.findById(carId);

        assertNotNull(actual);
        assertEquals(carDetailsInfoDto, actual);
        verify(carRepository).findById(carId);
        verify(carMapper).toDetailsInfoDto(car);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify findById() method with non existing id, method throw exception")
    void findById_NonExistingId_ThrowException() {
        Long carId = 99L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        String expected = "Can't find car by id: " + carId;
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> carService.findById(carId));
        assertEquals(expected, actual.getMessage());
        verify(carRepository).findById(carId);
        verifyNoMoreInteractions(carRepository);
    }

    @DisplayName("Verify updateById() method with existing id")
    @Test
    void updateById_ValidCreateCarRequestDtoAndCorrectId_ReturnCarDto() {
        CreateCarRequestDto requestDto = new CreateCarRequestDto()
                .setModel("Passat")
                .setBrand("Volkswagen")
                .setType("SEDAN")
                .setInventory(7)
                .setDailyFee(BigDecimal.valueOf(129));

        Car carForUpdating = TestUtil.createFirstCar();
        carForUpdating.setModel("Passat");
        CarDetailsInfoDto carDetailsInfoDto = TestUtil.createCarDetailsInfoDto();
        carDetailsInfoDto.setModel("Passat");
        Long carId = 1L;

        doNothing().when(carMapper).updateCarFromDto(requestDto, carForUpdating);
        when(carRepository.findById(carId)).thenReturn(Optional.of(carForUpdating));
        when(carMapper.toDetailsInfoDto(carForUpdating)).thenReturn(carDetailsInfoDto);

        CarDetailsInfoDto actual = carService.updateById(requestDto, carId);

        assertThat(actual).isEqualTo(carDetailsInfoDto);
        verify(carMapper).updateCarFromDto(requestDto, carForUpdating);
        verify(carRepository).findById(carId);
        verifyNoMoreInteractions(carRepository, carMapper);
    }

    @Test
    @DisplayName("Verify updateById() method with non existing id, method throw exception")
    void updateById_NonExistingId_throwException() {
        Long carId = 99L;
        CreateCarRequestDto requestDto = TestUtil.createCarRequestDto();

        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        String expected = "Can't find car by id: " + carId;
        EntityNotFoundException actual = assertThrows(EntityNotFoundException.class,
                () -> carService.updateById(requestDto, carId));
        assertEquals(expected, actual.getMessage());
        verify(carRepository).findById(carId);
        verifyNoMoreInteractions(carRepository);
    }

    @DisplayName("Verify deleteById() method with correct id")
    @Test
    void deleteById_CorrectId_DeleteCar() {
        Long carId = 1L;
        doNothing().when(carRepository).deleteById(carId);

        carService.deleteById(carId);

        verify(carRepository).deleteById(carId);
        verifyNoMoreInteractions(carRepository);
    }
}
