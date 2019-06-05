package com.family.tree.sea.familytreesea.config;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author xiefengchang
 */
@Aspect
@Component
public class DeleteResourceAfterDownloadAspect {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteResourceAfterDownloadAspect.class);

    @AfterReturning(pointcut = "execution(* com.family.tree.sea.familytreesea.controller.FamilyTreeController.download(String)) && @annotation(DeleteResourceAfterDownload)")
    public void deleteResourceAfterDownloadAfter(JoinPoint joinPoint ) throws Throwable {
        File file = new File((String)joinPoint.getArgs()[0]);
        FileUtils.deleteDirectory(new File(file.getParent()));
    }

}
