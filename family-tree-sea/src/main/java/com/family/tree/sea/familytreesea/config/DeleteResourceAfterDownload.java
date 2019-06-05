package com.family.tree.sea.familytreesea.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiefengchang
 * this annotation requires that if parameter has HttpServletRequest, it must be the first argument
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteResourceAfterDownload {
}