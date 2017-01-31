/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.orderservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Scanner;

import demo.orderservice.entities.OrderEntity;
import demo.orderservice.repositories.OrderRepository;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;


@Configuration
@DependsOn(value = "JpaConf")
public class StateMachineConfig {


    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ApplicationContext appContext;


    //tag::snippetA[]
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ProxyFactoryBean stateMachine() {
        ProxyFactoryBean pfb = new ProxyFactoryBean();
        pfb.setTargetSource(poolTargetSource());
        return pfb;
    }
//end::snippetA[]

    //tag::snippetB[]
    @Bean
    public CommonsPool2TargetSource poolTargetSource() {
        CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
        pool.setMaxSize(3);
        pool.setTargetBeanName("stateMachineTarget");
        return pool;
    }
//end::snippetB[]

    //tag::snippetC[]
    @Bean(name = "stateMachineTarget")
    @Scope(scopeName="prototype")
    public StateMachine<States, Events> stateMachineTarget() throws Exception {
        Builder<States, Events> builder = StateMachineBuilder.<States, Events>builder();

        builder.configureConfiguration()
                .withConfiguration()
                .beanFactory(appContext.getAutowireCapableBeanFactory())
                .autoStartup(true);

        builder.configureStates()
                .withStates()
                .initial(States.ORDERED)
                .states(EnumSet.allOf(States.class));

        builder.configureTransitions()
                .withExternal()
                .source(States.ORDERED)
                .target(States.ASSEMBLED)
                .event(Events.assemble)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.ASSEMBLED)
                .target(States.DELIVERED)
                .event(Events.deliver)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.DELIVERED)
                .target(States.INVOICED)
                .event(Events.release_invoice)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.INVOICED)
                .target(States.PAYED)
                .event(Events.payment_received)
                .action(traceEventAction())
                .action(payAction())
                .and()
                .withExternal()
                .source(States.ORDERED)
                .target(States.CANCELLED)
                .event(Events.cancel)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.ASSEMBLED)
                .target(States.CANCELLED)
                .event(Events.cancel)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.DELIVERED)
                .target(States.RETURNED)
                .event(Events.claim)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.INVOICED)
                .target(States.RETURNED)
                .event(Events.claim)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.RETURNED)
                .target(States.CANCELLED)
                .event(Events.cancel)
                .action(traceEventAction())
                .and()
                .withExternal()
                .source(States.RETURNED)
                .target(States.ASSEMBLED)
                .event(Events.reassemble)
                .action(traceEventAction());

        return builder.build();
    }
//end::snippetC[]

    @Bean
    public Action<States, Events> traceEventAction() {
        return (context) -> {
                String variable = context.getEvent().name();
                Integer count = context.getExtendedState().get(variable, Integer.class);
                if (count == null) {
                    context.getExtendedState().getVariables().put(variable, 1);
                } else {
                    context.getExtendedState().getVariables().put(variable, (count + 1));
                }

            };
    }

    @Bean
    public Action<States, Events> payAction() {
        return context -> context.getExtendedState().getVariables().put("PAYED", true);


    }


    @Bean
    public StateMachinePersist<States, Events, OrderEntity> stateMachinePersist(OrderRepository orderRepository) {
        return new JpaStateMachinePersist(orderRepository);
    }

    @Bean
    public DefaultStateMachinePersister<States, Events,OrderEntity> jpaStateMachinePersister(
            StateMachinePersist<States, Events, OrderEntity> stateMachinePersist) {
        return new DefaultStateMachinePersister<States, Events,OrderEntity>(stateMachinePersist);
    }
//end::snippetD[]

    @Bean
    public String stateChartModel() throws IOException {
        ClassPathResource model = new ClassPathResource("statechartmodel.txt");
        InputStream inputStream = model.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return content;
    }

    public enum States {
        ORDERED, ASSEMBLED, DELIVERED, INVOICED, PAYED, CANCELLED, RETURNED
    }

    public enum Events {
        order, assemble, deliver, release_invoice, payment_received, cancel, claim, reassemble
    }
}
