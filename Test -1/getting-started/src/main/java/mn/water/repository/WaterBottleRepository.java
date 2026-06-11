package mn.water.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
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


}