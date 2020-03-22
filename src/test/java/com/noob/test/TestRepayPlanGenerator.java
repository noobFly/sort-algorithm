package com.noob.test;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.noob.repayPlan.LoanParam;
import com.noob.repayPlan.RepayPlan;
import com.noob.repayPlan.RepayPlanGenerator.RateBaseTypeEnum;
import com.noob.repayPlan.impl.AverageCapitalAndInterestRepayPlanGenerator;
import com.noob.repayPlan.impl.AverageCapitalPlusInterestRepayPlanGenerator;
import com.noob.repayPlan.impl.AverageCapitalRepayPlanGenerator;
import com.noob.repayPlan.impl.InterestFirstRepayPlanGenerator;
import com.noob.repayPlan.impl.OneTimeRepayPlanGenerator;

public class TestRepayPlanGenerator {

	public static void oneTimeRepaymentPlanGeneratorTest() throws ParseException {
		LoanParam dto = buildParam();
		dto.setAprBaseType(RateBaseTypeEnum.DAYLY_365.getType());
		print(new OneTimeRepayPlanGenerator().handle(dto));

	}

	public static void interestFirstRepaymentPlanGeneratorTest() throws ParseException {
		LoanParam dto = buildParam();
		dto.setAprBaseType(RateBaseTypeEnum.DAYLY_365.getType());
		print(new InterestFirstRepayPlanGenerator().handle(dto));

	}

	public static void averageCapitalPlusInterestRepayPlanGeneratorTest() throws ParseException {
		LoanParam dto = buildParam();
		print(new AverageCapitalPlusInterestRepayPlanGenerator().handle(dto));

	}

	public static void averageCapitalAndInterestRepayPlanGeneratorTest() throws Exception {
		LoanParam dto = buildParam();
		dto.setAprBaseType(RateBaseTypeEnum.DAYLY_365.getType());
		print(new AverageCapitalAndInterestRepayPlanGenerator().handle(dto));

	}

	public static void averageCapitalRepayPlanGeneratorTest() throws Exception {
		LoanParam dto = buildParam();
		dto.setAprBaseType(RateBaseTypeEnum.DAYLY_365.getType());
		print(new AverageCapitalRepayPlanGenerator().handle(dto));

	}

	private static LoanParam buildParam() throws ParseException {
		LoanParam dto = new LoanParam();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		dto.setStartDate(df.parse("2020-02-21"));
		dto.setEndDate(df.parse("2020-08-21"));
		dto.setTotalPeriod(6);
		dto.setAmount(new BigDecimal("5000"));
		// dto.setPeriodMinDay(25);
		// dto.setRepaymentDay(20);
		dto.setApr(new BigDecimal("36"));
		dto.setLoanNo("testLoan123456");
		dto.setGraceDays(2);

		return dto;
	}

	private static void print(List<RepayPlan> handle) {
		System.out.println(JSON.toJSONStringWithDateFormat(handle, "yyyy-MM-dd"));
	}

	public static void main(String[] args) throws Exception {
		try {
			oneTimeRepaymentPlanGeneratorTest();
			interestFirstRepaymentPlanGeneratorTest();
			averageCapitalPlusInterestRepayPlanGeneratorTest();
			averageCapitalAndInterestRepayPlanGeneratorTest();
			averageCapitalRepayPlanGeneratorTest();

		} catch (Exception e) {
			throw e;
		}
	}
}
