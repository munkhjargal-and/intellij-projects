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
    @Path("total-pages")
    public SomeDto<Box> somePages(
            @RestQuery Integer page,
            @RestQuery Integer pageSize,
            @RestQuery String sortBy,
            @RestQuery String sortMode
    ){

        if (page == null){
            throw new BadRequestException("Enter A Page Number");
        }
        if (pageSize == null){
            throw new BadRequestException("Enter A Page Size");
        }

        if(page < 0){
            throw new BadRequestException("Enter A Valid Page Number");
        }
        if(pageSize < 0){
            throw new BadRequestException("Enter A Valid Page Size");
        }
        if(sortBy == null || sortMode == null){
            throw new BadRequestException("Enter A Value For Each Parameter");
        }
        var condA = !sortBy.equals(("brand"));
        var condB = !sortBy.equals("capacity");
        var condC = !sortBy.equals("barcode");
        if(condA && condB && condC) {
            throw new BadRequestException("Enter A Valid Value For Each Parameter");
        }
        return service.getPage(page, pageSize, sortBy, sortMode);
    }
    @GET
    @Path("filter-pages")
    public SomeDto<Box> somePages(
            @RestQuery Integer page,
            @RestQuery Integer pageSize,
            @RestQuery String sortBy,
            @RestQuery String sortMode,
            @RestQuery String filterBy,
            @RestQuery String filterVal
    ){
        if (page == null){
            throw new BadRequestException("Enter A Page Number");
        }
        if (pageSize == null){
            throw new BadRequestException("Enter A Page Size");
        }

        if(page < 0){
            throw new BadRequestException("Enter A Valid Page Number");
        }
        if(pageSize < 0){
            throw new BadRequestException("Enter A Valid Page Size");
        }
        if(sortBy == null || sortMode == null || filterBy == null || filterVal == null){
            throw new BadRequestException("Enter A Value For Each Parameter");
        }
        var condA = !sortBy.equals(("brand"));
        var condB = !sortBy.equals("capacity");
        var condC = !sortBy.equals("barcode");
        if(condA && condB && condC) {
            throw new BadRequestException("Enter A Valid Value For Each Parameter");
        }

        return service.getPage1(page, pageSize, sortBy, sortMode, filterBy, filterVal);
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