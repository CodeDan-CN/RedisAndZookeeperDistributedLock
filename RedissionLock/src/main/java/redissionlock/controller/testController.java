package redissionlock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redissionlock.services.LockService;

@RestController
public class testController {

    @Autowired
    private LockService lockService;

    @RequestMapping("lock")
    public String test(){
        lockService.getRedisLock();
        return "123";
    }
}
