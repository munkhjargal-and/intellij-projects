package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mn.water.dto.BoxDto;
import mn.water.dto.SomeDto;
import mn.water.entity.Box;
import mn.water.service.BoxService;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

@Path("/boxes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BoxResource {

    @Inject
    BoxService service;
    @ServerExceptionMapper
    public RestResponse<String> mapException(WebApplicationException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, x.getMessage());
    }

    @GET
    @Path("/{id}")
    public Box getOne(
            @PathParam("id") Long id
    ) {
        return service.getOne(id);
    }

    @GET
    @Path("/{id}/volume")
    public Float findVolume(
            @PathParam("id") Long id
    ) {
        return service.getVolume(id);
    }
    @GET
    public SomeDto<Box> filterPages(
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
            @DefaultValue("length")
            @Pattern(regexp = "^(length|width|height|id)$", message = "sortBy must be either length, width, height, or id")
            String sortBy,

            @RestQuery
            @DefaultValue("ASC")
            @Pattern(regexp = "^(ASC|DESC)$", message = "sortMode must be either ASC or DESC")
            String sortMode,

            @Valid
            @RestQuery
            @Pattern(regexp = "^(length|width|height|id)$", message = "filterBy must be either length, width, height, or id")
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
            var filterByA = !filterBy.equals("length");
            var filterByB = !filterBy.equals("width");
            var filterByC = !filterBy.equals("height");
            var filterByD = !filterBy.equals("id");
            if(filterByA && filterByB && filterByC && filterByD){
                throw new BadRequestException("Enter A Valid Value For FilterBy");
            }
        }
        return service.getPage(page, pageSize, sortBy, sortMode, filterBy, filterVal);
    }
    @POST
    public BoxDto createBox(BoxDto dto) {
        return service.createBox(dto);
    }

    @PUT
    @Path("/{id}")
    public BoxDto updateBox(
            @PathParam("id") Long id,
            BoxDto dto) {

        return service.updateBox(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public void deleteBox(@PathParam("id") Long id) {
        service.deleteBox(id);
    }
}