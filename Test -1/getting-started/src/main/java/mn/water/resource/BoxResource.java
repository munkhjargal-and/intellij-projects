package mn.water.resource;

import jakarta.inject.Inject;
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
    public List<Box> getAll() {
        return service.getAll();
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
    @Path("filter-pages")
    public SomeDto<Box> filterPages(
            @RestQuery @DefaultValue("0") Integer page,
            @RestQuery @DefaultValue("100") Integer pageSize,
            @RestQuery @DefaultValue("length") String sortBy,
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
        if(filterBy == null){
            throw new BadRequestException("Enter A Value For FilterBy");
        }
        if(filterVal == null){
            throw new BadRequestException("Enter A Value For FilterVal");
        }
        var sortByA = !sortBy.equals(("length"));
        var sortByB = !sortBy.equals("width");
        var sortByC = !sortBy.equals("height");
        if(sortByA && sortByB && sortByC) {
            throw new BadRequestException("Enter A Valid Value For SortBy");
        }
        var sortModeA = !sortMode.equals("ASC");
        var sortModeB = !sortMode.equals("DESC");
        if(sortModeA && sortModeB){
            throw new BadRequestException("Enter A Valid Value For SortMode");
        }
        var filterByA = !filterBy.equals("length");
        var filterByB = !filterBy.equals("width");
        var filterByC = !filterBy.equals("height");
        if(filterByA && filterByB && filterByC){
            throw new BadRequestException("Enter A Valid Value For FilterBy");
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