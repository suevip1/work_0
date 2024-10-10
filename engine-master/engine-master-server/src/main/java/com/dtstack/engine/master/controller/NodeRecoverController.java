package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.impl.NodeRecoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuebai
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/node/nodeRecover")
public class NodeRecoverController {

    @Autowired
    private NodeRecoverService nodeRecoverService;

    @RequestMapping(value="/masterTriggerNode", method = {RequestMethod.POST,RequestMethod.GET})
    public void masterTriggerNode(){
        nodeRecoverService.masterTriggerNode();
    }
}
