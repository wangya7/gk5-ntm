package wang.bannong.gk5.ntm.rpc.rpc;

import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.dto.DynamicDto;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 网关调用业务模块   单向
 * Created by bn. on 2019/8/12 10:57 AM
 */
@Slf4j
public final class NtmRpcClient {

    private static final String[] parameterTypeMap = new String[]{"java.util.Map"};
    private static final String[] parameterTypeDto = new String[]{"wang.bannong.gk5.ntm.common.dto.DynamicDto"};


    public static NtmResult $invoke(String serviceInterface, String method, String[] parameterType, Object[] args) {
        Object result = null;
        try {
            result = ReferenceConfigBuilder.getSynch(serviceInterface).get().$invoke(method, parameterType, args);
        } catch (GenericException e) {
            log.error("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                    serviceInterface, method, parameterType, args);
            log.error("=======> RPC(rsp): exception detail:", e);
            return NtmResult.of(ResultCode.rpc_invoke_exception);
        } catch (NtmException e) {
            log.error("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                    serviceInterface, method, parameterType, args);
            log.error("=======> RPC(rsp): exception detail:", e);
            return NtmResult.of(ResultCode.rpc_invoke_exception);
        }

        log.info("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                serviceInterface, method, parameterType, args);
        log.info("=======> RPC(rsp): result={}", result);

        HashMap<String, Object> map = (HashMap) result;
        NtmResult ntmResult = new NtmResult();
        ntmResult.setCode((int) map.get(ApiConfig.CODE));
        ntmResult.setSuccess((boolean) map.get(ApiConfig.SUCCESS));
        ntmResult.setMsg((String) map.get(ApiConfig.MSG));
        ntmResult.setData(map.get(ApiConfig.DATA));
        return ntmResult;
    }

    /**
     * RPC调用，采用默认的传参Map
     */
    public static NtmResult invoke(String serviceInterface, String method, Map<String, Object> args) {
        return $invoke(serviceInterface, method, parameterTypeMap, new Object[]{args});
    }

    /**
     * RPC调用，采用默认的传参Dto
     */
    public static NtmResult invoke(String serviceInterface, String method, DynamicDto args) {
        return $invoke(serviceInterface, method, parameterTypeDto, new Object[]{args});
    }

    public static void $invokeAsync(String serviceInterface, String method, String[] parameterType, Object[] args) {
        try {
            Object result = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invoke(method, parameterType, args);
            CountDownLatch latch = new CountDownLatch(1);
            CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
            future.whenComplete((value, t) -> {
                log.info("invokeSayHello(whenComplete): {}", value);
                latch.countDown();
            });
            log.info("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                    serviceInterface, method, parameterType, args);
            log.info("=======> RPC(rsp-direct): result={}", result);
            latch.await();
        } catch (GenericException e) {
            log.error("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                    serviceInterface, method, parameterType, args);
            log.error("=======> RPC(rsp): exception detail:", e);
        } catch (InterruptedException e) {
            log.error("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                    serviceInterface, method, parameterType, args);
            log.error("=======> RPC(rsp): exception detail:", e);
        } catch (NtmException e) {
            log.error("=======> RPC(req): serviceInterface={}, method={}, parameterType={}, args={}",
                    serviceInterface, method, parameterType, args);
            log.error("=======> RPC(rsp): exception detail:", e);
        }
    }

    /**
     * RPC调用，采用默认的传参Map
     */
    public static void invokeAsync(String serviceInterface, String method, Map<String, Object> args) {
        $invoke(serviceInterface, method, parameterTypeMap, new Object[]{args});
    }

    /**
     * RPC调用，采用默认的传参Dto
     */
    public static void invokeAsync(String serviceInterface, String method, DynamicDto args) {
        $invoke(serviceInterface, method, parameterTypeDto, new Object[]{args});
    }


    /******************/
    /******异步查询*****/
    /******************/

    public static void invokeAsync(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws InterruptedException {
        Object result = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invoke(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            log.info("invokeDoBizAsync(whenComplete): {}", value);
            latch.countDown();
        });

        log.info("invokeDoBizAsync(return): {}", result);
        latch.await();
    }

    public static void invokeAsyncDoBizAsync(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws Exception {
        CompletableFuture<Object> future = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invokeAsync(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((value, t) -> {
            log.info("invokeAsyncDoBizAsync(whenComplete): {}", value);
            latch.countDown();
        });
        latch.await();
    }

    public static void invokeAsyncDoBiz(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws Exception {
        CompletableFuture<Object> future = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invokeAsync(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((value, t) -> {
            log.info("invokeAsyncDoBiz(whenComplete): {}", value);
            latch.countDown();
        });
        latch.await();
    }

    public static void invokeDoBizAsyncComplex(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws Exception {
        Object result = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invoke(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            log.info("invokeDoBizAsyncComplex(whenComplete): {}", value);
            latch.countDown();
        });

        log.info("invokeDoBizAsync(return): {}", result);
        latch.await();
    }

    public static void asyncInvokeDoBizAsyncComplex(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws Exception {
        CompletableFuture<Object> future = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invokeAsync(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);

        future.whenComplete((value, t) -> {
            log.info("asyncInvokeDoBizAsyncComplex(whenComplete): {}", value);
            latch.countDown();
        });

        latch.await();
    }

    public static void invokeDoBizAsyncGenericComplex(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws Exception {
        Object result = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invoke(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            log.info("invokeDoBizAsyncGenericComplex(whenComplete): {}", value);
            latch.countDown();
        });

        log.info("invokeDoBizAsyncGenericComplex(return): {}", result);
        latch.await();
    }

    public static void asyncInvokeDoBizAsyncGenericComplex(String serviceInterface, String method, String[] parameterTypes, Object[] args) throws Exception {
        CompletableFuture<Object> future = ReferenceConfigBuilder.getAsynch(serviceInterface).get().$invokeAsync(method, parameterTypes, args);
        CountDownLatch latch = new CountDownLatch(1);

        future.whenComplete((value, t) -> {
            log.info("asyncInvokeDoBizAsyncGenericComplex(whenComplete): {}", value);
            latch.countDown();
        });

        latch.await();
    }
}
