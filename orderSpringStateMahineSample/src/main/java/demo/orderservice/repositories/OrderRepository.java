package demo.orderservice.repositories;

import demo.orderservice.StateMachineConfig;
import demo.orderservice.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<OrderEntity,String> {

    Page<OrderEntity> findByCurrentState(StateMachineConfig.States currentState, Pageable pageable);


}
