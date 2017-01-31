package com.example;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;


//@RequiredArgsConstructor
public class DefaultStateMachineAdapter<S, E, T> {

    final StateMachineFactory<S, E> stateMachineFactory;

    final StateMachinePersister<S, E, T> persister;

    public DefaultStateMachineAdapter(StateMachineFactory<S, E> stateMachineFactory, StateMachinePersister<S, E, T> persister) {
        this.stateMachineFactory = stateMachineFactory;
        this.persister = persister;
    }

    //@SneakyThrows
    public StateMachine<S, E> restore(T contextObject) {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine();
        try {
            return persister.restore(stateMachine, contextObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //@SneakyThrows
    public void persist(StateMachine<S, E> stateMachine, T order) {
        try {
            persister.persist(stateMachine, order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StateMachine<S, E> create() {
        StateMachine<S, E> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
        return stateMachine;
    }

}
