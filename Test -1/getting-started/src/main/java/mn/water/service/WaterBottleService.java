package mn.water.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.WaterBottleDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;
import mn.water.repository.WaterBottleRepository;
import java.util.List;

@ApplicationScoped
public class WaterBottleService {

    @Inject
    WaterBottleRepository repository;
    @Inject
    WaterBottleRepository waterBottleRepository;

    @Transactional
    public WaterBottleDto createBottle(WaterBottleDto dto) {

        WaterBottle bottle = new WaterBottle();
        bottle.setBrand(dto.getBrand());
        bottle.setCapacity(dto.getCapacity());
        bottle.setBarcode(dto.getBarcode());

        repository.persist(bottle);

        dto.setId(bottle.getId());

        return dto;
    }

    public List<WaterBottle> getAll() {
        return repository.findAll();
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

    @Transactional
    public WaterBottle getOne(Long id) {
        return repository.findOne(id);
    }

    public List<WaterBottle> getBottlesByVendor(Vendor vendor) {
        return waterBottleRepository.findBottlesByVendor(vendor);
    }
}