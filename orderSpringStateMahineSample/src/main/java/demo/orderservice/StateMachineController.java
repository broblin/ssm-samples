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

import demo.orderservice.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import demo.orderservice.StateMachineConfig.Events;
import demo.orderservice.StateMachineConfig.States;

@Controller
public class StateMachineController {

    //tag::snippetA[]
    @Autowired
    private StateMachine<States, Events> stateMachine;

    @Autowired
    private StateMachinePersister<States, Events, OrderEntity> stateMachinePersister;
//end::snippetA[]

    @Autowired
    private String stateChartModel;

    @RequestMapping("/")
    public String home() {
        return "redirect:/state";
    }

    //tag::snippetB[]
    @RequestMapping("/state")
    public String feedAndGetState(@RequestParam(value = "orderId", required = false) String orderId,@RequestParam(value = "user", required = false) String user,
                                  @RequestParam(value = "id", required = false) Events id, Model model) throws Exception {
        model.addAttribute("orderId", orderId);
        model.addAttribute("user", user);
        model.addAttribute("allTypes", Events.values());
        model.addAttribute("stateChartModel", stateChartModel);
        // we may get into this page without a user so
        // do nothing with a state machine
        if (StringUtils.hasText(orderId)) {
            OrderEntity order = new OrderEntity();
            order.setId(user+":"+orderId);
            order.setUserId(user);
            resetStateMachineFromStore(order);
            if (id != null) {
                feedMachine(order, id);
            }
            model.addAttribute("states", stateMachine.getState().getIds());
            model.addAttribute("extendedState", stateMachine.getExtendedState().getVariables());
        }
        return "states";
    }
//end::snippetB[]

    //tag::snippetC[]
    @RequestMapping(value = "/feed",method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void feedPageview(@RequestBody(required = true) Pageview event) throws Exception {
        Assert.notNull(event.getUser(), "User must be set");
        Assert.notNull(event.getId(), "Id must be set");
        OrderEntity order = new OrderEntity();
        order.setId(event.getUser()+":"+event.getOrderId());
        order.setUserId(event.getUser());
        resetStateMachineFromStore(order);
        feedMachine(order, event.getId());
    }
//end::snippetC[]

    //tag::snippetD[]
    private void feedMachine(OrderEntity order, Events id) throws Exception {
        stateMachine.sendEvent(id);
        stateMachinePersister.persist(stateMachine, order);
    }
//end::snippetD[]

    //tag::snippetE[]
    private StateMachine<States, Events> resetStateMachineFromStore(OrderEntity orderEntity) throws Exception {
        return stateMachinePersister.restore(stateMachine, orderEntity);
    }
//end::snippetE[]
}
