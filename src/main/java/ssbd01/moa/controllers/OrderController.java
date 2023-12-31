package ssbd01.moa.controllers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.inject.Singleton;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import ssbd01.common.AbstractController;
import ssbd01.config.EntityIdentitySignerVerifier;
import ssbd01.dto.order.CreateOrderDTO;
import ssbd01.dto.order.OrderDTO;
import ssbd01.entities.Order;
import ssbd01.moa.managers.OrderManager;
import ssbd01.mok.managers.AccountManager;
import ssbd01.util.converters.OrderConverter;

import java.util.List;
import java.util.stream.Collectors;

@Path("order")
@Singleton
@DenyAll
@LocalBean
@Log
public class OrderController extends AbstractController {

    @Inject
    public OrderManager orderManager;
    @Inject
    public AccountManager accountManager;
    @Inject
    public EntityIdentitySignerVerifier entityIdentitySignerVerifier;

    //moa 18
    @GET
    @Path("/")
    @PermitAll
    public List<OrderDTO> getAllOrders() {
        throw new UnsupportedOperationException();
    }

    //moa 17
    @GET
    @PermitAll
    @Path("/self")
    public Response getAllOrdersForSelf() {
        List<OrderDTO> orderDTOs = repeatTransaction(orderManager, () -> orderManager.getAllOrdersForSelf(accountManager.getCurrentUserWithAccessLevels()).stream()
                .map(OrderConverter::mapOrderToOrderDTO)
                .collect(Collectors.toList()));
        return Response.ok(orderDTOs).build();
    }

    // moa 14
    @PUT
    @Path("/{id}/cancel")
    @PermitAll
    public Response cancelOrder(@PathParam("id") Long id) {
        repeatTransactionVoid(orderManager, () -> orderManager.cancelOrder(id));
        return Response.ok().build();
    }

    //moa 13
    @PUT
    @Path("/{id}/approve")
    @PermitAll
    public Response approveOrder(@PathParam("id") Long id) {
        repeatTransactionVoid(orderManager, () -> orderManager.approveOrder(id));
        return Response.ok().build();
    }

    //moa 12
    @GET
    @Path("/to-approve")
    @PermitAll
    public List<OrderDTO> getOrdersToApprove() {
        List<Order> orders = repeatTransaction(orderManager,
                () -> orderManager.getOrdersToApprove());
        return orders.stream()
                .map(OrderConverter::mapOrderToOrderDTO)
                .collect(Collectors.toList());
    }

    // moa 7
    @POST
    @Path("/submit")
    @PermitAll
    public Response submitOrder(@Valid CreateOrderDTO createOrderDTO) {
      Order inputOrder = OrderConverter.mapCreateOrderDTOToOrder(createOrderDTO);
      repeatTransactionVoidWithOptimisticLock(orderManager, () -> orderManager.createOrder(inputOrder));
      return Response.status(Response.Status.CREATED).build();
    }


    // mok 9
    @GET
    @PermitAll
    @Path("/waiting")
    public List<OrderDTO> getWaitingOrders() {
        List<Order> orders = repeatTransaction(orderManager, () -> orderManager.getWaitingOrders());
                return orders.stream()
                        .map(OrderConverter::mapOrderToOrderDTO)
                        .collect(Collectors.toList());
    }

    //mok 8
   @PUT
   @PermitAll
   @Path("/{id}/withdraw")
   public Response withdrawOrderById(@PathParam("id") Long id){
       repeatTransactionVoid(orderManager, () -> orderManager.withdrawOrder(id));
        return Response.ok().build();
   }


    // mok 10
    @DELETE
    @PermitAll
    @Path("/{id}/waiting")
    public Response deleteWaitingOrderById(@PathParam("id") Long id) {
        repeatTransactionVoid(orderManager, () -> orderManager.deleteWaitingOrderById(id));
        return Response.ok().build();
    }

    @PUT
    @PermitAll
    @Path("/{id}/patient-approve")
    public Response approvedByPatient(@PathParam("id") Long id){
        repeatTransactionVoid(orderManager, () -> orderManager.approvedByPatient(id));
        return Response.ok().build();
    }

    //moa 16
    @PUT
    @Path("/update-queue")
    @PermitAll
    public Response updateQueue() {
        repeatTransactionVoidWithOptimisticLock(orderManager, () -> orderManager.updateQueue());
        return Response.noContent().build();
    }

    //moa 15
    @GET
    @Path("/statistics")
    @PermitAll
    public void showStatistics() {
        throw new UnsupportedOperationException();
    }

}
