package com.romjan.restmart;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PATCH;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

import retrofit2.http.PUT;

public interface ApiService {
    @POST("auth/jwt/create/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @GET("auth/users/me/")
    Call<User> getUser(@Header("Authorization") String authToken);

    @PUT("auth/users/me/")
    Call<User> updateUser(@Header("Authorization") String authToken, @Body UpdateUserRequest updateUserRequest);

    @POST("auth/users/set_password/")
    Call<Void> changePassword(@Header("Authorization") String authToken, @Body ChangePasswordRequest changePasswordRequest);

    @POST("auth/users/")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("products/")
    Call<ProductResponse> getProducts(
            @Header("Authorization") String authToken,
            @Query("page") int page,
            @Query("category_id") Integer categoryId,
            @Query("price__gt") Integer minPrice,
            @Query("price__lt") Integer maxPrice,
            @Query("search") String searchQuery,
            @Query("ordering") String ordering
    );

    @GET("products/{id}/")
    Call<Product> getProductDetails(@Header("Authorization") String authToken, @Path("id") int productId);

    @GET("categories/")
    Call<List<Category>> getCategories(@Header("Authorization") String authToken);

    @POST("carts/")
    Call<Cart> createCart(@Header("Authorization") String authToken);

    @GET("carts/{cart_pk}/items/")
    Call<List<CartItem>> getCartItems(@Header("Authorization") String authToken, @Path("cart_pk") String cartPk);

    @POST("carts/{cart_pk}/items/")
    Call<CartItem> addItemToCart(@Header("Authorization") String authToken, @Path("cart_pk") String cartPk, @Body AddItemRequest addItemRequest);

    @GET("carts/{cart_pk}/items/{id}/")
    Call<CartItem> getSpecificCartItem(@Header("Authorization") String authToken, @Path("cart_pk") String cartPk, @Path("id") int itemId);

    @PATCH("carts/{cart_pk}/items/{id}/")
    Call<CartItem> updateCartItemQuantity(@Header("Authorization") String authToken, @Path("cart_pk") String cartPk, @Path("id") int itemId, @Body UpdateQuantityRequest updateQuantityRequest);

    @DELETE("carts/{cart_pk}/items/{id}/")
    Call<Void> deleteCartItem(@Header("Authorization") String authToken, @Path("cart_pk") String cartPk, @Path("id") int itemId);

    @GET("orders/")
    Call<List<Order>> getOrders(@Header("Authorization") String authToken);

    @POST("orders/")
    Call<Order> createOrder(@Header("Authorization") String authToken, @Body CreateOrderRequest createOrderRequest);

    @POST("orders/{id}/cancel/")
    Call<Void> cancelOrder(@Header("Authorization") String authToken, @Path("id") String orderId);

    @GET("orders/{id}/")
    Call<Order> getOrderDetails(@Header("Authorization") String authToken, @Path("id") String orderId);

    @POST("payment/initiate/")
    Call<PaymentInitiationResponse> initiatePayment(@Header("Authorization") String authToken, @Body PaymentInitiationRequest request);
}