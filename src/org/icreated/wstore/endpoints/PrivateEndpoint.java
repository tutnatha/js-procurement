package org.icreated.wstore.endpoints;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.icreated.wstore.bean.PriceListProduct;
import org.icreated.wstore.bean.ProductCategory;
import org.icreated.wstore.service.CatalogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

//@PermitAll
//@RolesAllowed({"ROLE_USER"})
//apa lagi yg harus diperbuat untuk hal sederhana ini..(:
@RolesAllowed({"USER"})
@Path("/private")
@Tag(name = "Private Catalog Services")
public class PrivateEndpoint {
    @Context
    CatalogService catalogService;

    @GET
    @Path("/userprodcategories/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Category Products by UserID", description = "Product Categories, active, not discontinued & not BOM")
    public List<ProductCategory> getUserProdCategories(
            @Parameter(description = "web or Email User ID", required = true)
            @PathParam("userId") String userID) {

        return catalogService.getUserProdCategories(userID);
    }

}