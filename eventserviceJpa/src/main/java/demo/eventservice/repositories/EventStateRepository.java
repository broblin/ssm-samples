package demo.eventservice.repositories;

import demo.eventservice.StateMachineConfig;
import demo.eventservice.entities.StateContextEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventStateRepository extends JpaRepository<StateContextEntity,String> {

    Page<StateContextEntity> findByCurrentState(StateMachineConfig.States currentState, Pageable pageable);


}
