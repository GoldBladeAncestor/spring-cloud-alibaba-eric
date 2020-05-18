package com.eric.alibabafeign;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * value：资源名称，必需项（不能为空）
 * entryType：entry 类型，可选项（默认为 EntryType.OUT）
 * blockHandler / blockHandlerClass: blockHandler 对应处理 BlockException 的函数名称，可选项。blockHandler 函数访问范围需要是 public，返回类型需要与原方法相匹配，参数类型需要和原方法相匹配并且最后加一个额外的参数，类型为 BlockException。blockHandler 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 blockHandlerClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
 * fallback / fallbackClass：fallback 函数名称，可选项，用于在抛出异常的时候提供 fallback 处理逻辑。fallback 函数可以针对所有类型的异常（除了 exceptionsToIgnore 里面排除掉的异常类型）进行处理。fallback 函数签名和位置要求：
 *      返回值类型必须与原函数返回值类型一致；
 *      方法参数列表需要和原函数一致，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
 * fallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
 * defaultFallback（since 1.6.0）：默认的 fallback 函数名称，可选项，通常用于通用的 fallback 逻辑（即可以用于很多服务或方法）。默认 fallback 函数可以针对所有类型的异常（除了 exceptionsToIgnore 里面排除掉的异常类型）进行处理。若同时配置了 fallback 和 defaultFallback，则只有 fallback 会生效。defaultFallback 函数签名要求：
 *      返回值类型必须与原函数返回值类型一致；
 *      方法参数列表需要为空，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
 * defaultFallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
 * exceptionsToIgnore（since 1.6.0）：用于指定哪些异常被排除掉，不会计入异常统计中，也不会进入 fallback 逻辑中，而是会原样抛出。
 */
@Service
public class SentinelService {
    private static final Logger logger = LoggerFactory.getLogger(SentinelService.class);

    private static final int PARAM_A = 1;
    private static final int PARAM_B = 2;
    private static final int PARAM_C = 3;
    private static final int PARAM_D = 4;

    @SentinelResource(value = "hello", blockHandler = "sentinelBlockHandler", fallback = "sentinelFallback")
    public String hello(String id){
        return "hello success";
    }

    @SentinelResource(value = "param", blockHandler = "sentinelBlockHandler", fallback = "sentinelFallback")
    public String param(String id){
        return "hello " + id;
    }

    @SentinelResource(value = "sentinel", blockHandler = "sentinelBlockHandler", fallback = "sentinelFallback")
    public String sentinel(String id){
        if ("0".equals(id)){
            throw new RuntimeException();
        }
        return "sentinel success";
    }
    //blockHandler 函数，原方法调用被限流/降级/系统保护的时候调用
    public String sentinelBlockHandler(String id, BlockException ex){
        logger.warn("sentinel block");
        return "sentinel block";
    }

    public String sentinelFallback(String id,Throwable throwable){
        logger.warn("sentinel fallback");
        return "sentinel fallback";
    }

    //熔断
    @PostConstruct
    private void initDegradeRule(){
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        //资源名，即限流规则的作用对象
        rule.setResource("sentinel");
        // 阈值5, 打到即进行熔断
        rule.setCount(5);
        //熔断策略，支持秒级 RT/秒级异常比例/分钟级异常数
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
        //降级的时间，单位为 s
        rule.setTimeWindow(10);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    //限流
    @PostConstruct
    private void initFlowRule(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        //资源名，资源名是限流规则的作用对象
        rule.setResource("sentinel");
        //限流阈值
        rule.setCount(2);
        //限流阈值类型，QPS 模式（1）或并发线程数模式（0）
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //流控针对的调用来源 ，默认default，代表不区分调用来源
        rule.setLimitApp("default");
        //调用关系限流策略：直接、链路、关联，more根据资源本身（直接）
//        rule.setStrategy()
        //流控效果（直接拒绝 / 排队等待 / 慢启动模式），不支持按调用关系限流，默认直接拒绝
//        rule.setControlBehavior()
        //是否集群限流，默认否
        rule.setClusterMode(Boolean.FALSE);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
    //系统保护规则 (SystemRule),注意系统规则只针对入口资源（EntryType=IN）生效
    @PostConstruct
    private void initSystemRule() {
        List<SystemRule> rules = new ArrayList<>();
        SystemRule rule = new SystemRule();
        //load触发值，用于触发自适应控制阶段.-1不生效
        rule.setHighestSystemLoad(10);
        //所有入口流量的平均响应时间.-1不生效
        rule.setAvgRt(-1);
        //入口流量的最大并发数.-1不生效
        rule.setMaxThread(-1);
        //所有入口资源的 QPS.-1不生效
        rule.setQps(-1);
        //当前系统的 CPU 使用率（0.0-1.0）.-1不生效
        rule.setHighestCpuUsage(-1);
        rules.add(rule);
        SystemRuleManager.loadRules(rules);
    }

    //访问控制规则 (AuthorityRule)
    @PostConstruct
    private void initAuthorityRule(){
        List<AuthorityRule> rules = new ArrayList<>();
        AuthorityRule rule = new AuthorityRule() ;
        //资源名，即限流规则的作用对象
        rule.setResource("sentinel");
        //对应的黑名单/白名单，不同 origin 用 , 分隔，如 appA,appB
        rule.setLimitApp("appA,appB");
        //限制模式，AUTHORITY_WHITE 为白名单模式，AUTHORITY_BLACK 为黑名单模式，默认为白名单模式
        rule.setStrategy(RuleConstant.AUTHORITY_BLACK);
        rules.add(rule);
        AuthorityRuleManager.loadRules(rules);
    }

    //热点参数限流
    @PostConstruct
    private void initParamFlowRule(){
        ParamFlowRule rule = new ParamFlowRule();
        rule.setResource("param");
        //热点参数的索引，必填，对应 SphU.entry(xxx, args) 中的参数索引位置
        rule.setParamIdx(0);
        //限流阈值，必填
        rule.setCount(5);
        //限流模式
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        //统计窗口时间长度（单位为秒）,1.6.0 版本开始支持,默认1s
        rule.setDurationInSec(1);
        //流控效果（支持快速失败和匀速排队模式），1.6.0 版本开始支持,默认快速失败
//        rule.setControlBehavior();
        //最大排队等待时长（仅在匀速排队模式生效），1.6.0 版本开始支持,默认0s
//        rule.setMaxQueueingTimeMs();

        // 针对 int 类型的参数 PARAM_B，单独设置限流 QPS 阈值为 10，而不是全局的阈值 5.
        ParamFlowItem item = new ParamFlowItem().setObject(String.valueOf(PARAM_B))
                .setClassType(int.class.getName())
                .setCount(10);
        //参数例外项，可以针对指定的参数值单独设置限流阈值，不受前面 count 阈值的限制。仅支持基本类型和字符串类型
        rule.setParamFlowItemList(Collections.singletonList(item));

        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}
