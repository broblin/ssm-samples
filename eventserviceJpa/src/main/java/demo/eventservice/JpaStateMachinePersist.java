package demo.eventservice;


import demo.eventservice.entities.StateContextEntity;
import demo.eventservice.repositories.EventStateRepository;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import static demo.eventservice.StateMachineConfig.*;


public class JpaStateMachinePersist implements
        StateMachinePersist<States, Events, String> {


    private final EventStateRepository eventStateRepository;

    public JpaStateMachinePersist(EventStateRepository eventStateRepository) {
        this.eventStateRepository = eventStateRepository;
    }

    @Override
    public void write(StateMachineContext<States, Events> context, String contextId) throws Exception {
        StateContextEntity contextObj=new StateContextEntity();
        contextObj.setId(contextId);
        contextObj.setUserId(contextId);
        contextObj.setCurrentState(context.getState());
        contextObj.setStateMachineContext(context);
        eventStateRepository.save(contextObj);
    }

    @Override
    public StateMachineContext<States, Events> read(String contextId) throws Exception {
        StateContextEntity contextObj = eventStateRepository.findOne(contextId);
        return contextObj==null?null:contextObj.getStateMachineContext() ;
    }
}
