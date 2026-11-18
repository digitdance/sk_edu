package org.springcrazy.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springcrazy.core.mp.CrazyMetaObjectHandler;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component   //一定不要忘记将处理器加到IOC容器中！
@Primary
public class MyMetaObjectHander extends CrazyMetaObjectHandler {


    //插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill.....");
        //super.insertFill(metaObject);
        //if(metaObject.getGetterNames())
        //setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject)
        //第一个参数 字段名
        //第二个参数 值
        //第三个参数 metaObject
//        this.setFieldValByName("createTime",new Date(),metaObject);
//        this.setFieldValByName("updateTime",new Date(),metaObject);
        // 上面的方法是旧版本，新版本的是使用下面的strictInsertFill方法
       // this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        //this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        String deptId =  SecureUtil.getDeptId();
        if(StringUtil.isNotBlank(deptId)){
            try {
                this.strictInsertFill(metaObject, "schoolId", Long.class, Long.parseLong(deptId));
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    //更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill.....");
        super.updateFill(metaObject);
        String deptId =  SecureUtil.getDeptId();
        if(StringUtil.isNotBlank(deptId)){
            try {
                this.strictInsertFill(metaObject, "schoolId", Long.class, Long.parseLong(deptId));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        //this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

}
