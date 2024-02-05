package ccw.serviceinnovation.oss.manager.authority.api;

import ccw.serviceinnovation.common.entity.Api;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.mapper.ApiMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * 项目中的接口与数据库的交互
 * @author 陈翔
 */
@Service
@Slf4j
public class ApiService extends ServiceImpl<ApiMapper, Api> {

    @Autowired
    ApiMapper apiMapper;

    /**
     * 初始化接口资源到数据库
     */
    public void initApi(){
        apiMapper.delete(null);
        //拿到路径 => com.cx.service
        String path = OssApplicationConstant.CONTROLLER;
        //扫描哪些类上面有component注解
        ClassLoader classLoader = ApiService.class.getClassLoader();
        //getResource 需要../.../..的结果 比如 com/cx/service
        path = path.replace('.', '/');
        URL resource = classLoader.getResource(path);
        String packagePath = resource.getPath();
        //解决中文乱码问题
        try {
            packagePath = java.net.URLDecoder.decode(packagePath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(packagePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            //扫描
            for (File f : files) {
                String fileName = f.getAbsolutePath();
                //如果是一个class文件
                if (fileName.endsWith(".class")) {
                    //获取 ccw.serviceinnovation.oss的ccw
                    String prefix = OssApplicationConstant.CONTROLLER.substring(0, OssApplicationConstant.CONTROLLER.indexOf("."));
                    //获取完整的类名
                    String className = fileName = fileName.substring(fileName.indexOf(prefix), fileName.indexOf(".class"));
                    //将类名变成com.cx.UserService.class
                    className = className.replace("\\", ".");
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        RequestMapping annotation1 = clazz.getAnnotation(RequestMapping.class);
                        if (clazz.getAnnotation(Controller.class)!=null ||annotation1 !=null) {
                            log.info("{} is a controller",clazz);
                            //如果是一个接口,获取方法列表
                            Method[] methods = clazz.getMethods();
                            for (Method method : methods) {
                                log.info(method.getName());
                                OssApi annotation = method.getAnnotation(OssApi.class);
                                if(annotation!=null){
                                    apiMapper.init(annotation1.value()[0]+"/"+annotation.name(), annotation.description(),annotation.type(),annotation.target());
                                }
                            }
                        }else{
                            log.info("{}'s Annotation is empty",clazz);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
