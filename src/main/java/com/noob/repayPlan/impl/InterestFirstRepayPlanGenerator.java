package com.noob.repayPlan.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.noob.repayPlan.AbstractRepayPlanGenerator;
import com.noob.repayPlan.LoanParam;
import com.noob.repayPlan.RepayPlan;

/**
 * 按月先息后本
 * 
 */
public class InterestFirstRepayPlanGenerator extends AbstractRepayPlanGenerator {

	@Override
	public List<RepayPlan> calculate(LoanParam loanDto, Map<Date, Boolean> periodEndDateMap,
			BigDecimal defaultDivBase) {
		List<RepayPlan> planList = new ArrayList<>();
		int periodCount = periodEndDateMap.size(); // 总期数
		Date periodBeginDate = loanDto.getStartDate();
		BigDecimal amount = loanDto.getAmount();
		BigDecimal yearRate = loanDto.getApr();
		RoundingMode interestRoundingMode = loanDto.getInterestRoundingMode();

		BigDecimal distributedInterest = BigDecimal.ZERO;
		int curPeriod = 1;
		for (Entry<Date, Boolean> dateEntry : periodEndDateMap.entrySet()) {
			Date periodEndDate = dateEntry.getKey();
			Boolean isDayRate = dateEntry.getValue();

			int baseCount = isDayRate
					? calculateInterestDays(loanDto.isCalculateInterestFromNow(), periodBeginDate, periodEndDate)
					: 1;// 首期或指定 则用日利息计算。
			BigDecimal divBase = isDayRate && !RateBaseTypeEnum.useDayRate(loanDto.getAprBaseType())
					? RateBaseTypeEnum.DAYLY_365.getBase()
					: defaultDivBase; // 如果没指定按日计息则默认365

			BigDecimal capital = BigDecimal.ZERO;
			BigDecimal interest = BigDecimal.ZERO;
			if (curPeriod == periodCount) { // 最后一期
				capital = amount;
				if (loanDto.isEndComplementInterest()) {
					int totalBaseCount = calculateInterestDays(loanDto.isCalculateInterestFromNow(),
							loanDto.getStartDate(), loanDto.getEndDate());
					BigDecimal totalInterest = calculateInterest(
							RateBaseTypeEnum.useDayRate(loanDto.getAprBaseType()) ? defaultDivBase
									: RateBaseTypeEnum.DAYLY_365.getBase(),
							amount, yearRate, interestRoundingMode, totalBaseCount); // 按日利息计算实际总利息
					interest = totalInterest.subtract(distributedInterest);
				}
			}

			if (BigDecimal.ZERO.equals(interest)) {
				interest = calculateInterest(divBase, amount, yearRate, interestRoundingMode, baseCount);// 等额本金这的金额基数是剩余未还本金
			}

			if (interest.compareTo(BigDecimal.ZERO) < 0 || capital.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("还款计划生成异常");
			}
			planList.add(RepayPlan.init(loanDto.getLoanNo(), loanDto.getGraceDays(), curPeriod,
					periodEndDate, capital, interest, BigDecimal.ZERO.equals(capital) ? amount : BigDecimal.ZERO));

			periodBeginDate = periodEndDate; // 下一期的起息日
			curPeriod++;
			distributedInterest = distributedInterest.add(interest);
		}
		return planList;
	}

	@Override
	public String getRepayMode() {
		return RepayMode.SYS004;
	}

}
