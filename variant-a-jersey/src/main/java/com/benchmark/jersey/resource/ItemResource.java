package com.benchmark.jersey.resource;

import com.benchmark.jersey.dto.PageResponse;
import com.benchmark.jersey.entity.Item;
import com.benchmark.jersey.entity.Category;
import com.benchmark.jersey.service.ItemService;
import com.benchmark.jersey.service.CategoryService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.net.URI;

/**
 * JAX-RS Resource for Item endpoints
 */
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {
    
    @Inject
    private ItemService itemService;
    
    @Inject
    private CategoryService categoryService;
    
    /**
     * GET /items?page=X&size=Y&categoryId=Z
     * Get all items with optional category filter
     */
    @GET
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("50") int size,
            @QueryParam("categoryId") Long categoryId) {
        
        if (page < 0 || size <= 0 || size > 1000) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid pagination parameters\"}")
                    .build();
        }
        
        PageResponse<Item> result;
        
        if (categoryId != null) {
            // Filter by category
            if (!categoryService.exists(categoryId)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Category not found\"}")
                        .build();
            }
            result = itemService.findByCategoryId(categoryId, page, size);
        } else {
            // Get all items
            result = itemService.findAll(page, size);
        }
        
        return Response.ok(result).build();
    }
    
    /**
     * GET /items/{id}
     * Get item by ID
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return itemService.findById(id)
                .map(item -> Response.ok(item).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Item not found\"}")
                        .build());
    }
    
    /**
     * POST /items
     * Create new item
     */
    @POST
    public Response create(@Valid ItemRequest request, @Context UriInfo uriInfo) {
        // Validate category exists
        Category category = categoryService.findById(request.getCategoryId())
                .orElse(null);
        
        if (category == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Category not found\"}")
                    .build();
        }
        
        // Create item
        Item item = new Item();
        item.setSku(request.getSku());
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setStock(request.getStock());
        item.setCategory(category);
        
        try {
            Item created = itemService.save(item);
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
     * PUT /items/{id}
     * Update existing item
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid ItemRequest request) {
        Item existingItem = itemService.findById(id).orElse(null);
        
        if (existingItem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Item not found\"}")
                    .build();
        }
        
        // Validate category if changed
        if (request.getCategoryId() != null && 
            !request.getCategoryId().equals(existingItem.getCategory().getId())) {
            
            Category category = categoryService.findById(request.getCategoryId())
                    .orElse(null);
            
            if (category == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Category not found\"}")
                        .build();
            }
            existingItem.setCategory(category);
        }
        
        // Update fields
        if (request.getSku() != null) existingItem.setSku(request.getSku());
        if (request.getName() != null) existingItem.setName(request.getName());
        if (request.getPrice() != null) existingItem.setPrice(request.getPrice());
        if (request.getStock() != null) existingItem.setStock(request.getStock());
        
        try {
            Item updated = itemService.save(existingItem);
            return Response.ok(updated).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * DELETE /items/{id}
     * Delete item
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (!itemService.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Item not found\"}")
                    .build();
        }
        
        try {
            itemService.delete(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    /**
     * DTO for Item creation/update requests
     */
    public static class ItemRequest {
        @NotBlank
        private String sku;
        
        @NotBlank
        private String name;
        
        @NotNull
        private java.math.BigDecimal price;
        
        @NotNull
        private Integer stock;
        
        @NotNull
        private Long categoryId;
        
        // Getters and setters
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public java.math.BigDecimal getPrice() { return price; }
        public void setPrice(java.math.BigDecimal price) { this.price = price; }
        
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }
}
