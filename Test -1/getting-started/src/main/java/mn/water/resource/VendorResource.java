package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import mn.water.dto.SomeDto;
import mn.water.dto.VendorDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;
import mn.water.service.VendorService;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;

@Path("/vendors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendorResource {
    @Inject
    VendorService service;

    @GET
    public List<Vendor> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public Vendor getOne(
            @PathParam("id") Long id
    ){
        return  service.getOne(id);
    }

    @GET
    @Path("/{id}/bottles")
    public List<WaterBottle> findBottles(
            @PathParam("id") Long id
    ) {
        Vendor vendor = this.getOne(id);
        return service.findBottles(vendor);
    }

    @GET
    @Path("total-pages")
    public SomeDto<Vendor> somePages(
            @RestQuery int page,
            @RestQuery int pageSize,
            @RestQuery String sortBy,
            @RestQuery String sortMode
    ){
        return service.getPage(page, pageSize, sortBy, sortMode);
    }


    @POST
    public VendorDto createVendor(VendorDto dto) {
        return service.createVendor(dto);
    }

    @PUT
    @Path("/{id}")
    public VendorDto updateVendor(
            @PathParam("id") Long id,
            VendorDto dto
    ){
        return  service.updateVendor(id, dto);
    }

    @DELETE
    @Path(("/{id}"))
    public void deleteVendor(@PathParam("id") Long id) {
        service.deleteVendor(id);
    }

}
