//package ssbd01.moa.managers;
//
//
//import jakarta.ejb.Local;
//import jakarta.ejb.Singleton;
//import ssbd01.common.CommonManagerLocalInterface;
//import ssbd01.entities.Account;
//import ssbd01.entities.Order;
//
//import java.util.List;
//
//@Local
//@Singleton
//public interface OrderManagerLocal extends CommonManagerLocalInterface {
//
//    void createOrder(Order order);
//
//    Order getOrder(Long id);
//
//    List<Order> getAllOrders();
//
//    List<Order> getAllOrdersForSelf(Account account);
//
//    List<Order> getWaitingOrders();
//
//    List<Order> getOrdersToApprove();
//
//    void approveOrder(Long id);
//
//    void cancelOrder(Long id);
//
//    void deleteWaitingOrderById(Long id);
//
//    void withdrawOrder(Long id);
//
//    void approvedByPatient(Long id);
//
//    void updateQueue();
//}
