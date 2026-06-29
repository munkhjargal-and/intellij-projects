package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    private static final java.util.regex.Pattern testPattern = java.util.regex.Pattern.compile("^(ASC|DESC)$");

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
    public SomeDto<WaterBottle> filterPages(
            @Valid
            @RestQuery
            @DefaultValue("0")
            @Min(value = 0, message = "page value must be 0 or greater")
            Integer page,

            @Valid
            @RestQuery
            @DefaultValue("100")
            @Min(value = 1, message = "pageSize value must be 1 or greater")
            Integer pageSize,

            @RestQuery
            @DefaultValue("barcode")
            @Pattern(regexp = "^(brand|capacity|barcode|id)$", message = "sortBy must be either brand, capacity, barcode, or id")
            String sortBy,

            @RestQuery
            @DefaultValue("ASC")
            @Pattern(regexp = "^(ASC|DESC)$", message = "sortMode must be either ASC or DESC")
            String sortMode,

            @Valid
            @RestQuery
            @Pattern(regexp = "^(brand|capacity|barcode|id)$", message = "filterBy must be either brand, capacity, barcode, or id")
            String filterBy,

            @Valid
            @RestQuery
            String filterVal
    ){

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
            var filterByD = !filterBy.equals("id");
            if(filterByA && filterByB && filterByC && filterByD){
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