package mn.water.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.SomeDto;
import mn.water.dto.WaterBottleDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;
import mn.water.repository.VendorRepository;
import mn.water.repository.WaterBottleRepository;
import java.util.List;

@ApplicationScoped
public class WaterBottleService {

    @Inject
    WaterBottleRepository repository;
    @Inject
    WaterBottleRepository waterBottleRepository;
    @Inject
    VendorRepository vendorRepository;

    @Transactional
    public WaterBottleDto createBottle(WaterBottleDto dto) {
        WaterBottle bottle = new WaterBottle();
        bottle.setBrand(dto.getBrand());
        bottle.setCapacity(dto.getCapacity());
        bottle.setBarcode(dto.getBarcode());

        Vendor vendor = vendorRepository.findById(dto.getVendorId());
        bottle.setVendor(vendor);

        repository.persist(bottle);

        dto.setId(bottle.getId());

        return dto;
    }

    @Transactional
    public WaterBottleDto updateBottle(Long id, WaterBottleDto dto) {

        WaterBottle bottle = repository.findById(id);

        if (bottle == null) {
            throw new NotFoundException("Bottle not found");
        }

        bottle.setBrand(dto.getBrand());
        bottle.setCapacity(dto.getCapacity());
        bottle.setBarcode(dto.getBarcode());

        Vendor vendor = vendorRepository.findById(dto.getVendorId());
        bottle.setVendor(vendor);
        waterBottleRepository.persist(bottle);
        repository.update(bottle);

        dto.setId(id);

        return dto;
    }

    @Transactional
    public void deleteBottle(Long id) {

        WaterBottle bottle = repository.findById(id);

        if (bottle == null) {
            throw new NotFoundException("Bottle not found");
        }

        repository.delete(bottle);
    }

    private WaterBottleDto toDto(WaterBottle bottle) {
        WaterBottleDto dto = new WaterBottleDto();

        dto.setId(bottle.getId());
        dto.setBrand(bottle.getBrand());
        dto.setCapacity(bottle.getCapacity());
        dto.setBarcode(bottle.getBarcode());

        if (bottle.getVendor() != null) {
            dto.setVendorId(bottle.getVendor().getId());
            dto.setVendorName(bottle.getVendor().getName());
        }

        return dto;
    }

    @Transactional
    public WaterBottleDto getOne(Long id) {

        WaterBottle bottle = repository.findOne(id);

        return toDto(bottle);
    }

    public List<WaterBottle> getBottlesByVendor(Vendor vendor) {
        return waterBottleRepository.findBottlesByVendor(vendor);
    }
    public SomeDto<WaterBottleDto> getPage(
            int page,
            int pageSize,
            String sortBy,
            String sortMode,
            String filterBy,
            String filterVal) {

        SomeDto<WaterBottle> result =
                waterBottleRepository.filterPage(page, pageSize, sortBy, sortMode, filterBy, filterVal);

        List<WaterBottleDto> dtoList = result.getData()
                .stream()
                .map(this::toDto)
                .toList();

        return new SomeDto<>(
                result.getPage(),
                result.getPageSize(),
                result.getTotal(),
                dtoList
        );
    }
}