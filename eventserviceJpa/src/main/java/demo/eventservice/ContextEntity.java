package demo.eventservice;

import org.springframework.statemachine.StateMachineContext;

import java.io.Serializable;

/**
 * Created by montassar on 30/01/17.
 */
public interface ContextEntity<S,E,ID extends Serializable> {

    StateMachineContext<S, E> getStateMachineContext();

    void setStateMachineContext(StateMachineContext<S, E> context);
}
