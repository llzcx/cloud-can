package ccw.serviceinnovation.oss.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @author 陈翔
 */
@Component
@Slf4j
public class RedisUtil {
   @Resource
   private StringRedisTemplate stringRedisTemplate;

   @Resource
   private RedisTemplate redisTemplate;

   private static  final ObjectMapper mapper = new ObjectMapper();

   // Key（键），简单的key-value操作

   /**
    * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
    * @param key
    * @return
    */
   public long ttl(String key) {
      byte[] bytes = new byte[123];
      return stringRedisTemplate.getExpire(key);
   }

   /**
    * 判断key是否过期
    * @param key
    * @return
    */
   public boolean isExpire(String key) {
      return ttl(key) > 0?false:true;
   }

   /**
    * 判断key是否存在
    * @param key
    * @return
    */
   public boolean exists(String key) {
      return redisTemplate.hasKey(key);
   }
   
   /**
    * 实现命令：expire 设置过期时间，单位秒
    * @param key
    * @return
    */
   public void expire(String key, long timeout) {
      stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
   }
   
   /**
    * 实现命令：INCR key，增加key一次
    * @param key
    * @return
    */
   public long incr(String key, long delta) {
      return stringRedisTemplate.opsForValue().increment(key, delta);
   }

   /**
    * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
    */
   public Set<String> keys(String pattern) {
      return stringRedisTemplate.keys(pattern);
   }

   /**
    * 实现命令：DEL key，删除一个key
    *
    * @param key
    * @return
    */
   public Boolean del(String key) {

      return stringRedisTemplate.delete(key);
   }

   // String（字符串）

   /**
    * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
    * 
    * @param key
    * @param value
    */
   public void set(String key, String value) {
      stringRedisTemplate.opsForValue().set(key, value);
   }


   /**
    * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
    *
    * @param key
    * @param value
    */
   public void set(String key, Integer value) {
      stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
   }

   /**
    * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
    * 
    * @param key
    * @param value
    * @param timeout
    *            （以秒为单位）
    */
   public void set(String key, String value, long timeout) {
      stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
   }

   /**
    * 实现命令：GET key，返回 key所关联的字符串值。
    * 
    * @param key
    * @return value
    */
   public String get(String key) {
      return (String) stringRedisTemplate.opsForValue().get(key);
   }

   /**
    * 以json形式存储对象并设置过期时间
    * @param key
    * @param object
    * @param timeout
    * @return
    */
   public Boolean setObject(String key,Object object,Long timeout){
      stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(object),timeout, TimeUnit.SECONDS);
      return true;
   }

   /**
    * 以json形式存储对象 永久存储
    * @param key
    * @param object
    * @return
    */
   public Boolean setObject(String key,Object object){
      stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(object));
      return true;
   }

   /**
    * 以json形式获取对象
    * @param key
    * @param cls
    * @param <T>
    * @return
    */
   public <T> T getObject(String key,Class<T> cls){
      return JSONObject.parseObject(stringRedisTemplate.opsForValue().get(key),cls);
   }


   /**
    * 获取第offset的值
    * @param key
    * @param offset
    * @return
    */
   public Boolean getBit(String key,int offset){
      return redisTemplate.opsForValue().getBit(key, offset);
   }

   /**
    * 将key的第bit位设为1
    * @param key
    * @param offset
    * @param value
    * @return
    */
   public Boolean setBit(String key,int offset,boolean value){
      return redisTemplate.opsForValue().setBit(key, offset, value);
   }



   // Hash（哈希表）

   /**
    * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
    * @param key
    * @param field
    * @param value
    */
   public void hset(String key, String field, Object value) {
      stringRedisTemplate.opsForHash().put(key, field, value);
   }

   /**
    * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
    * 
    * @param key
    * @param field
    * @return
    */
   public String hget(String key, String field) {
      return (String) stringRedisTemplate.opsForHash().get(key, field);
   }

   /**
    * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
    * 
    * @param key
    * @param fields
    */
   public void hdel(String key, Object... fields) {
      stringRedisTemplate.opsForHash().delete(key, fields);
   }

   public Boolean hExists(String key, String field){
      return stringRedisTemplate.opsForHash().hasKey(key, field);
   }

   /**
    * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
    * 
    * @param key
    * @return
    */
   public Map<Object, Object> hgetall(String key) {
      return stringRedisTemplate.opsForHash().entries(key);
   }

   // hash 结构的计数
   public long hincr(String key, String field, long value) {
      return redisTemplate.opsForHash().increment(key, field, value);
   }

   // List（列表）

   /**
    * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
    * 
    * @param key
    * @param value
    * @return 执行 LPUSH命令后，列表的长度。
    */
   public long LPush(String key, String value) {
      return stringRedisTemplate.opsForList().leftPush(key, value);
   }

   /**
    * 实现命令：LPOP key，移除并返回列表 key的头元素。
    * 
    * @param key
    * @return 列表key的头元素。
    */
   public String LPop(String key) {
      return (String) stringRedisTemplate.opsForList().leftPop(key);
   }

   /**
    * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
    * 
    * @param key
    * @param value
    * @return 执行 LPUSH命令后，列表的长度。
    */
   public long RPush(String key, String value) {
      return stringRedisTemplate.opsForList().rightPush(key, value);
   }


   /**
    * 将一个列表添加到list
    * @param key
    * @param list
    */
   public void RAddList(String key, List<String> list){
      redisTemplate.opsForList().rightPush(key,list);
   }

   /**
    * 获取整个key
    * @param key
    * @return
    */
   public List<String> getAllList(String key){
      return redisTemplate.opsForList().range(key, 0, -1);
   }

   //zset 排序列表

   /**
    * 获取指定排名的value
    * @param key
    * @param start
    * @param end
    * @return
    */
   public Set<Object> reverseRange(String key, Integer start, Integer end) {
      try {
         return redisTemplate.opsForZSet().reverseRange(key, start, end);
      } catch (Exception e) {
         log.error("[RedisUtils.rangeZset] [error] [key is {},start is {},end is {}]", key, start, end, e);
         return null;
      }
   }

   /**
    * 为有序集合添加一个元素
    * @param key 键
    * @param value 值
    * @param seqNo 指定score
    * @return
    */
   public Boolean addSortSet(String key, Object value, double seqNo) {
      try {
         return redisTemplate.opsForZSet().add(key, value, seqNo);
      } catch (Exception e) {
         log.error("[RedisUtils.addZset] [error]", e);
         return false;
      }
   }
   /**
    * 查询集合中指定顺序的值， 0 -1 表示获取全部的集合内容
    * 返回有序的集合，score小的在前面
    * @param key
    * @param start
    * @param end
    * @return
    */
   public Set<String> range(String key, int start, int end) {
      return redisTemplate.opsForZSet().range(key, start, end);
   }

   /**
    * 实现命令 zrem
    * 删除zset中的元素
    *
    * @param key
    * @param value
    */
   public void remove(String key, String value) {
      redisTemplate.opsForZSet().remove(key, value);
   }


   /**
    * 为某个value增加值
    * @param key
    * @param value
    * @param seqNo
    * @return
    */
   public Boolean incrementScore(String key, Object value, double seqNo) {
      try {
         redisTemplate.opsForZSet().incrementScore(key, value, seqNo);
         return true;
      } catch (Exception e) {
         log.error("[RedisUtils.addZset] [error]", e);
         return false;
      }
   }

   /**
    * 获取zset中某个value的score
    * @param key
    * @param value
    * @return
    */
   public double getScore(String key, Object value) {
      try {
         return redisTemplate.opsForZSet().score(key, value);
      } catch (Exception e) {
         log.error("[RedisUtils.addZset] [error]", e);
         return 0;
      }
   }

   /**
    * 检查value是否存在
    * @param key
    * @param value
    * @return
    */
   public Boolean checkSortSetValueIsExist(String key, String value){
      return  redisTemplate.opsForZSet().score(key, value)!=null;

   }



   //---------------------redis  incr/decr 相关----------------------------

   /**
    * 设置自增/自减初始值
    *
    * @param key
    * @param value
    * @param timeout
    * @param unit
    */
   public void setAtomicValue(String key, int value, long timeout, TimeUnit unit) {
      RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory(), value);
      redisAtomicLong.expire(timeout, unit);
   }

   /**
    * 在redis中自增并获取数据
    *
    * @param key 键
    * @return 自增后的值
    */
   public long incr(String key) {
      RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
      return redisAtomicLong.incrementAndGet();
   }

   /**
    * 在redis中自增并获取数据，并设置过期时间
    *
    * @param key     键
    * @param timeout 过期时间
    * @param unit    过期时间单位
    * @return 自增后的值
    */
   public long incr(String key, long timeout, TimeUnit unit) {
      RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
      redisAtomicLong.expire(timeout, unit);
      return redisAtomicLong.incrementAndGet();
   }

   /**
    * 在redis中自增指定步长并获取数据
    *
    * @param key       键
    * @param increment 步长
    * @return 自增后的值
    */
   public long incr(String key, int increment) {
      RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
      return redisAtomicLong.addAndGet(increment);
   }

   /**
    * 在redis中自增指定步长并获取数据，并设置过期时间
    *
    * @param key       键
    * @param increment 步长
    * @param timeout   过期时间
    * @param unit      过期时间单位
    * @return 自增后的值
    */
   public long incr(String key, int increment, long timeout, TimeUnit unit) {
      RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
      redisAtomicLong.expire(timeout, unit);
      return redisAtomicLong.addAndGet(increment);
   }

}