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
import mn.water.entity.Box;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@ApplicationScoped
public class BoxRepository {

    @Inject
    EntityManager em;

    public void persist(Box box) {
        em.persist(box);
    }

    public Box findById(Long id) {
        return em.find(Box.class, id);
    }

    @Transactional
    public List<Box> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Box> findAllQuery = cb.createQuery(Box.class);
        Root<Box> f1 = findAllQuery.from(Box.class);
        findAllQuery.select(f1);
        TypedQuery<Box> realFindAll = em.createQuery(findAllQuery);
        List<Box> allBottles;
        allBottles = realFindAll.getResultList();
        return allBottles;
    }

    @Transactional
    public Box findOne(Long id){
        try{
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Box> findOneQuery = cb.createQuery(Box.class);
            Root<Box> f1 = findOneQuery.from(Box.class);
            findOneQuery.select(f1).where(cb.equal(f1.get("id"), id));
            TypedQuery<Box> realFindOne = em.createQuery(findOneQuery);
            Box oneBottle;
            oneBottle = realFindOne.getSingleResult();
            return oneBottle;
        } catch (NoSuchElementException e){
            throw new NotFoundException(e);
        }
    }
    public void update(Box box) {
        em.merge(box);
    }

    public void delete(Box box) {
        em.remove(box);
    }

    @Transactional
    public SomeDto<Box> findPage(int page, int pageSize, String sortBy, String sortMode){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Box> dataQuery = cb.createQuery(Box.class);
        Root<Box> f1 = dataQuery.from(Box.class);
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

        TypedQuery<Box> realDataQuery = em.createQuery(dataQuery);
        List<Box> dataFromDb = realDataQuery.getResultList();

        CriteriaQuery<Long> countCriteriaQuery = cb.createQuery(Long.class);
        Root<Box> f2 = countCriteriaQuery.from(Box.class);
        countCriteriaQuery.select(cb.count(f2));
        TypedQuery<Long> realQuery = em.createQuery(countCriteriaQuery);
        Long countFromDb = realQuery.getResultList().getFirst();

        return new SomeDto<>(page, pageSize, countFromDb.intValue(), dataFromDb);
    }
}