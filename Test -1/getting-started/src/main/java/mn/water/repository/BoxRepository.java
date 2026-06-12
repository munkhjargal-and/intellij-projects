package mn.water.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;
import mn.water.entity.Box;
import java.util.List;
import java.util.NoSuchElementException;

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
    public List<Box> findAll() {
        return em.createQuery(
                "SELECT b FROM Box b",
                Box.class
        ).getResultList();
    }

    public Box findOne(Long id) {
        try{
            return em.createQuery("SELECT b FROM Box b WHERE id = :id",
                            Box.class
                    ).setParameter("id", id)
                    .getResultList().getFirst();
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


}