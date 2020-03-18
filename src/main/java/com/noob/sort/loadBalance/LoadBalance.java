package com.noob.sort.loadBalance;

import java.util.List;

import com.noob.sort.loadBalance.spi.ServiceInvoker;

@SPI
public interface LoadBalance {

	ServiceInvoker select(List<ServiceInvoker> invokers, Object[] parameters);

}