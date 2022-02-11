package redissionlock.services.impi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import redissionlock.services.LockService;
import redissionlock.utils.LuaUtil;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class LockServiceImpI implements LockService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 通过原子性加锁，原子性解锁来完成redis分布式锁
     * 前提：在redis服务器端创建字符串类型数据，key = lock，value = 100即可运行
     * */
    @Override
    public void getRedisLock() {
        long id = Thread.currentThread().getId();
        String uuid = String.valueOf(id);
        //获取锁
        Boolean lockFlag = stringRedisTemplate.opsForValue()
                .setIfAbsent("lock", uuid, 20, TimeUnit.SECONDS);
        if(lockFlag){
            String num = stringRedisTemplate.opsForValue().get("num");
            if (num == null){
                System.out.println("还未开始");
                //这里如果我们测试准备好了，不会进入这里，所以没有必要解锁，等待超时即可
                return;
            }
            int value = Integer.parseInt(num);
            if (value <= 0 ){
                System.out.println("累减结束，当前结果为"+value);
            }else {
                System.out.println("现在数字为"+num);
                stringRedisTemplate.opsForValue().decrement("num");
            }
            //执行LUA脚本删除
            String luascript = new LuaUtil().getLuaText();
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(luascript);
            redisScript.setResultType(Long.class);
            stringRedisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
        }else {
            //如果竞争失败，再次尝试获取锁，这里需要手动操作
            getRedisLock();
        }
    }
}
