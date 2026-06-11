package mn.box.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import mn.box.dto.BoxDto;
import mn.box.entity.Box;
import mn.box.service.BoxService;

import java.util.List;

@Path("/boxes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BoxResource {

    @Inject
    BoxService service;

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