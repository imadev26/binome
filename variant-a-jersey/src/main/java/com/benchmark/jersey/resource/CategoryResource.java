package com.benchmark.jersey.resource;

import com.benchmark.jersey.dto.PageResponse;
import com.benchmark.jersey.entity.Category;
import com.benchmark.jersey.service.CategoryService;
import com.benchmark.jersey.service.ItemService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;

/**
 * JAX-RS Resource for Category endpoints
 */
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    
    @Inject
    private CategoryService categoryService;
    
    @Inject
    private ItemService itemService;
    
    /**
     * GET /categories?page=X&size=Y
     * Get all categories with pagination
     */
    @GET
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        
        if (page < 0 || size <= 0 || size > 1000) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid pagination parameters\"}")
                    .build();
        }
        
        PageResponse<Category> result = categoryService.findAll(page, size);
        return Response.ok(result).build();
    }
    
    /**
     * GET /categories/{id}
     * Get category by ID
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return categoryService.findById(id)
                .map(category -> Response.ok(category).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Category not found\"}")
                        .build());
    }
    
    /**
     * POST /categories
     * Create new category
     */
    @POST
    public Response create(@Valid Category category, @Context UriInfo uriInfo) {
        // Ensure ID is null for creation
        category.setId(null);
        
        try {
            Category created = categoryService.save(category);
            URI uri = uriInfo.getAbsolutePathBuilder()
                    .path(String.valueOf(created.getId()))
                    .build();
            return Response.created(uri).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * PUT /categories/{id}
     * Update existing category
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid Category category) {
        if (!categoryService.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found\"}")
                    .build();
        }
        
        category.setId(id);
        
        try {
            Category updated = categoryService.save(category);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * DELETE /categories/{id}
     * Delete category
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (!categoryService.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found\"}")
                    .build();
        }
        
        try {
            categoryService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * GET /categories/{id}/items?page=X&size=Y
     * Get items for a specific category (relational endpoint)
     */
    @GET
    @Path("/{id}/items")
    public Response getItems(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size) {
        
        if (!categoryService.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Category not found\"}")
                    .build();
        }
        
        if (page < 0 || size <= 0 || size > 1000) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid pagination parameters\"}")
                    .build();
        }
        
        var result = itemService.findByCategoryId(id, page, size);
        return Response.ok(result).build();
    }
}
