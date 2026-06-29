package mn.water.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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
    public SomeDto<Vendor> filterPages(
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
            @DefaultValue("registrationNumber")
            @Pattern(regexp = "^(registrationNumber|contractSignedDate|getContractEndDate|name|id)$", message = "sortBy must be either registrationNumber, contractSignedDate, getContractEndDate, name, or id")
            String sortBy,

            @RestQuery
            @DefaultValue("ASC")
            @Pattern(regexp = "^(ASC|DESC)$", message = "sortMode must be either ASC or DESC")
            String sortMode,

            @Valid
            @RestQuery
            @Pattern(regexp = "^(registrationNumber|name|id)$", message = "filterBy must be either registrationNumber, name, or id")
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
            var filterByA = !filterBy.equals(("registrationNumber"));
            var filterByB = !filterBy.equals("name");
            var filterByC = !filterBy.equals("id");
            if(filterByA && filterByB && filterByC) {
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
