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
 * 等本等息
 * <p>
 * 本金与利息 都不变
 * <p>
 * 与等额本金基本相同，唯一区别是计算利息时，本金始终等于总本金
 * <p>
 * 最终利息与一次性还本付息、先息后本的利息额相同（分期会因为每期尾数抛弃而略有差异）
 * <p>
 * 
 */

public class AverageCapitalAndInterestRepayPlanGenerator extends AbstractRepayPlanGenerator {

	@Override
	public List<RepayPlan> calculate(LoanParam loanDto, Map<Date, Boolean> periodEndDateMap,
			BigDecimal defaultDivBase) {
		List<RepayPlan> planList = new ArrayList<>();
		int periodCount = periodEndDateMap.size(); // 总期数
		Date periodBeginDate = loanDto.getStartDate();
		BigDecimal amount = loanDto.getAmount();
		BigDecimal yearRate = loanDto.getApr();
		RoundingMode interestRoundingMode = loanDto.getInterestRoundingMode();

		int curPeriod = 1;
		BigDecimal distributedCapital = BigDecimal.ZERO;
		BigDecimal distributedInterest = BigDecimal.ZERO;

		BigDecimal periodCapital = amount.divide(BigDecimal.valueOf(periodCount), 2, loanDto.getCapitalRoundingMode()); // 每期应还本金

		for (Entry<Date, Boolean> dateEntry : periodEndDateMap.entrySet()) {
			Date periodEndDate = dateEntry.getKey();
			Boolean isDayRate = dateEntry.getValue();

			BigDecimal calculateAmount = amount.subtract(distributedCapital); // 计息本金

			int baseCount = isDayRate
					? calculateInterestDays(loanDto.isCalculateInterestFromNow(), periodBeginDate, periodEndDate)
					: 1;// 首期或指定 则用日利息计算。
			BigDecimal divBase = isDayRate && !RateBaseTypeEnum.useDayRate(loanDto.getAprBaseType())
					? RateBaseTypeEnum.DAYLY_365.getBase()
					: defaultDivBase; // 如果没指定按日计息则默认365

			BigDecimal capital = BigDecimal.ZERO;
			BigDecimal interest = BigDecimal.ZERO;
			if (curPeriod == periodCount) { // 最后一期
				capital = calculateAmount;
				if (loanDto.isEndComplementInterest()) {
					int totalBaseCount = calculateInterestDays(loanDto.isCalculateInterestFromNow(),
							loanDto.getStartDate(), loanDto.getEndDate());
					BigDecimal totalInterest = calculateInterest(
							RateBaseTypeEnum.useDayRate(loanDto.getAprBaseType()) ? defaultDivBase
									: RateBaseTypeEnum.DAYLY_365.getBase(),
							amount, yearRate, interestRoundingMode, totalBaseCount); // 按日利息计算实际总利息
					interest = totalInterest.subtract(distributedInterest);
				}
			} else {
				capital = periodCapital;
			}
			if (BigDecimal.ZERO.equals(interest)) {
				interest = calculateInterest(divBase, amount, yearRate, interestRoundingMode, baseCount);// 等额本金这的金额基数是剩余未还本金
			}

			BigDecimal remainingPrincipal = calculateAmount.subtract(capital);

			if (interest.compareTo(BigDecimal.ZERO) < 0 || capital.compareTo(BigDecimal.ZERO) < 0
					|| remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("还款计划生成异常");
			}

			planList.add(RepayPlan.init(loanDto.getLoanNo(), loanDto.getGraceDays(), curPeriod,
					periodEndDate, capital, interest, remainingPrincipal));

			periodBeginDate = periodEndDate; // 下一期的起息日
			curPeriod++;
			distributedCapital = distributedCapital.add(capital);
			distributedInterest = distributedInterest.add(interest);

		}
		return planList;
	}

	@Override
	public String getRepayMode() {
		return RepayMode.SYS005;
	}
}
