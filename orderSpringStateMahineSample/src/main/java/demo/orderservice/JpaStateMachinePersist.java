package demo.orderservice;


import demo.orderservice.entities.OrderEntity;
import demo.orderservice.repositories.OrderRepository;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;


public class JpaStateMachinePersist implements
        StateMachinePersist<StateMachineConfig.States, StateMachineConfig.Events, OrderEntity> {


    private final OrderRepository orderRepository;

    public JpaStateMachinePersist(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void write(StateMachineContext<StateMachineConfig.States, StateMachineConfig.Events> context, OrderEntity contextObj) throws Exception {
        contextObj.setCurrentState(context.getState());
        contextObj.setStateMachineContext(context);
        orderRepository.save(contextObj);
    }

    @Override
    public StateMachineContext<StateMachineConfig.States, StateMachineConfig.Events> read(OrderEntity contextObj) throws Exception {
        contextObj = orderRepository.findOne(contextObj.getId());
        return contextObj==null?null:contextObj.getStateMachineContext() ;
    }
}
