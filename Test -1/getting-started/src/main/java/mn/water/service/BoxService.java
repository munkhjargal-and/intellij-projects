package mn.water.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.BoxDto;
import mn.water.dto.SomeDto;
import mn.water.entity.Box;
import mn.water.repository.BoxRepository;
import java.util.List;

@ApplicationScoped
public class BoxService {

    @Inject
    BoxRepository repository;

    @Inject
    BoxRepository boxRepository;
    @Transactional
    public BoxDto createBox(BoxDto dto) {

        Box box = new Box();
        box.setLength(dto.getLength());
        box.setHeight(dto.getHeight());
        box.setWidth(dto.getWidth());
        repository.persist(box);

        dto.setId(box.getId());

        return dto;
    }

    public List<Box> getAll() {
        return repository.findAll();
    }

    @Transactional
    public BoxDto updateBox(Long id, BoxDto dto) {

        Box box = repository.findById(id);

        if (box == null) {
            throw new NotFoundException("box not found");
        }

        box.setLength(dto.getLength());
        box.setWidth(dto.getWidth());
        box.setHeight(dto.getHeight());
        repository.update(box);

        dto.setId(id);

        return dto;
    }

    @Transactional
    public void deleteBox(Long id) {

        Box box = repository.findById(id);

        if (box == null) {
            throw new NotFoundException("Box not found");
        }

        repository.delete(box);
    }

    @Transactional
    public Box getOne(Long id) {
        return repository.findOne(id);
    }

    @Transactional
    public Float getVolume(Long id) {
        Box box = this.getOne(id);
        return box.getHeight() * box.getWidth() * box.getLength();
    }

    public SomeDto<Box> getPage(int page, int pageSize, String sortBy,String sortMode) {
        return boxRepository.findPage(page, pageSize, sortBy, sortMode);
    }
    public SomeDto<Box> getPage1(int page, int pageSize, String sortBy,String sortMode, String filterBy, String filterVal) {
        return boxRepository.filterPage(page, pageSize, sortBy, sortMode, filterBy, filterVal);
    }
}