package redissionlock.services;


public interface LockService {

    /**
     * Redis获取分布锁过程
     * */
    public void getRedisLock();

}
