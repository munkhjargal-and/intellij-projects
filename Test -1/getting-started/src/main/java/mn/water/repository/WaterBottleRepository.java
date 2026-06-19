package mn.water.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.SomeDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WaterBottle> findAllQuery = cb.createQuery(WaterBottle.class);
        Root<WaterBottle> f1 = findAllQuery.from(WaterBottle.class);
        findAllQuery.select(f1);
        TypedQuery<WaterBottle> realFindAll = em.createQuery(findAllQuery);
        List<WaterBottle> allBottles;
        allBottles = realFindAll.getResultList();
        return allBottles;
    }
    @Transactional
    public WaterBottle findOne(Long id){
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<WaterBottle> findOneQuery = cb.createQuery(WaterBottle.class);
            Root<WaterBottle> f1 = findOneQuery.from(WaterBottle.class);
            findOneQuery.select(f1).where(cb.equal(f1.get("id"), id));
            TypedQuery<WaterBottle> realFindOne = em.createQuery(findOneQuery);
            WaterBottle oneBottle;
            oneBottle = realFindOne.getSingleResult();
            return oneBottle;
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

    @Transactional
    public List<WaterBottle> findBottlesByVendor(Vendor vendor){
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<WaterBottle> findBottlesByVendorQuery = cb.createQuery(WaterBottle.class);
            Root<WaterBottle> f1 = findBottlesByVendorQuery.from(WaterBottle.class);
            findBottlesByVendorQuery.select(f1).where(cb.equal(f1.get("vendor"), vendor));
            TypedQuery<WaterBottle> realFindBottlesByVendor = em.createQuery(findBottlesByVendorQuery);
            List<WaterBottle> bottlesByVendor;
            bottlesByVendor = realFindBottlesByVendor.getResultList();
            return bottlesByVendor;

        } catch (RuntimeException e) {
            throw new NotFoundException(e);
        }
    }

    @Transactional
    public SomeDto<WaterBottle> findPage(int page, int pageSize, String sortBy, String sortMode){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WaterBottle> dataQuery = cb.createQuery(WaterBottle.class);
        Root<WaterBottle> f1 = dataQuery.from(WaterBottle.class);
        dataQuery = dataQuery.select(f1);
        if(Objects.equals(sortMode, "DESC")){
            dataQuery.orderBy(
                    cb.desc(f1.get(sortBy))
            );
        }
        else if (Objects.equals(sortMode, "ASC")){
            dataQuery.orderBy(
                    cb.asc(f1.get(sortBy))
            );
        }
        else{
            dataQuery.orderBy(
                    cb.asc(f1.get(sortBy))
            );
        }

        TypedQuery<WaterBottle> realDataQuery = em.createQuery(dataQuery);
        List<WaterBottle> dataFromDb = realDataQuery.getResultList();

        CriteriaQuery<Long> countCriteriaQuery = cb.createQuery(Long.class);
        Root<WaterBottle> f2 = countCriteriaQuery.from(WaterBottle.class);
        countCriteriaQuery.select(cb.count(f2));
        TypedQuery<Long> realQuery = em.createQuery(countCriteriaQuery);
        Long countFromDb = realQuery.getResultList().getFirst();

        return new SomeDto<>(page, pageSize, countFromDb.intValue(), dataFromDb);
    }
}