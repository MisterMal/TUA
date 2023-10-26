package ssbd01.moa.scheduled;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import ssbd01.common.AbstractController;
import ssbd01.moa.managers.OrderManager;

@Startup
@Singleton
public class OrderScheduledTask extends AbstractController {

    @Inject
    OrderManager orderManagerLocal;

    @Schedule(hour = "*", minute = "0", second = "0", info = "Each hour")
    @PermitAll
    public void updateOrderQueue() {
        repeatTransactionVoidWithOptimisticLock(orderManagerLocal, () -> orderManagerLocal.updateQueue());
    }

}
