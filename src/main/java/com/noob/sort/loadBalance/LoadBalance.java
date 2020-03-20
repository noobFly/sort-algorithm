package com.noob.sort.loadBalance;

import java.util.List;

import com.noob.sort.loadBalance.impl.ServiceInvoker;

public interface LoadBalance {

	ServiceInvoker select(List<ServiceInvoker> invokers, Object[] parameters);

}