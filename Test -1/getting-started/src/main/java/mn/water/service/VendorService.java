package mn.water.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.VendorDto;
import mn.water.dto.VendorSomeDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;
import mn.water.repository.BoxRepository;
import mn.water.repository.VendorRepository;

import java.util.List;

@ApplicationScoped
public class VendorService {
    @Inject
    VendorRepository repository;
    @Inject
    WaterBottleService waterBottleService;
    @Inject
    BoxRepository boxRepository;
    @Inject
    VendorRepository vendorRepository;

    @Transactional
    public VendorDto createVendor(VendorDto dto) {
        Vendor vendor = new Vendor();
        vendor.setName(dto.getName());
        vendor.setRegistrationNumber(dto.getRegistrationNumber());
        vendor.setContractSignedDate(dto.getContractSignedDate());
        vendor.setGetContractEndDate(dto.getContractEndDate());

        repository.persist(vendor);
        dto.setId(vendor.getId());

        return dto;
    }

    public List<Vendor> getAll(){return repository.findAll();}

    @Transactional
    public VendorDto updateVendor(Long id, VendorDto dto) {
        Vendor vendor = repository.findById(id);
         if (vendor == null) {
             throw new NotFoundException("Vendor not found");
         }

         vendor.setName(dto.getName());
         vendor.setRegistrationNumber(dto.getRegistrationNumber());
         vendor.setContractSignedDate(dto.getContractSignedDate());
         vendor.setGetContractEndDate(dto.getContractEndDate());

         repository.update(vendor);
         dto.setId(id);
         return dto;
    }

    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = repository.findById(id);

        if(vendor == null) {
            throw new NotFoundException("Vendor no found");
        }
        repository.delete(vendor);
    }

    @Transactional
    public Vendor getOne(Long id) {
        return repository.findOne(id);
    }

    @Transactional
    public List<WaterBottle> findBottles(Vendor vendor) {
        return waterBottleService.getBottlesByVendor(vendor);
    }

    public VendorSomeDto getPage(int page, int pageSize) {
        return vendorRepository.findPage(page, pageSize);
    }
}
