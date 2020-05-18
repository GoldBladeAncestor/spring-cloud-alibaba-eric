package com.eric.alibabafeign;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

public class DataSourceInitFunc implements InitFunc {
    @Override
    public void init() throws Exception {
        final String remoteAddress = "localhost";
        final String groupId = "DEFAULT_GROUP";
        final String dataId = "nacos-config";

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    public static void main(String[] args) throws Exception {
        DataSourceInitFunc func = new DataSourceInitFunc();
        func.init();
    }
}
