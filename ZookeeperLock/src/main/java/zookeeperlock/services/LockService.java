package zookeeperlock.services;

public interface LockService {

    /**
     * 获取分布式锁，后修改ZK节点中的数据
     * */
    public void testGetLock();

}
