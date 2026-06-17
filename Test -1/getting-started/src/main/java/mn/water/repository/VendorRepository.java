package mn.water.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.VendorSomeDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;

import java.util.List;
import java.util.NoSuchElementException;

@ApplicationScoped
public class VendorRepository {

    @Inject
    EntityManager em;

    public void persist(Vendor vendor) {em.persist(vendor);}

    public Vendor findById(Long id) {return em.find(Vendor.class, id);}

    public List<Vendor> findAll() {
        return em.createQuery(
                "SELECT v FROM Vendor v",
                Vendor.class
        ).getResultList();
    }

    public Vendor findOne(Long id) {
        try{
            return em.createQuery("SELECT v FROM Vendor v WHERE id = :id",
                    Vendor.class
                    ).setParameter("id", id)
                    .getResultList().getFirst();
        } catch (NoSuchElementException e) {
            throw new NotFoundException(e);
        }
    }

    public void update(Vendor vendor) {
        em.merge(vendor);
    }

    public void delete(Vendor vendor) {
        em.remove(vendor);
    }

    public List<WaterBottle> findBottles(Long id){
        return em.createQuery(
                "SELECT w FROM WaterBottle w",
                WaterBottle.class
        ).getResultList();
    }

    public VendorSomeDto findPage(int page, int pageSize) {
        TypedQuery<Long> countQuery = em.createQuery(
                "SELECT COUNT(v) FROM Vendor v",
                Long.class
        );
        TypedQuery<Vendor> dataQuery = em.createQuery(
                "SELECT v FROM Vendor v",
                Vendor.class
        ).setMaxResults(pageSize).setFirstResult(Math.multiplyExact(page, pageSize));
        return new VendorSomeDto(page, pageSize, countQuery.getFirstResult(), dataQuery.getResultList());
    }
}
