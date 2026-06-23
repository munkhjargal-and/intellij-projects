package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mn.water.dto.SomeDto;
import mn.water.dto.WaterBottleDto;
import mn.water.entity.WaterBottle;
import mn.water.repository.WaterBottleRepository;
import mn.water.service.WaterBottleService;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/water-bottles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WaterBottleResource {
    private static final Logger log = LoggerFactory.getLogger(WaterBottleResource.class);

    @Inject
    WaterBottleService service;
    @Inject
    WaterBottleRepository waterBottleRepository;

    @ServerExceptionMapper
    public RestResponse<String> mapException(WebApplicationException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, x.getMessage());
    }

    @GET
    @Path("/{id}")
    public WaterBottle getOne(
            @PathParam("id") Long id
    ) {
        return service.getOne(id);
    }
    @GET
    @Path("filter-pages")
    public SomeDto<WaterBottle> filterPages(
            @RestQuery @DefaultValue("0") Integer page,
            @RestQuery @DefaultValue("100") Integer pageSize,
            @RestQuery @DefaultValue("barcode") String sortBy,
            @RestQuery @DefaultValue("ASC") String sortMode,
            @RestQuery String filterBy,
            @RestQuery String filterVal
    ){

        if(page < 0){
            throw new BadRequestException("Enter A Valid PageNumber");
        }
        if(pageSize < 0){
            throw new BadRequestException("Enter A Valid PageSize");
        }

        var sortByA = !sortBy.equals(("brand"));
        var sortByB = !sortBy.equals("capacity");
        var sortByC = !sortBy.equals("barcode");
        if(sortByA && sortByB && sortByC) {
            throw new BadRequestException("Enter A Valid Value For SortBy");
        }
        var sortModeA = !sortMode.equals("ASC");
        var sortModeB = !sortMode.equals("DESC");
        if(sortModeA && sortModeB){
            throw new BadRequestException("Enter A Valid Value For SortMode");
        }
        if(filterBy != null && filterVal == null){
            throw new BadRequestException("Enter a Value For FilterVal");
        }
        if(filterBy == null && filterVal != null){
            throw new BadRequestException("Enter a Value For FilterBy");
        }
        if(filterBy != null && filterVal != null){
            var filterByA = !filterBy.equals("brand");
            var filterByB = !filterBy.equals("capacity");
            var filterByC = !filterBy.equals("barcode");
            if(filterByA && filterByB && filterByC){
                throw new BadRequestException("Enter A Valid Value For FilterBy");
            }
        }
        return service.getPage(page, pageSize, sortBy, sortMode, filterBy, filterVal);
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
    public void deleteBottle(
            @PathParam("id") Long id
    ){
        service.deleteBottle(id);
    }
}