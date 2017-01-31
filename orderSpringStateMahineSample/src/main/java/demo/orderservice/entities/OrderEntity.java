package demo.orderservice.entities;

import demo.orderservice.ContextEntity;

import org.springframework.statemachine.StateMachineContext;

import javax.persistence.*;


import static demo.orderservice.StateMachineConfig.*;

@Entity
@Access(AccessType.FIELD)
@Table(name = "OrderStateContext", indexes = @Index(columnList = "currentState"))
public class OrderEntity implements ContextEntity<States,Events,String> {

    @Id
    String id;

    String userId;
    StateMachineContext<States,Events>  stateMachineContext;

    @Enumerated
    States currentState;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public StateMachineContext<States, Events> getStateMachineContext() {
        return stateMachineContext;
    }

    @Override
    public void setStateMachineContext(StateMachineContext<States, Events> stateMachineContext) {
        this.stateMachineContext = stateMachineContext;
    }

    public States getCurrentState() {
        return currentState;
    }

    public void setCurrentState(States currentState) {
        this.currentState = currentState;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
