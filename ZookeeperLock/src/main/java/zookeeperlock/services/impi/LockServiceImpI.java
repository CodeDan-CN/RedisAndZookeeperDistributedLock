package zookeeperlock.services.impi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zookeeperlock.services.LockService;
import zookeeperlock.utils.ZkConfig;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Service
public class LockServiceImpI implements LockService {

    @Autowired
    private CuratorFramework curatorFramework;

    /**
     * 首先在服务器端设置持久节点/parentLock和/data，/data设置值为100
     * 我们使用ab并发工具，设置请求数量为101，并发请求100
     * 预计结果，打印100-1数字，并无重复，最后打印一句本次累减结束，结果为0。
     * */
    @Override
    public void testGetLock() {
        InterProcessMutex interProcessMutex = new InterProcessMutex(curatorFramework,"/parentLock");
        try {
            interProcessMutex.acquire();
            byte[] bytes = curatorFramework.getData().forPath("/data");
            int data = Integer.parseInt(new String(bytes));
            if(data <= 0){
                System.out.println("本次累减结束，结果为"+data);
                interProcessMutex.release();
                return;
            }
            System.out.println("当前数字为"+data);
            data = data - 1;
            String result = data+"";
            curatorFramework.setData().forPath("/data",result.getBytes(StandardCharsets.UTF_8));
            interProcessMutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
