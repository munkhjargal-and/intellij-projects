package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import mn.water.dto.SomeDto;
import mn.water.dto.WaterBottleDto;
import mn.water.entity.WaterBottle;
import mn.water.repository.WaterBottleRepository;
import mn.water.service.WaterBottleService;
import org.jboss.resteasy.reactive.RestQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/water-bottles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WaterBottleResource {
    private static final Logger log = LoggerFactory.getLogger(WaterBottleResource.class);

    @Inject
    WaterBottleService service;
    @Inject
    WaterBottleRepository waterBottleRepository;

    @GET
    public List<WaterBottle> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public WaterBottle getOne(
            @PathParam("id") Long id
    ) {
        return service.getOne(id);
    }

    @GET
    @Path("total-pages")
    public SomeDto<WaterBottle> somePages(
            @RestQuery int page,
            @RestQuery int pageSize,
            @RestQuery String sortBy,
            @RestQuery String sortMode
    ){
        return service.getPage(page, pageSize, sortBy, sortMode);
    }

    @POST
    public WaterBottleDto createBottle(WaterBottleDto dto) {
        return service.createBottle(dto);
    }
    @PUT
    @Path("/{id}")
    public WaterBottleDto updateBottle(
            @PathParam("id") Long id,
            WaterBottleDto dto) {

        return service.updateBottle(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void deleteBottle(@PathParam("id") Long id) {
        service.deleteBottle(id);
    }
}