package com.noob.repayPlan.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.noob.repayPlan.AbstractRepayPlanGenerator;
import com.noob.repayPlan.LoanParam;
import com.noob.repayPlan.RepayPlan;

/**
 * 等额本金
 * <p>
 * 先均分本金， 按剩余本金计算每月利息
 * 
 */
public class AverageCapitalRepayPlanGenerator extends AbstractRepayPlanGenerator {

	@Override
	public List<RepayPlan> calculate(LoanParam loanDto, Map<Date, Boolean> periodEndDateMap,
			BigDecimal defaultDivBase) {
		List<RepayPlan> planList = new ArrayList<>();
		int periodCount = periodEndDateMap.size(); // 总期数
		Date periodBeginDate = loanDto.getStartDate();
		BigDecimal amount = loanDto.getAmount();
		BigDecimal yearRate = loanDto.getApr();

		int curPeriod = 1;
		BigDecimal distributedCapital = BigDecimal.ZERO;

		BigDecimal periodCapital = amount.divide(BigDecimal.valueOf(periodCount), 2, loanDto.getCapitalRoundingMode()); // 每期应还本金

		for (Entry<Date, Boolean> dateEntry : periodEndDateMap.entrySet()) {
			Date periodEndDate = dateEntry.getKey();
			Boolean isDayRate = dateEntry.getValue();

			BigDecimal calculateAmount = amount.subtract(distributedCapital); // 计息本金
			BigDecimal capital = curPeriod == periodCount ? calculateAmount : periodCapital;

			int baseCount = isDayRate
					? calculateInterestDays(loanDto.isCalculateInterestFromNow(), periodBeginDate, periodEndDate)
					: 1;// 首期或指定 则用日利息计算。
			BigDecimal divBase = isDayRate && !RateBaseTypeEnum.useDayRate(loanDto.getAprBaseType())
					? RateBaseTypeEnum.DAYLY_365.getBase()
					: defaultDivBase; // 如果没指定按日计息则默认365

			BigDecimal interest = calculateInterest(divBase, calculateAmount, yearRate,
					loanDto.getInterestRoundingMode(), baseCount);

			BigDecimal remainingPrincipal = calculateAmount.subtract(capital);

			if (interest.compareTo(BigDecimal.ZERO) < 0 || capital.compareTo(BigDecimal.ZERO) < 0
					|| remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("还款计划生成异常");
			}
			planList.add(RepayPlan.init(loanDto.getLoanNo(), loanDto.getGraceDays(), curPeriod, periodEndDate,
					capital, interest, remainingPrincipal));

			periodBeginDate = periodEndDate; // 下一期的起息日
			curPeriod++;
			distributedCapital = distributedCapital.add(capital);
		}
		return planList;
	}

	@Override
	public String getRepayMode() {
		return RepayMode.SYS003;
	}
}
