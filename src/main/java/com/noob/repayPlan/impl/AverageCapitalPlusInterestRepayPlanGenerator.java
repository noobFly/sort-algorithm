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
 * 等额本息 （每期还款金额相同）
 * <p>
 * 等比数列求和: S = a1 * (1 - q ^ n ) / (1- q)
 * <p>
 * 假设贷款总金额为A，月利率为β，贷款期数为k，每期需还款总金额（本金+利息）为x，则各个月所欠银行贷款为：
 * <p>
 * 第一个月A(1+β)-X
 * <p>
 * 第二个月(A(1+β)-X)(1+β)-X=A(1+β)^2-X[1+(1+β)]
 * <p>
 * 第三个月[A(1+β)-X)(1+β)-X](1+β)-X =A(1+β)^3-X[1+(1+β)+(1+β)^2]
 * <p>
 * …
 * <p>
 * 由此可得第k个月后所欠银行贷款为 A(1+β)^n –X[1+(1+β)+(1+β)^2+…+(1+β)^(k-1)]= A(1+β)^k
 * –X[(1+β)^k - 1]/β = 0
 * <p>
 * 推得： 每期还款本息总额 x = A * β * (1 + β) ^ k / [(1 + β) ^ k - 1]
 * <p>
 * 先计算出每月应还总金额（不变）, 再用剩余总本金*月利率得到每月应还利息。 差额就是每月还款本金
 * 
 */
public class AverageCapitalPlusInterestRepayPlanGenerator extends AbstractRepayPlanGenerator {

	@Override
	public List<RepayPlan> calculate(LoanParam loanDto, Map<Date, Boolean> periodEndDateMap,
			BigDecimal defaultDivBase) {
		List<RepayPlan> planList = new ArrayList<>();
		int periodCount = periodEndDateMap.size(); // 总期数
		Date periodBeginDate = loanDto.getStartDate();
		BigDecimal amount = loanDto.getAmount();
		BigDecimal yearRate = loanDto.getApr();

		BigDecimal periodRate = yearRate.divide(BigDecimal.valueOf(12 * 100), CALCULATE_SCALE, RoundingMode.DOWN);
		BigDecimal periodRepayAmount = getPeriodRepayAmount(loanDto.getAmount(), periodRate, periodCount,
				loanDto.getPeriodAmountRoundingMode()); // 计算每个周期的还款金额
		BigDecimal distributedCapital = BigDecimal.ZERO;
		int curPeriod = 1;
		for (Entry<Date, Boolean> dateEntry : periodEndDateMap.entrySet()) {
			Date periodEndDate = dateEntry.getKey();
			Boolean isDayRate = dateEntry.getValue();

			BigDecimal calculateAmount = amount.subtract(distributedCapital); // 计息本金
			BigDecimal interest = BigDecimal.ZERO;
			BigDecimal capital = BigDecimal.ZERO;

			if (curPeriod == periodCount) {
				capital = calculateAmount;
				interest = periodRepayAmount.subtract(capital);
			} else {
				int baseCount = isDayRate
						? calculateInterestDays(loanDto.isCalculateInterestFromNow(), periodBeginDate, periodEndDate)
						: 1;// 首期或指定 则用日利息计算。

				BigDecimal divBase = isDayRate && !RateBaseTypeEnum.useDayRate(loanDto.getAprBaseType())
						? RateBaseTypeEnum.DAYLY_365.getBase()
						: defaultDivBase; // 如果没指定按日计息则默认365

				interest = calculateInterest(divBase, calculateAmount, yearRate, loanDto.getInterestRoundingMode(),
						baseCount);
				capital = periodRepayAmount.subtract(interest); // 先收剩余本金利息，后收本金
			}

			BigDecimal remainingPrincipal = calculateAmount.subtract(capital);

			if (interest.compareTo(BigDecimal.ZERO) < 0 || capital.compareTo(BigDecimal.ZERO) < 0
					|| remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("还款计划生成异常");
			}

			planList.add(RepayPlan.init(loanDto.getLoanNo(), loanDto.getGraceDays(), curPeriod, periodEndDate, capital,
					interest, remainingPrincipal));

			periodBeginDate = periodEndDate; // 下一期的起息日
			curPeriod++;
			distributedCapital = distributedCapital.add(capital);
		}
		return planList;
	}

	private static BigDecimal getPeriodRepayAmount(BigDecimal amount, BigDecimal apr, int periods,
			RoundingMode roundingMode) {
		double aprPow = Math.pow(1 + apr.doubleValue(), periods);
		double period = 1;
		if (aprPow > 1) {
			period = aprPow - 1;
		}
		return amount.multiply(apr).multiply(BigDecimal.valueOf(aprPow)).divide(BigDecimal.valueOf(period), 2,
				roundingMode);
	}

	@Override
	public String getRepayMode() {
		return RepayMode.SYS002;
	}

	public static void main(String[] args) {
		System.out.println(getPeriodRepayAmount(new BigDecimal("5000"),
				new BigDecimal("36").divide(BigDecimal.valueOf(12 * 100), 48, RoundingMode.DOWN), 6,
				RoundingMode.HALF_UP));
		System.out.println(new BigDecimal("5000")
				.multiply(new BigDecimal("36").divide(BigDecimal.valueOf(12 * 100), 48, RoundingMode.DOWN)));
	}

}
