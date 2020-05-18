package com.eric.alibabaribbon;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;

public class HelloServiceCommand extends HystrixObservableCommand<String> {

    protected HelloServiceCommand(HystrixCommandGroupKey group) {
        super(group);
    }

    protected HelloServiceCommand(Setter setter) {
        super(setter);
    }

    @Override
    protected Observable<String> construct() {
        return null;
    }
}
