package mn.water.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.SomeDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;

import java.util.List;
import java.util.NoSuchElementException;

@ApplicationScoped
public class WaterBottleRepository {

    @Inject
    EntityManager em;

    public void persist(WaterBottle bottle) {
        em.persist(bottle);
    }

    public WaterBottle findById(Long id) {
        return em.find(WaterBottle.class, id);
    }

    public List<WaterBottle> findAll() {
        return em.createQuery(
                "SELECT w FROM WaterBottle w",
                WaterBottle.class
        ).getResultList();
    }

    public WaterBottle findOne(Long id) {
        try{
            return em.createQuery("SELECT w FROM WaterBottle w WHERE id = :id",
                            WaterBottle.class
                    ).setParameter("id", id)
                    .getResultList().getFirst();
        } catch (NoSuchElementException e){
            throw new NotFoundException(e);
        }
    }

    public void update(WaterBottle bottle) {
        em.merge(bottle);
    }

    public void delete(WaterBottle bottle) {
        em.remove(bottle);
    }


    public List<WaterBottle> findBottlesByVendor(Vendor vendor) {
        try{
            return em.createQuery(
                    "SELECT w FROM WaterBottle w WHERE vendor = :vendor",
                    WaterBottle.class
            ).setParameter("vendor", vendor).getResultList();
        } catch (NoSuchElementException e){
            throw new NotFoundException(e);
        }
    }
/*
    public SomeDto findSome() {
        TypedQuery<WaterBottle> query = em.createQuery(
                "SELECT w FROM WaterBottle w",
                WaterBottle.class
        );
        query.setMaxResults(20);
        List<WaterBottle> x = query.getResultList();
        System.out.println(x.getFirst().toString());
        return new SomeDto(0, 20, 1000000, x);
    }
*/
    public SomeDto<WaterBottle> findPage(int page, int pageSize) {
        TypedQuery<Long> countQuery = em.createQuery(
                "SELECT COUNT(w) FROM WaterBottle w",
                Long.class
        );
        TypedQuery<WaterBottle> dataQuery = em.createQuery(
                "SELECT w FROM WaterBottle w",
                WaterBottle.class
        ).setMaxResults(pageSize).setFirstResult(Math.multiplyExact(page, pageSize));
        return new SomeDto<WaterBottle>(page, pageSize, countQuery.getFirstResult(), dataQuery.getResultList());
    }
}