package mn.water.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.WaterBottleDto;
import mn.water.entity.WaterBottle;
import mn.water.repository.WaterBottleRepository;
import java.util.List;
import java.util.NoSuchElementException;

@ApplicationScoped
public class WaterBottleService {

    @Inject
    WaterBottleRepository repository;

    @Transactional
    public WaterBottleDto createBottle(WaterBottleDto dto) {

        WaterBottle bottle = new WaterBottle();
        bottle.setBrand(dto.getBrand());
        bottle.setCapacity(dto.getCapacity());

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
}