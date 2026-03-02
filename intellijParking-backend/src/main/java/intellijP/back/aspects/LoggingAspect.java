package intellijP.back.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect pour le logging automatique des operations.
 * - Maintient le code propre et concentre sur la logique metier
 * - controllerMethods() : Tous les controllers REST
 * - serviceMethods()    : Tous les services metier
 * - repositoryMethods() : Tous les repositories
 */
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(* esiag.back.controllers.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* esiag.back.services.*.*(..))")
    public void serviceMethods() {}
    @Pointcut("execution(* esiag.back.repositories.*.*(..))")
    public void repositoryMethods() {}

    @Before("controllerMethods()")
    public void logControllerEntry(JoinPoint joinPoint) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.debug("=> {} - Parametres: {}", methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logControllerSuccess(JoinPoint joinPoint, Object result) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();

        logger.info("<= {} - Succes", methodName);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logControllerError(JoinPoint joinPoint, Throwable ex) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();

        logger.warn("<= {} - Erreur: {}", methodName, ex.getMessage());
    }

    @Around("serviceMethods()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();
        logger.debug("=> Service.{} - Args: {}", methodName, summarizeArgs(args));

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("<= Service.{} - Succes ({}ms)", methodName, duration);
            return result;

        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("<= Service.{} - Echec apres {}ms: {}", methodName, duration, ex.getMessage());
            throw ex;
        }
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");

            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else {
                String str = arg.toString();
                if (str.length() > 50) {
                    sb.append(str.substring(0, 47)).append("...");
                } else {
                    sb.append(str);
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
