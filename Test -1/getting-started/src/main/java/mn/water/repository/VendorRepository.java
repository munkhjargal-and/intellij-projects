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
public class VendorRepository {

    @Inject
    EntityManager em;

    public void persist(Vendor vendor) {em.persist(vendor);}

    public Vendor findById(Long id) {return em.find(Vendor.class, id);}

    @Transactional
    public List<Vendor> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vendor> findAllQuery = cb.createQuery(Vendor.class);
        Root<Vendor> f1 = findAllQuery.from(Vendor.class);
        findAllQuery.select(f1);
        TypedQuery<Vendor> realFindAll = em.createQuery(findAllQuery);
        List<Vendor> allBottles;
        allBottles = realFindAll.getResultList();
        return allBottles;
    }

    @Transactional
    public Vendor findOne(Long id){
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Vendor> findOneQuery = cb.createQuery(Vendor.class);
            Root<Vendor> f1 = findOneQuery.from(Vendor.class);
            findOneQuery.select(f1).where(cb.equal(f1.get("id"), id));
            TypedQuery<Vendor> realFindOne = em.createQuery(findOneQuery);
            Vendor oneBottle;
            oneBottle = realFindOne.getSingleResult();
            return oneBottle;
        } catch (NoSuchElementException e){
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<WaterBottle> findBottlesQuery = cb.createQuery(WaterBottle.class);
        Root<WaterBottle> f1 = findBottlesQuery.from(WaterBottle.class);
        findBottlesQuery.select(f1);
        TypedQuery<WaterBottle> realFindBottles = em.createQuery(findBottlesQuery);
        List<WaterBottle> realBottle;
        realBottle = realFindBottles.getResultList();
        return realBottle;
    }

    @Transactional
    public SomeDto<Vendor> findPage(int page, int pageSize, String sortBy, String sortMode){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vendor> dataQuery = cb.createQuery(Vendor.class);
        Root<Vendor> f1 = dataQuery.from(Vendor.class);
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

        TypedQuery<Vendor> realDataQuery = em.createQuery(dataQuery);
        List<Vendor> dataFromDb = realDataQuery.getResultList();

        CriteriaQuery<Long> countCriteriaQuery = cb.createQuery(Long.class);
        Root<Vendor> f2 = countCriteriaQuery.from(Vendor.class);
        countCriteriaQuery.select(cb.count(f2));
        TypedQuery<Long> realQuery = em.createQuery(countCriteriaQuery);
        Long countFromDb = realQuery.getResultList().getFirst();

        return new SomeDto<>(page, pageSize, countFromDb.intValue(), dataFromDb);
    }
}
