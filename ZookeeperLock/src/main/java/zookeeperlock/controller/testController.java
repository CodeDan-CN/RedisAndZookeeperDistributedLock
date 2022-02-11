package zookeeperlock.controller;


import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zookeeperlock.services.LockService;

@RestController
public class testController {

    @Autowired
    private LockService lockService;

    @RequestMapping("/log")
    public String test(){
        lockService.testGetLock();
        return "123";
    }
}
