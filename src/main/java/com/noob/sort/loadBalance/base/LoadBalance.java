package com.noob.sort.loadBalance.base;

import java.util.List;

import com.noob.sort.loadBalance.ServiceInvoker;

@SPI
public interface LoadBalance {

	ServiceInvoker select(List<ServiceInvoker> invokers, Object[] parameters);

}