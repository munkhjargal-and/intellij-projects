package mn.water.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import mn.water.dto.SomeDto;
import mn.water.dto.WaterBottleDto;
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

    @Transactional
    public WaterBottle findOne(Long id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<WaterBottle> findOneQuery = cb.createQuery(WaterBottle.class);
            Root<WaterBottle> root1 = findOneQuery.from(WaterBottle.class);
            findOneQuery.select(root1).where(cb.equal(root1.get("id"), id));
            TypedQuery<WaterBottle> realFindOne = em.createQuery(findOneQuery);
            WaterBottle oneBottle;
            oneBottle = realFindOne.getSingleResult();
            return oneBottle;
        } catch (NoSuchElementException e) {
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
    public List<WaterBottle> findBottlesByVendor(Vendor vendor) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<WaterBottle> findBottlesByVendorQuery = cb.createQuery(WaterBottle.class);
            Root<WaterBottle> root1 = findBottlesByVendorQuery.from(WaterBottle.class);
            findBottlesByVendorQuery.select(root1).where(cb.equal(root1.get("vendor"), vendor));
            TypedQuery<WaterBottle> realFindBottlesByVendor = em.createQuery(findBottlesByVendorQuery);
            List<WaterBottle> bottlesByVendor;
            bottlesByVendor = realFindBottlesByVendor.getResultList();
            return bottlesByVendor;

        } catch (RuntimeException e) {
            throw new NotFoundException(e);
        }
    }

    @Transactional
    public SomeDto<WaterBottle> filterPage(
            int page,
            int pageSize,
            String sortBy,
            String sortMode,
            String filterBy,
            String filterVal
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // =========================
        // DATA QUERY
        // =========================

        CriteriaQuery<WaterBottle> dataQuery =
                cb.createQuery(WaterBottle.class);

        Root<WaterBottle> bottle =
                dataQuery.from(WaterBottle.class);

        // JOIN WaterBottle -> Vendor
        Join<WaterBottle, Vendor> vendor =
                bottle.join("vendor", JoinType.LEFT);

        // Decide which database field to sort by
        Expression<?> sortExpression;

        if ("vendorId".equals(sortBy)) {
            sortExpression = vendor.get("id");
        } else if ("vendorName".equals(sortBy)) {
            sortExpression = vendor.get("name");
        } else {
            sortExpression = bottle.get(sortBy);
        }

        // Sort
        if ("DESC".equalsIgnoreCase(sortMode)) {
            dataQuery.orderBy(cb.desc(sortExpression));
        } else {
            dataQuery.orderBy(cb.asc(sortExpression));
        }

        // Filter
        if (filterBy != null && !filterBy.isBlank()
                && filterVal != null && !filterVal.isBlank()) {

            Expression<?> filterExpression;

            if ("vendorId".equals(filterBy)) {
                filterExpression = vendor.get("id");
            } else if ("vendorName".equals(filterBy)) {
                filterExpression = vendor.get("name");
            } else {
                filterExpression = bottle.get(filterBy);
            }

            dataQuery.where(
                    cb.equal(filterExpression, filterVal)
            );
        }

        dataQuery.select(bottle);

        TypedQuery<WaterBottle> query =
                em.createQuery(dataQuery);

        query.setMaxResults(pageSize);
        query.setFirstResult(page * pageSize);

        List<WaterBottle> dataFromDb =
                query.getResultList();


        // =========================
        // COUNT QUERY
        // =========================

        CriteriaQuery<Long> countQuery =
                cb.createQuery(Long.class);

        Root<WaterBottle> countBottle =
                countQuery.from(WaterBottle.class);

        if (filterBy != null && !filterBy.isBlank()
                && filterVal != null && !filterVal.isBlank()) {

            Join<WaterBottle, Vendor> countVendor =
                    countBottle.join("vendor", JoinType.LEFT);

            Expression<?> countFilterExpression;

            if ("vendorId".equals(filterBy)) {
                countFilterExpression = countVendor.get("id");
            } else if ("vendorName".equals(filterBy)) {
                countFilterExpression = countVendor.get("name");
            } else {
                countFilterExpression = countBottle.get(filterBy);
            }

            countQuery
                    .select(cb.count(countBottle))
                    .where(cb.equal(
                            countFilterExpression,
                            filterVal
                    ));

        } else {
            countQuery.select(cb.count(countBottle));
        }

        Long countFromDb =
                em.createQuery(countQuery).getSingleResult();

        return new SomeDto<>(
                page,
                pageSize,
                countFromDb.intValue(),
                dataFromDb
        );
    }
}