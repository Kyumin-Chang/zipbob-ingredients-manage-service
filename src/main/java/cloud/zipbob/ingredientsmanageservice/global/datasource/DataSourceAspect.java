package cloud.zipbob.ingredientsmanageservice.global.datasource;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class DataSourceAspect {

    @Before("execution(* cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service.*.get*(..)) || " +
            "execution(* cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.*.get*(..))")
    public void setReadDataSource() {
        log.info("Switching to Slave DataSource");
        DataSourceContextHolder.setDataSourceType("slave");
    }

    @Before("execution(* cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service.*.create*(..)) || " +
            "execution(* cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service.*.update*(..)) || " +
            "execution(* cloud.zipbob.ingredientsmanageservice.domain.refrigerator.service.*.delete*(..)) || " +
            "execution(* cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.*.add*(..)) || " +
            "execution(* cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.*.delete*(..)) || " +
            "execution(* cloud.zipbob.ingredientsmanageservice.domain.ingredient.service.*.update*(..))")
    public void setWriteDataSource() {
        log.info("Switching to Master DataSource");
        DataSourceContextHolder.setDataSourceType("master");
    }

}
