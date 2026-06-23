package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mn.water.dto.SomeDto;
import mn.water.dto.VendorDto;
import mn.water.entity.Vendor;
import mn.water.entity.WaterBottle;
import mn.water.service.VendorService;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

@Path("/vendors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VendorResource {
    @Inject
    VendorService service;
    @ServerExceptionMapper
    public RestResponse<String> mapException(WebApplicationException x) {
        return RestResponse.status(Response.Status.NOT_FOUND, x.getMessage());
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
    @Path("filter-pages")
    public SomeDto<Vendor> filterPages(
            @RestQuery @DefaultValue("0") Integer page,
            @RestQuery @DefaultValue("100") Integer pageSize,
            @RestQuery @DefaultValue("registrationNumber") String sortBy,
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
        var sortByA = !sortBy.equals(("registrationNumber"));
        var sortByB = !sortBy.equals("contractSignedDate");
        var sortByC = !sortBy.equals("getContractEndDate");
        var sortByD = !sortBy.equals("name");
        if(sortByA && sortByB && sortByC && sortByD) {
            throw new BadRequestException("Enter A Valid Value For SortBy");
        }
        var sortModeA = !sortMode.equals(("ASC"));
        var sortModeB = !sortMode.equals("DESC");
        if(sortModeA && sortModeB) {
            throw new BadRequestException("Enter A Valid Value For SortMode");
        }
        if(filterBy != null && filterVal != null){
            var filterByA = !filterBy.equals(("registrationNumber"));
            var filterByB = !filterBy.equals("name");
            if(filterByA && filterByB) {
                throw new BadRequestException("Enter A Valid Value For FilterBy");
            }
        }
        return service.getPage(page, pageSize, sortBy, sortMode, filterBy, filterVal);
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
    public void deleteVendor(
            @PathParam("id") Long id
    )
    {
        service.deleteVendor(id);
    }

}
